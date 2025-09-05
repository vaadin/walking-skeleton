package com.example.application;

import org.springframework.boot.SpringApplication;

public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                // TODO Add additional test configurations here, such as Testcontainers
                .run(args);
    }
}
