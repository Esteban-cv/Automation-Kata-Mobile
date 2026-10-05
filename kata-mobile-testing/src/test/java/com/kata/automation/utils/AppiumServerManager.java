package com.kata.automation.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

/**
 * Starts Appium only when the dashboard owns the server process.
 */
public final class AppiumServerManager {
    private static final String DEFAULT_SERVER_URL = "http://127.0.0.1:4723";
    private static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(30);
    private static Process ownedProcess;

    private AppiumServerManager() {
    }

    public static synchronized void ensureRunning() {
        String serverUrl = serverUrl();
        if (isRunning(serverUrl)) {
            return;
        }

        Path logPath = Paths.get("Logs", "appium.log");
        try {
            Files.createDirectories(logPath.getParent());
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "appium", "--relaxed-security");
            builder.redirectErrorStream(true);
            builder.redirectOutput(ProcessBuilder.Redirect.appendTo(logPath.toFile()));
            ownedProcess = builder.start();
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "No se pudo iniciar Appium. Ejecuta InstalarDependencias.bat y verifica que "
                            + "appium esté disponible en el PATH.",
                    exception);
        }

        Instant deadline = Instant.now().plus(STARTUP_TIMEOUT);
        while (Instant.now().isBefore(deadline)) {
            if (isRunning(serverUrl)) {
                return;
            }
            if (ownedProcess != null && !ownedProcess.isAlive()) {
                throw new IllegalStateException(
                        "Appium terminó durante el arranque. Revisa Logs\\appium.log.");
            }
            sleep(Duration.ofMillis(500));
        }
        stopOwnedProcess();
        throw new IllegalStateException(
                "Appium no respondió en 30 segundos. Verifica Node.js, UiAutomator2 y Logs\\appium.log.");
    }

    public static synchronized void stopOwnedProcess() {
        if (ownedProcess == null) {
            return;
        }
        if (ownedProcess.isAlive()) {
            ownedProcess.destroy();
            try {
                if (!ownedProcess.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    ownedProcess.destroyForcibly();
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                ownedProcess.destroyForcibly();
            }
        }
        ownedProcess = null;
    }

    private static boolean isRunning(String serverUrl) {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(serverUrl + "/status")
                    .toURL().openConnection();
            connection.setConnectTimeout(500);
            connection.setReadTimeout(500);
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();
            connection.disconnect();
            return status >= 200 && status < 300;
        } catch (IOException | IllegalArgumentException exception) {
            return false;
        }
    }

    private static String serverUrl() {
        String configuredUrl = System.getenv("APPIUM_SERVER_URL");
        return configuredUrl == null || configuredUrl.isBlank()
                ? DEFAULT_SERVER_URL
                : configuredUrl.replaceAll("/+$", "");
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("El arranque de Appium fue interrumpido.", exception);
        }
    }
}
