package com.kata.automation.utils;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
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
    private final Map<String, String> normalizedValues = new LinkedHashMap<>();

    public TestScenario(Map<String, String> values) {
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
        values.forEach((column, value) -> normalizedValues.merge(
                normalizeHeader(column), value, (current, next) -> next.isBlank() ? current : next));
    }

    /** Lets "Número de identificación", "numero_identificacion" and "NumeroIdentificacion" match. */
    public static String normalizeHeader(String header) {
        String withoutAccents = Normalizer.normalize(header == null ? "" : header, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    /** Returns the first non-blank value among the given column names (English or Spanish). */
    public String get(String... columns) {
        for (String column : columns) {
            String value = normalizedValues.getOrDefault(normalizeHeader(column), "");
            if (!value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    public String getCaseId() {
        return get("idCaso", "caseId", "id_caso");
    }

    public String getNumberId() {
        return get("numberId", "numeroIdentificacion", "Número de identificación");
    }

    public String getProduct() {
        return get("product", "Producto", "productType", "Tipo de producto");
    }

    public Type getType() {
        return parseEnum(get("tipoPrueba", "Tipo de prueba"), "Tipo de prueba", Type.class, Type.VALID);
    }

    public ExpectedResult getExpectedResult() {
        return parseEnum(get("resultadoEsperado"), "Resultado esperado",
                ExpectedResult.class, ExpectedResult.SUCCESS);
    }

    public boolean shouldContinue() {
        String value = get("continuarFlujo", "continueFlow");
        return value.isBlank() || "SI".equalsIgnoreCase(value) || "YES".equalsIgnoreCase(value);
    }

    /**
     * Keeps the original Excel representation so very large amounts are never
     * silently rounded by a double conversion.
     */
    public String getAmountText() {
        return get("amount", "monto").trim();
    }

    public BigDecimal getAmount() {
        String amount = getAmountText().replace(",", "").replace("$", "").trim();
        return amount.isBlank() ? null : new BigDecimal(amount);
    }

    public Map<String, String> values() {
        return values;
    }

    private <T extends Enum<T>> T parseEnum(String rawValue, String column, Class<T> type, T fallback) {
        String value = rawValue.trim();
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
}
