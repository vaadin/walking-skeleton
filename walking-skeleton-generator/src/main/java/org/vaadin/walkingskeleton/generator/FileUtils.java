package org.vaadin.walkingskeleton.generator;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

final class FileUtils {

    private FileUtils() {
    }

    static void deleteDirectory(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    static void replaceInFiles(Path directory, Predicate<Path> predicate, Map<String, String> replacements) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (predicate.test(file)) {
                    FileUtils.replaceInFile(file, replacements);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    static void replaceInFile(Path file, Map<String, String> replacements) throws IOException {
        var content = Files.readString(file);
        boolean modified = false;
        for (var replacement : replacements.entrySet()) {
            var newContent = content.replace(replacement.getKey(), replacement.getValue());
            if (!newContent.equals(content)) {
                content = newContent;
                modified = true;
            }
        }

        if (modified) {
            Files.writeString(file, content);
        }
    }

    static Predicate<Path> nameEndsWith(String... extensions) {
        var extensionSet = Set.of(extensions);
        return path -> {
            var name = path.toString();
            return extensionSet.stream().anyMatch(name::endsWith);
        };
    }
}
