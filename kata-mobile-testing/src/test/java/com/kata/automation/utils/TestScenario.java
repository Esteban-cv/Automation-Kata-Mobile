package com.kata.automation.utils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Represents one Excel row and the expected behavior for that test case. */
public final class TestScenario {
    public enum Type {
        VALID,
        BOUNDARY,
        INVALID,
        CONFIGURATION
    }

    public enum ExpectedResult {
        SUCCESS,
        REJECTED,
        CONTROLLED_ERROR,
        TECHNICAL_ERROR
    }

    private final Map<String, String> values;

    public TestScenario(Map<String, String> values) {
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public String get(String column) {
        return values.getOrDefault(column, "");
    }

    public String getCaseId() {
        return firstNonBlank(get("idCaso"), get("caseId"), get("id_caso"));
    }

    public Type getType() {
        return parseEnum("tipoPrueba", Type.class, Type.VALID);
    }

    public ExpectedResult getExpectedResult() {
        return parseEnum("resultadoEsperado", ExpectedResult.class, ExpectedResult.SUCCESS);
    }

    public boolean shouldContinue() {
        String value = firstNonBlank(get("continuarFlujo"), get("continueFlow"));
        return value.isBlank() || "SI".equalsIgnoreCase(value) || "YES".equalsIgnoreCase(value);
    }

    /**
     * Keeps the original Excel representation so very large amounts are never
     * silently rounded by a double conversion.
     */
    public String getAmountText() {
        return firstNonBlank(get("amount"), get("monto"));
    }

    public BigDecimal getAmount() {
        String amount = getAmountText().replace(",", "").replace("$", "").trim();
        return amount.isBlank() ? null : new BigDecimal(amount);
    }

    public Map<String, String> values() {
        return values;
    }

    private <T extends Enum<T>> T parseEnum(String column, Class<T> type, T fallback) {
        String value = get(column).trim();
        if (value.isBlank()) {
            return fallback;
        }
        try {
            return Enum.valueOf(type, value.toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Valor inválido en la columna '" + column + "': " + value, exception);
        }
    }

    private static String firstNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (candidate != null && !candidate.isBlank()) {
                return candidate.trim();
            }
        }
        return "";
    }
}
