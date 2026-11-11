package com.kata.automation.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Writes a readable diagnostic log for the currently running execution. */
public final class ExecutionLog {
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
            .withLocale(Locale.ROOT).withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter ENTRY_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withLocale(Locale.ROOT).withZone(ZoneId.systemDefault());
    private static Path currentFile;

    private ExecutionLog() {
    }

    public static synchronized void start(int runNumber) {
        try {
            Path logsDirectory = EvidenceStorage.evidenceDirectory().getParent().resolve("Logs");
            Files.createDirectories(logsDirectory);
            currentFile = logsDirectory.resolve("ejecucion_" + runNumber + "_"
                    + FILE_TIME.format(Instant.now()) + ".log");
            write("INICIO", "Ejecución " + runNumber + " iniciada");
        } catch (IOException exception) {
            currentFile = null;
            System.err.println("No se pudo crear el log de la ejecución: " + exception.getMessage());
        }
    }

    public static synchronized void finish(String status) {
        if (currentFile != null) {
            write("FIN", "Ejecución finalizada con estado: " + status);
            currentFile = null;
        }
    }

    public static synchronized void info(String message) {
        write("INFO", message);
    }

    public static synchronized void error(String message) {
        write("ERROR", message);
        if (currentFile == null) {
            System.err.println(message);
        }
    }

    public static synchronized void error(String message, Throwable throwable) {
        String detail = throwable == null ? "Sin detalle de excepción proporcionado por TestNG."
                : stackTrace(throwable);
        write("ERROR", message + System.lineSeparator() + detail);
        if (currentFile == null) {
            System.err.println(message);
            if (throwable != null) {
                throwable.printStackTrace(System.err);
            }
        }
    }

    private static void write(String level, String message) {
        if (currentFile == null) {
            return;
        }
        String entry = "[" + ENTRY_TIME.format(Instant.now()) + "] [" + level + "] "
                + message + System.lineSeparator();
        try {
            Files.writeString(currentFile, entry, StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException exception) {
            System.err.println("No se pudo escribir el log de la ejecución: " + exception.getMessage());
        }
    }

    private static String stackTrace(Throwable throwable) {
        StringWriter text = new StringWriter();
        throwable.printStackTrace(new PrintWriter(text));
        return text.toString();
    }
}