package com.rsocket.security.repo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Slf4j
@Repository
public class UserRepository {

    private final PasswordEncoder passwordEncoder;
    private Map<String, UserDetails> db;

    public UserRepository(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        db = Map.of(
                "user", User.withUsername("user").password(passwordEncoder.encode("user")).build(),
                "admin", User.withUsername("admin").password(passwordEncoder.encode("admin")).build()
        );
    }

    public UserDetails loadUserByUsername(String username) {
        log.info("Loading user by username {}", username);
        return db.get(username);
    }
}
