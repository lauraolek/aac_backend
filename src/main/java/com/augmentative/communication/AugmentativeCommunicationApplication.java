package com.augmentative.communication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class AugmentativeCommunicationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AugmentativeCommunicationApplication.class, args);
    }

}
