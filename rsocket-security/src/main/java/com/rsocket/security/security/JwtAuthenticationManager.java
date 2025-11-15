package com.rsocket.security.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.rsocket.authentication.AuthenticationPayloadInterceptor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final ReactiveUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationManager(ReactiveUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("Authentication Request: {}", authentication);
        String token = authentication.getCredentials().toString();
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return userDetailsService.findByUsername(username)
                .filter(u -> jwtTokenUtil.validateToken(token, u))
                .<Authentication>map(ud -> new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities()))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Wrong Credentials")));
    }
}
