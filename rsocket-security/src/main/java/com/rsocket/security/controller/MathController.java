package com.rsocket.security.controller;


import com.rsocket.security.dto.LoginRequest;
import com.rsocket.security.repo.UserRepository;
import com.rsocket.security.security.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Controller
@MessageMapping("math")
public class MathController {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public MathController(UserRepository userRepository, JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @MessageMapping("login")
    public Mono<String> login(Mono<LoginRequest> requestMono) {
        return requestMono.doOnNext(r -> log.info("Login Request: {}", r))
                .flatMap(r -> Mono.fromSupplier(() -> userRepository.loadUserByUsername(r.username()))
                        .filter(ud -> passwordEncoder.matches(r.password(), ud.getPassword())))
                .map(ud -> jwtTokenUtil.generateToken(ud.getUsername()))
                .doOnNext(s -> log.info("Generated Token: {}", s));
    }

    @MessageMapping("square")
    public Mono<Integer> square(@AuthenticationPrincipal UserDetails userDetails, Mono<Integer> requestMono, @Header("request-id") UUID requestId) {
        return requestMono.doOnNext(r -> log.info("Square Request: {} For User: {} With RequestId: {}", r, userDetails, requestId))
                .map(r -> r * r);
    }

    @MessageMapping("cube")
    public Mono<Integer> cube(Mono<Integer> requestMono) {
        return requestMono.doOnNext(r -> log.info("Cube Request: {}", r))
                .map(r -> r * r * r);
    }

}
