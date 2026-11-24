package com.kata.automation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Cónyuges de prueba, en la hoja "Datos conyuge" del mismo Excel de solicitudes.
 *
 * <p>
 * La app pide la sección del cónyuge cuando el estado civil es Casado(a) o Unión Libre, y
 * valida esa cédula contra listas: una cédula inventada puede estar reportada y bloquear la
 * solicitud. Por eso los cónyuges se mantienen en una sola hoja con personas "limpias" y cada
 * solicitud elige uno en la columna "Cónyuge" (número de identificación). Si la columna está
 * vacía se usa el primer cónyuge de la hoja.
 * </p>
 */
public final class SpouseCatalog {
    public static final String SHEET = "Datos conyuge";
    public static final String KEY = "Número de identificación";

    private SpouseCatalog() {
    }

    /** Datos del cónyuge para la solicitud (columna → valor), o un mapa vacío si no hay hoja. */
    public static Map<String, String> forScenario(TestScenario scenario) {
        List<Map<String, String>> spouses = readSheet(SHEET);
        if (spouses.isEmpty()) {
            return Collections.emptyMap();
        }
        String wanted = scenario == null ? "" : digits(scenario.get("Cónyuge", "Conyuge"));
        if (!wanted.isBlank()) {
            for (Map<String, String> spouse : spouses) {
                if (wanted.equals(digits(get(spouse, KEY)))) {
                    return spouse;
                }
            }
            throw new IllegalArgumentException("El cónyuge " + wanted + " de la columna 'Cónyuge' no está en la hoja '"
                    + SHEET + "' del Excel.");
        }
        return spouses.get(0);
    }

    /** Valor de una columna del cónyuge, sin importar tildes ni mayúsculas en el encabezado. */
    public static String get(Map<String, String> spouse, String column) {
        String wanted = TestScenario.normalizeHeader(column);
        for (Map.Entry<String, String> entry : spouse.entrySet()) {
            if (TestScenario.normalizeHeader(entry.getKey()).equals(wanted)) {
                return entry.getValue() == null ? "" : entry.getValue().trim();
            }
        }
        return "";
    }

    static String excelPath() {
        for (String property : List.of("kata.excel.path", "kata.inmueble.excel.path")) {
            String value = System.getProperty(property, "");
            if (!value.isBlank()) {
                return value;
            }
        }
        return "src/test/resources/datos_solicitudes_configurado.xlsx";
    }

    /** Filas de una hoja auxiliar del Excel de solicitudes (encabezado → valor), sin las filas sin cédula. */
    static List<Map<String, String>> readSheet(String sheetName) {
        String path = excelPath();
        try (FileInputStream input = new FileInputStream(path); Workbook workbook = new XSSFWorkbook(input)) {
            Sheet sheet = null;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                if (TestScenario.normalizeHeader(workbook.getSheetName(i)).equals(TestScenario.normalizeHeader(sheetName))) {
                    sheet = workbook.getSheetAt(i);
                }
            }
            if (sheet == null || sheet.getRow(0) == null) {
                return Collections.emptyList();
            }
            DataFormatter formatter = new DataFormatter(Locale.ROOT);
            Row header = sheet.getRow(0);
            List<Map<String, String>> spouses = new ArrayList<>();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                Map<String, String> spouse = new LinkedHashMap<>();
                for (int c = 0; c < header.getLastCellNum(); c++) {
                    Cell name = header.getCell(c);
                    Cell value = row.getCell(c);
                    if (name != null) {
                        spouse.put(formatter.formatCellValue(name).trim(),
                                value == null ? "" : formatter.formatCellValue(value).trim());
                    }
                }
                if (!get(spouse, KEY).isBlank()) {
                    spouses.add(spouse);
                }
            }
            return spouses;
        } catch (IOException exception) {
            ExecutionLog.error("No se pudo leer la hoja '" + sheetName + "' del Excel " + path, exception);
            return Collections.emptyList();
        }
    }

    static String digits(String value) {
        return value == null ? "" : value.replaceAll("[^0-9]", "");
    }
}
