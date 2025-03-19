package org.vaadin.walkingskeleton.generator;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme("default")
public class Application implements AppShellConfigurator {

    private final String contextPath;

    public Application(@Value("${walking-skeleton.generator.context-path:}") String contextPath) {
        this.contextPath = contextPath;
        LoggerFactory.getLogger(Application.class).info("Using generator context path '{}'", contextPath);
    }

    @Override
    public void configurePage(AppShellSettings settings) {
        AppShellConfigurator.super.configurePage(settings);
        settings.addMetaTag("context-path", contextPath);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
