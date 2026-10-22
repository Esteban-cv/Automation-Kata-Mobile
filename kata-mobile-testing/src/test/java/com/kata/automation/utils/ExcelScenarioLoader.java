package com.kata.automation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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

/** Reads test cases and product parameters from the workbook without changing legacy readers. */
public final class ExcelScenarioLoader {
    public static final String PARAMETERS_SHEET = "Parametros";
    private static final String CASE_ID = "idCaso";

    private ExcelScenarioLoader() {
    }

    public static List<TestScenario> readScenarios(Path file, String sheetName) {
        try (FileInputStream input = new FileInputStream(file.toFile());
                Workbook workbook = new XSSFWorkbook(input)) {
            Sheet sheet = requireSheet(workbook, sheetName);
            List<String> headers = readHeaders(sheet);
            List<TestScenario> scenarios = new ArrayList<>();
            DataFormatter formatter = new DataFormatter(Locale.ROOT);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (isBlank(row, headers.size(), formatter)) {
                    continue;
                }
                Map<String, String> values = new LinkedHashMap<>();
                for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
                    Cell cell = row.getCell(columnIndex);
                    values.put(headers.get(columnIndex), cell == null ? "" : formatter.formatCellValue(cell).trim());
                }
                if (new TestScenario(values).getCaseId().isBlank()) {
                    values.put(CASE_ID, "FILA-" + (rowIndex + 1));
                }
                scenarios.add(new TestScenario(values));
            }
            return scenarios;
        } catch (IOException exception) {
            throw new IllegalArgumentException("No se pudo leer el Excel de escenarios: " + file, exception);
        }
    }

    public static List<ProductParameter> readParameters(Path file) {
        try (FileInputStream input = new FileInputStream(file.toFile());
                Workbook workbook = new XSSFWorkbook(input)) {
            Sheet sheet = workbook.getSheet(PARAMETERS_SHEET);
            if (sheet == null) {
                return Collections.emptyList();
            }
            List<String> headers = readHeaders(sheet);
            Map<String, Integer> positions = positions(headers);
            DataFormatter formatter = new DataFormatter(Locale.ROOT);
            List<ProductParameter> parameters = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                String product = cell(row, positions, "producto", formatter);
                String parameter = cell(row, positions, "parametro", formatter);
                if (product.isBlank() || parameter.isBlank()) {
                    continue;
                }
                parameters.add(new ProductParameter(
                        product,
                        parameter,
                        decimal(cell(row, positions, "minimo", formatter)),
                        decimal(cell(row, positions, "maximo", formatter)),
                        splitOptions(cell(row, positions, "valoresPermitidos", formatter)),
                        parseBoolean(cell(row, positions, "obligatorio", formatter))));
            }
            return parameters;
        } catch (IOException exception) {
            throw new IllegalArgumentException("No se pudo leer la hoja de parámetros del Excel: " + file, exception);
        }
    }

    private static Sheet requireSheet(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null || sheet.getRow(0) == null) {
            throw new IllegalArgumentException("No existe la hoja o encabezado: " + sheetName);
        }
        return sheet;
    }

    private static List<String> readHeaders(Sheet sheet) {
        Row header = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < header.getLastCellNum(); columnIndex++) {
            String value = header.getCell(columnIndex) == null ? "" : header.getCell(columnIndex).getStringCellValue();
            if (value.isBlank()) {
                throw new IllegalArgumentException("El encabezado de la columna " + (columnIndex + 1) + " está vacío.");
            }
            headers.add(value.trim());
        }
        return headers;
    }

    private static boolean isBlank(Row row, int columnCount, DataFormatter formatter) {
        if (row == null) {
            return true;
        }
        for (int index = 0; index < columnCount; index++) {
            Cell cell = row.getCell(index);
            if (cell != null && !formatter.formatCellValue(cell).trim().isBlank()) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, Integer> positions(List<String> headers) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (int index = 0; index < headers.size(); index++) {
            result.put(headers.get(index).toLowerCase(Locale.ROOT), index);
        }
        return result;
    }

    private static String cell(Row row, Map<String, Integer> positions, String name, DataFormatter formatter) {
        Integer index = positions.get(name.toLowerCase(Locale.ROOT));
        if (index == null || row.getCell(index) == null) {
            return "";
        }
        return formatter.formatCellValue(row.getCell(index)).trim();
    }

    private static BigDecimal decimal(String value) {
        return value.isBlank() ? null : new BigDecimal(value.replace(",", "").replace("$", "").trim());
    }

    private static List<String> splitOptions(String value) {
        if (value.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split("\\|"))
                .map(option -> option.trim())
                .filter(option -> !option.isBlank())
                .toList();
    }

    private static boolean parseBoolean(String value) {
        return "si".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
    }
}
