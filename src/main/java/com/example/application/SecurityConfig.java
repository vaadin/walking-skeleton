package com.example.application;

import com.vaadin.controlcenter.starter.idm.IdentityManagementConfiguration;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@Import({ VaadinAwareSecurityContextHolderStrategyConfiguration.class })
@ConditionalOnMissingBean(IdentityManagementConfiguration.class)
class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.warn("╔═════════════════════════════════════════════════╗");
        log.warn("║                SECURITY DISABLED                ║");
        log.warn("║ Anyone is permitted to access the application.  ║");
        log.warn("╚═════════════════════════════════════════════════╝");

        // TODO Configure application security. See https://vaadin.com/docs/latest/building-apps/security for details.
        // To remove Spring Security completely:
        // - delete this class
        // - remove the com.vaadin:control-center-starter dependency from pom.xml
        // - remove the org.springframework.boot:spring-boot-starter-security dependency from pom.xml
        return http
                .with(VaadinSecurityConfigurer.vaadin(),
                        configurer -> configurer.anyRequest(AuthorizeHttpRequestsConfigurer.AuthorizedUrl::permitAll))
                .build();
    }
}
