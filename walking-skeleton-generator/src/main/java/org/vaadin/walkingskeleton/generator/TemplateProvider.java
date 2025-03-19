package org.vaadin.walkingskeleton.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
class TemplateProvider {

    private final URL flowTemplate;
    private final URL reactTemplate;

    public TemplateProvider(@Value("${walking-skeleton.templates.flow.url}") URL flowTemplate,
                            @Value("${walking-skeleton.templates.react.url}") URL reactTemplate) {
        this.flowTemplate = flowTemplate;
        this.reactTemplate = reactTemplate;

        verifyValidURL(flowTemplate);
        verifyValidURL(reactTemplate);
    }

    private void verifyValidURL(URL url) {
        try {
            url.openStream().close();
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot access " + url);
        }
    }

    Path loadTemplateIntoTemporaryDirectory(UIFramework uiFramework) throws IOException {
        // Download archive
        var tempZip = Files.createTempFile("walking-skeleton-", ".zip");
        try (var is = getTemplateUrl(uiFramework).openStream()) {
            Files.copy(is, tempZip, StandardCopyOption.REPLACE_EXISTING);
        }
        try {
            // Extract the archive
            var tempDir = Files.createTempDirectory("walking-skeleton-");
            ZipUtils.extractZipIntoDirectory(tempZip, tempDir);
            return tempDir;
        } finally {
            // Delete the downloaded archive
            Files.delete(tempZip);
        }
    }

    private URL getTemplateUrl(UIFramework uiFramework) {
        return switch (uiFramework) {
            case FLOW -> flowTemplate;
            case REACT -> reactTemplate;
        };
    }
}
