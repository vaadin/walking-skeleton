package org.vaadin.walkingskeleton.generator;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;

final class ZipUtils {

    private ZipUtils() {
    }

    static void createZipFromDirectory(Path sourceDir, Path zipFile, Predicate<Path> shouldBeExecutable) throws IOException {
        try (var os = new ZipArchiveOutputStream(Files.newOutputStream(zipFile))) {
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    var zipEntryPath = sourceDir.relativize(file).toString();
                    var zipEntry = new ZipArchiveEntry(zipEntryPath);
                    if (shouldBeExecutable.test(file)) {
                        zipEntry.setUnixMode(0100755);
                    }

                    os.putArchiveEntry(zipEntry);
                    Files.copy(file, os);
                    os.closeArchiveEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    var zipEntryPath = sourceDir.relativize(dir).toString();
                    if (!zipEntryPath.isEmpty()) {
                        os.putArchiveEntry(new ZipArchiveEntry(zipEntryPath + "/"));
                        os.closeArchiveEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    static void extractZipIntoDirectory(Path zipFile, Path destDir) throws IOException {
        try (var is = new ZipArchiveInputStream(Files.newInputStream(zipFile))) {
            ZipArchiveEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                var targetPath = destDir.resolve(entry.getName());
                Files.createDirectories(targetPath.getParent());
                if (!entry.isDirectory()) {
                    Files.copy(is, targetPath);
                }
            }
        }
    }
}
