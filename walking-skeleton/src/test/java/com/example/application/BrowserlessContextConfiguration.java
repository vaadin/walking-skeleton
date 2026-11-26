package com.example.application;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.SpringBrowserlessApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Defines the browserless application context that all browserless tests share. Spring creates it once, together with
 * the Spring application context, and closes it when the Spring application context closes.
 */
@Configuration
class BrowserlessContextConfiguration {

    @Bean
    BrowserlessApplicationContext browserlessApplicationContext(ApplicationContext applicationContext) {
        // Scans the package of Application and its sub-packages for routes
        return SpringBrowserlessApplicationContext.create(applicationContext, Application.class);
    }
}
