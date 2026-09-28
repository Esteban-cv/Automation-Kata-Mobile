package com.kata.automation.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Stores dashboard timing facts outside the in-memory Swing session. */
public final class ExecutionHistoryStore {
    private static final Path HISTORY_FILE = Paths.get("evidencias", "dashboard-history.csv");

    public record Entry(Instant timestamp, String type, String key, long durationMillis, String status) {
    }

    private ExecutionHistoryStore() {
    }

    public static synchronized void save(String type, String key, long durationMillis, String status) {
        try {
            Files.createDirectories(HISTORY_FILE.getParent());
            String row = String.join(",", Instant.now().toString(), type,
                    key.replace(",", " "), String.valueOf(durationMillis), status) + System.lineSeparator();
            Files.writeString(HISTORY_FILE, row,
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException exception) {
            System.err.println("No se pudo guardar la estadística del dashboard: " + exception.getMessage());
        }
    }

    public static synchronized List<Entry> readAll() {
        List<Entry> entries = new ArrayList<>();
        if (!Files.exists(HISTORY_FILE)) {
            return entries;
        }
        try {
            for (String row : Files.readAllLines(HISTORY_FILE)) {
                String[] columns = row.split(",", -1);
                if (columns.length != 5) {
                    continue;
                }
                entries.add(new Entry(Instant.parse(columns[0]), columns[1], columns[2],
                        Long.parseLong(columns[3]), columns[4]));
            }
        } catch (IOException | RuntimeException exception) {
            System.err.println("No se pudo leer el historial del dashboard: " + exception.getMessage());
        }
        return entries;
    }
}
