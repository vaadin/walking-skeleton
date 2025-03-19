package org.vaadin.walkingskeleton.generator;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Component
class ProjectGenerator {

    final TemplateProvider templateProvider;

    ProjectGenerator(TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    Path generateProjectDirectory(ProjectConfiguration projectConfiguration) throws IOException {
        var directory = templateProvider.loadTemplateIntoTemporaryDirectory(projectConfiguration.uiFramework());
        updatePomFile(directory, projectConfiguration);
        updateBasePackage(directory, projectConfiguration);
        renameRootDirectory(directory, projectConfiguration);
        return directory;
    }

    private void updatePomFile(Path projectDirectory, ProjectConfiguration projectConfiguration) throws IOException {
        var pomFile = projectDirectory.resolve("walking-skeleton/pom.xml");
        FileUtils.replaceInFile(pomFile, Map.of("com.example.application", projectConfiguration.groupId().toString(), "walking-skeleton", projectConfiguration.artifactId().toString()));
    }

    private void updateBasePackage(Path projectDirectory, ProjectConfiguration projectConfiguration) throws IOException {
        if (projectConfiguration.basePackage().toString().equals("com.example.application")) {
            return; // No need to change the base package
        }

        var mainJava = projectDirectory.resolve("walking-skeleton/src/main/java");
        moveJavaClasses(mainJava, projectConfiguration.basePackage());

        var testJava = projectDirectory.resolve("walking-skeleton/src/test/java");
        moveJavaClasses(testJava, projectConfiguration.basePackage());

        FileUtils.replaceInFiles(projectDirectory, FileUtils.nameEndsWith(".java", ".properties"), Map.of("com.example.application", projectConfiguration.basePackage().toString()));
        FileUtils.replaceInFiles(projectDirectory, FileUtils.nameEndsWith(".tsx"), Map.of("Frontend/generated/com/example/application", "Frontend/generated/" + projectConfiguration.basePackage().toString().replace('.', '/')));
    }

    private void renameRootDirectory(Path projectDirectory, ProjectConfiguration projectConfiguration) throws IOException {
        var oldRoot = projectDirectory.resolve("walking-skeleton");
        var newRoot = projectDirectory.resolve(projectConfiguration.artifactId().toString());
        Files.move(oldRoot, newRoot, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    private void moveJavaClasses(Path javaDirectory, PackageName basePackage) throws IOException {
        var newPackage = createBasePackageDirectories(javaDirectory, basePackage);
        var oldPackage = javaDirectory.resolve("com/example/application");
        Files.move(oldPackage, newPackage, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        try {
            Files.delete(javaDirectory.resolve("com/example"));
            Files.delete(javaDirectory.resolve("com"));
        } catch (DirectoryNotEmptyException ignoreIt) {
            // The new base package started with 'com' or 'com.example'.
        }
    }

    private Path createBasePackageDirectories(Path destinationDir, PackageName basePackage) throws IOException {
        var path = destinationDir.resolve(basePackage.toString().replace(".", "/"));
        Files.createDirectories(path);
        return path;
    }

    Path generateProjectArchive(ProjectConfiguration projectConfiguration) throws IOException {
        var directory = generateProjectDirectory(projectConfiguration);
        try {
            var zipFile = Files.createTempFile(projectConfiguration.artifactId().toString(), ".zip");
            ZipUtils.createZipFromDirectory(directory, zipFile, file -> file.getFileName().toString().equals("mvnw"));
            return zipFile;
        } finally {
            FileUtils.deleteDirectory(directory);
        }
    }
}
