package com.kata.automation.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Codeudores de prueba, en la hoja "Datos codeudor" del mismo Excel de solicitudes.
 *
 * <p>
 * Cada solicitud elige su codeudor en la columna "Codeudor" (número de identificación de una
 * fila de la hoja). Columna vacía = la solicitud va sin codeudor. Igual que con el cónyuge, la
 * app consulta la cédula del codeudor en centrales y listas, así que la hoja debe tener personas
 * "limpias". Las columnas vacías de la hoja toman el valor por defecto del robot.
 * </p>
 */
public final class CodebtorCatalog {
    public static final String SHEET = "Datos codeudor";
    public static final String KEY = "Número de identificación";

    private CodebtorCatalog() {
    }

    /** Número de identificación del codeudor elegido en la solicitud, o "" si no lleva. */
    public static String selected(TestScenario scenario) {
        return scenario == null ? "" : SpouseCatalog.digits(scenario.get("Codeudor"));
    }

    /** Datos del codeudor de la solicitud (columna → valor), o un mapa vacío si no lleva codeudor. */
    public static Map<String, String> forScenario(TestScenario scenario) {
        String wanted = selected(scenario);
        if (wanted.isBlank()) {
            return Collections.emptyMap();
        }
        List<Map<String, String>> codebtors = SpouseCatalog.readSheet(SHEET);
        for (Map<String, String> codebtor : codebtors) {
            if (wanted.equals(SpouseCatalog.digits(SpouseCatalog.get(codebtor, KEY)))) {
                return codebtor;
            }
        }
        throw new IllegalArgumentException("El codeudor " + wanted + " de la columna 'Codeudor' no está en la hoja '"
                + SHEET + "' del Excel.");
    }

    /** Valor de una columna del codeudor (sin importar tildes ni mayúsculas), o el valor por defecto. */
    public static String get(Map<String, String> codebtor, String column, String defaultValue) {
        String value = SpouseCatalog.get(codebtor, column).replaceAll("\\.0$", "");
        return value.isBlank() ? defaultValue : value;
    }
}
