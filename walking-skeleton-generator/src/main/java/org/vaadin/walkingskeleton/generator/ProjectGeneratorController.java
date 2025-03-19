package org.vaadin.walkingskeleton.generator;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/generate")
class ProjectGeneratorController {

    private final ProjectGenerator projectGenerator;

    ProjectGeneratorController(ProjectGenerator projectGenerator) {
        this.projectGenerator = projectGenerator;
    }

    @PostMapping
    public ResponseEntity<Resource> downloadProject(@RequestBody ProjectConfiguration projectConfiguration) {
        try {
            var zipFile = projectGenerator.generateProjectArchive(projectConfiguration);
            var resource = new DeletingFileSystemResource(zipFile);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + projectConfiguration.artifactId() + ".zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(Files.size(zipFile))
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private static class DeletingFileSystemResource extends FileSystemResource {
        private final Path path;

        public DeletingFileSystemResource(Path path) {
            super(path);
            this.path = path;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new BufferedInputStream(super.getInputStream()) {
                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        Files.deleteIfExists(path);
                    }
                }
            };
        }
    }
}
