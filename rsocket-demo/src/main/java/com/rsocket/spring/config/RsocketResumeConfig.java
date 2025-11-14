package com.rsocket.spring.config;

import io.rsocket.core.Resume;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RsocketResumeConfig {

    @Bean
    public RSocketServerCustomizer rSocketServerCustomizer() {
        return c -> c.resume(resume());
    }

    private Resume resume() {
        return new Resume().sessionDuration(Duration.ofMinutes(5));
    }
}
