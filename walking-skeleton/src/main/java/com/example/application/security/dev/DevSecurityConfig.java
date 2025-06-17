package com.example.application.security.dev;

import com.example.application.security.controlcenter.ControlCenterSecurityConfig;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Security configuration for the development environment.
 * <p>
 * This configuration simplifies authentication during development by:
 * <ul>
 * <li>Using a simple login view for authentication</li>
 * <li>Providing predefined test users with fixed credentials</li>
 * <li>Using an in-memory user details service with no external dependencies</li>
 * </ul>
 * </p>
 * <p>
 * This configuration is automatically activated when {@link ControlCenterSecurityConfig} is not active. It should
 * <strong>not</strong> be used in production environments, as it uses hardcoded credentials and simplified security
 * settings.
 * </p>
 * <p>
 * The predefined users are declared in the {@link SampleUsers} class.
 * </p>
 * <p>
 * This configuration integrates with Vaadin's security framework through {@link VaadinSecurityConfigurer} to provide a
 * seamless login experience in the Vaadin UI.
 * </p>
 *
 * @see DevUserDetailsService The in-memory user details service implementation
 * @see DevUser Builder for creating development test users
 * @see SampleUsers User credentials for the predefined users
 */
@EnableWebSecurity
@Configuration
@Import({ VaadinAwareSecurityContextHolderStrategyConfiguration.class })
@ConditionalOnMissingBean(ControlCenterSecurityConfig.class)
class DevSecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(DevSecurityConfig.class);

    DevSecurityConfig() {
        log.warn("Using DEVELOPMENT security configuration. This should not be used in production environments!");
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.formLogin(Customizer.withDefaults())
                .with(VaadinSecurityConfigurer.vaadin(), Customizer.withDefaults())
                .addFilterBefore(vaadinLoginPageFilter(), UsernamePasswordAuthenticationFilter.class).build();
    }

    /**
     * Creates a servlet filter that injects the Vaadin refresh comment into Spring Security's default login page
     * response.
     *
     * <p>
     * This filter is necessary because when a Vaadin application performs a logout operation, it expects either a JSON
     * response or an HTML response containing the special comment {@code <!-- Vaadin-Refresh -->}. Without this
     * comment, Vaadin's client-side code doesn't recognize that the user has been logged out and fails to refresh the
     * page to show the login form.
     * </p>
     *
     * <p>
     * The filter intercepts requests to {@code /login}, captures Spring Security's generated HTML login page, and
     * injects the required Vaadin comment immediately after the opening {@code <body>} tag. This allows the application
     * to use Spring Security's auto-generated login page during development while maintaining compatibility with
     * Vaadin's logout handling.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> This is intended for development use only. Production deployments should use OIDC
     * authentication which handles logout properly without requiring this workaround.
     * </p>
     *
     * @return a Filter that modifies login page responses to include the Vaadin refresh comment
     * @see HttpServletResponseWrapper
     */
    private Filter vaadinLoginPageFilter() {
        return (request, response, chain) -> {
            var httpRequest = (HttpServletRequest) request;
            if (httpRequest.getRequestURI().equals("/login")) {
                var stringWriter = new StringWriter();
                var wrapper = new HttpServletResponseWrapper((HttpServletResponse) response) {
                    private final PrintWriter printWriter = new PrintWriter(stringWriter);

                    @Override
                    public PrintWriter getWriter() {
                        return printWriter;
                    }
                };
                chain.doFilter(request, wrapper);
                var html = stringWriter.toString();
                if (html.contains("<body>")) {
                    html = html.replace("<body>", "<body><!-- Vaadin-Refresh -->");
                }
                response.setContentLength(html.length());
                response.getWriter().write(html);
            } else {
                chain.doFilter(request, response);
            }
        };
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new DevUserDetailsService(SampleUsers.ALL_USERS);
    }

    @Bean
    VaadinServiceInitListener productionModeGuard() {
        return (serviceInitEvent) -> {
            if (serviceInitEvent.getSource().getDeploymentConfiguration().isProductionMode()) {
                throw new IllegalStateException(
                        "Development profile is active but Vaadin is running in production mode. This indicates a configuration error - development profile should not be used in production.");
            }
        };
    }
}