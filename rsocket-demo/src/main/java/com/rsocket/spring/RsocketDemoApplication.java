package com.rsocket.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RsocketDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RsocketDemoApplication.class, args);
	}

}
