package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.util.Arrays;

/**
 * Main Spring Boot application class for the Vaadin application.
 *
 * <h2>Automatic Development Profile Activation</h2> This application automatically activates the "dev" Spring profile
 * when:
 * <ul>
 * <li>Vaadin is running in development mode (vaadin-dev-server dependency is present)</li>
 * <li>No Spring profiles have been explicitly configured</li>
 * </ul>
 *
 * <p>
 * If you need to use explicit profile management, remove the custom initializer from the {@link #main(String[])} method
 * and use standard Spring profile activation:
 * </p>
 *
 * <!-- spotless:off-->
 * <pre>
 * {@code
 * // Replace the current main method with:
 * public static void main(String[] args) {
 *     SpringApplication.run(Application.class, args);
 * }
 *
 * // Then use explicit profile activation:
 * java -Dspring.profiles.active=prod -jar your-app.jar
 * // or set SPRING_PROFILES_ACTIVE environment variable
 * }
 * </pre>
 * <!-- spotless:on -->
 *
 * @see #enableDevelopmentModeIfNeeded(ConfigurableApplicationContext)
 */
@SpringBootApplication
@Theme("default")
public class Application implements AppShellConfigurator {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone(); // You can also use Clock.systemUTC()
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).initializers(Application::enableDevelopmentModeIfNeeded)
                .run(args);
    }

    /**
     * Conditionally activates the "dev" Spring profile based on Vaadin's runtime mode.
     *
     * <p>
     * This method is called during Spring context initialization and will activate the "dev" profile if and only if:
     * </p>
     * <ul>
     * <li>No Spring profiles are already active (excluding the default "default" profile)</li>
     * <li>Vaadin is detected to be running in development mode</li>
     * </ul>
     *
     * <p>
     * <strong>Note:</strong> If you prefer explicit profile management, remove this initializer from the main method
     * and use standard Spring profile activation methods.
     * </p>
     *
     * @param context
     *            the Spring application context being initialized
     * @see #isVaadinInDevelopmentMode()
     */
    private static void enableDevelopmentModeIfNeeded(ConfigurableApplicationContext context) {
        var environment = context.getEnvironment();

        // Check if any profiles are already explicitly set
        var activeProfiles = environment.getActiveProfiles();
        var defaultProfiles = environment.getDefaultProfiles();

        var hasExplicitProfiles = activeProfiles.length > 0
                || (defaultProfiles.length > 0 && !Arrays.equals(defaultProfiles, new String[] { "default" }));

        if (!hasExplicitProfiles && isVaadinInDevelopmentMode()) {
            LoggerFactory.getLogger(Application.class).warn("Automatically enabling the DEVELOPMENT profile");
            environment.setActiveProfiles("dev");
        }
    }

    /**
     * Determines if Vaadin is running in development mode by checking for the presence of development-specific classes
     * in the classpath.
     *
     * <p>
     * This method uses classpath inspection rather than system properties because it's more reliable - the
     * vaadin-dev-server dependency should only be present during development builds, not in production deployments.
     * </p>
     *
     * @return {@code true} if Vaadin development mode is detected, {@code false} otherwise
     */
    private static boolean isVaadinInDevelopmentMode() {
        try {
            // This class is in the 'vaadin-dev-server' dependency, which should be in the classpath when running
            // in development mode, but not when running in production mode.
            Class.forName("com.vaadin.base.devserver.ServerInfo");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}