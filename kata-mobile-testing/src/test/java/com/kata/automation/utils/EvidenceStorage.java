package com.kata.automation.utils;

import java.net.URI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Resolves the writable evidence folder for Maven runs and packaged apps. */
public final class EvidenceStorage {
    private static final String EVIDENCE_FOLDER = "evidencias";

    private EvidenceStorage() {
    }

    public static Path evidenceDirectory() {
        Path applicationDirectory = applicationDirectory();
        return applicationDirectory.resolve(EVIDENCE_FOLDER);
    }

    public static void ensureEvidenceDirectory() {
        try {
            Files.createDirectories(evidenceDirectory());
        } catch (IOException exception) {
            ExecutionLog.error("No se pudo crear la carpeta de evidencias", exception);
        }
    }

    private static Path applicationDirectory() {
        try {
            URI codeLocation = EvidenceStorage.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI();
            Path location = Paths.get(codeLocation);
            if (Files.isRegularFile(location)) {
                Path parent = location.getParent();
                if (parent != null) {
                    return parent;
                }
            }
        } catch (Exception ignored) {
            // Uses the working directory when the runtime location is unavailable.
        }
        return Paths.get(System.getProperty("user.dir"));
    }
}