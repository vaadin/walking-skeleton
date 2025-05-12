package com.example.application;

import org.springframework.boot.SpringApplication;

/**
 * Run this application class to use Testcontainers for all your external services. This is convenient during
 * development as you don't have to have your database or message broker running locally. They will instead start
 * up in temporary Docker containers when you start the application.
 */
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
    }
}
