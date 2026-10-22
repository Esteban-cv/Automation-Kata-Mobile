package com.kata.automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ExcelUtils {
    /** Normalized names of the optional scenario columns ("idCaso" or "ID caso", etc.). */
    private static final Set<String> SCENARIO_METADATA_HEADERS = Set.of(
            "idcaso", "tipoprueba", "tipodeprueba", "resultadoesperado", "continuarflujo");

    /** Typed scenario reader for new tests; the legacy Object[][] API remains unchanged. */
    public static List<TestScenario> getScenarios(String filePath, String sheetName) {
        return ExcelScenarioLoader.readScenarios(Path.of(filePath), sheetName);
    }

    /** Reads business rules maintained by analysts in the Parametros sheet. */
    public static List<ProductParameter> getProductParameters(String filePath) {
        return ExcelScenarioLoader.readParameters(Path.of(filePath));
    }

    public static Optional<TestScenario> findScenario(String filePath, String sheetName, String numberId) {
        return getScenarios(filePath, sheetName).stream()
                .filter(scenario -> numberId.equals(scenario.getNumberId()))
                .findFirst();
    }

    public static Object[][] getTestData(String filePath, String sheetName) {
        Object[][] data = null;
        DataFormatter formatter = new DataFormatter();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null || sheet.getRow(0) == null) {
                throw new IllegalArgumentException("No existe la hoja o encabezado: " + sheetName);
            }
            int rowCount = sheet.getPhysicalNumberOfRows();
            // Asumimos que la primera fila son los encabezados, por lo que empezamos desde la fila 1
            int colCount = legacyColumnCount(sheet);

            data = new Object[rowCount - 1][colCount];

            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    data[i - 1][j] = (cell == null) ? "" : formatter.formatCellValue(cell).trim();
                }

            }
        } catch (IOException e) {
            ExecutionLog.error("No se pudo leer el archivo Excel", e);
        }
        return data;
    }

    /**
     * Keeps the existing Object[][] contract stable when a workbook also
     * contains the optional scenario metadata columns.
     */
    private static int legacyColumnCount(Sheet sheet) {
        Row header = sheet.getRow(0);
        for (int columnIndex = 0; columnIndex < header.getLastCellNum(); columnIndex++) {
            Cell cell = header.getCell(columnIndex);
            if (cell == null) {
                continue;
            }
            String headerName = TestScenario.normalizeHeader(cell.getStringCellValue());
            if (SCENARIO_METADATA_HEADERS.contains(headerName)) {
                return columnIndex;
            }
        }
        return header.getLastCellNum();
    }
}