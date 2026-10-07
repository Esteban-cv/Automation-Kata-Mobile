package com.kata.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Adds the managed parameter sheet and scenario dropdowns to a workbook.
 * Existing data is preserved; the operation is intentionally opt-in.
 */
public final class ExcelWorkbookTemplate {
    public static final String PARAMETERS_SHEET = ExcelScenarioLoader.PARAMETERS_SHEET;
    private static final String[] SCENARIO_HEADERS = {
            "idCaso", "tipoPrueba", "resultadoEsperado", "continuarFlujo"
    };
    private static final String[] TYPE_OPTIONS = { "VALID", "BOUNDARY", "INVALID", "CONFIGURATION" };
    private static final String[] RESULT_OPTIONS = { "SUCCESS", "REJECTED", "CONTROLLED_ERROR", "TECHNICAL_ERROR" };
    private static final String[] BOOLEAN_OPTIONS = { "SI", "NO" };

    private ExcelWorkbookTemplate() {
    }

    public static void enhance(Path source, Path destination, String casesSheetName) {
        try (InputStream input = Files.newInputStream(source);
                Workbook workbook = new XSSFWorkbook(input)) {
            Sheet cases = workbook.getSheet(casesSheetName);
            if (cases == null || cases.getRow(0) == null) {
                throw new IllegalArgumentException("No existe la hoja de casos: " + casesSheetName);
            }
            appendScenarioHeaders(cases);
            createParametersSheet(workbook);
            addScenarioDropdowns(cases);
            addParameterDropdowns(cases, workbook.getSheet(PARAMETERS_SHEET));
            try (OutputStream output = Files.newOutputStream(destination)) {
                workbook.write(output);
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("No se pudo preparar la plantilla Excel.", exception);
        }
    }

    private static void appendScenarioHeaders(Sheet cases) {
        Row header = cases.getRow(0);
        int firstColumn = header.getLastCellNum();
        for (int index = 0; index < SCENARIO_HEADERS.length; index++) {
            Cell cell = header.getCell(firstColumn + index);
            if (cell == null) {
                cell = header.createCell(firstColumn + index);
            }
            cell.setCellValue(SCENARIO_HEADERS[index]);
        }
        for (int index = 1; index <= cases.getLastRowNum(); index++) {
            Row row = cases.getRow(index);
            if (row == null) {
                continue;
            }
            setDefault(row, firstColumn, String.format("MC-%03d", index));
            setDefault(row, firstColumn + 1, "VALID");
            setDefault(row, firstColumn + 2, "SUCCESS");
            setDefault(row, firstColumn + 3, "SI");
        }
    }

    private static void setDefault(Row row, int column, String value) {
        if (row.getCell(column) == null) {
            row.createCell(column).setCellValue(value);
        }
    }

    private static void createParametersSheet(Workbook workbook) {
        Sheet parameters = workbook.getSheet(PARAMETERS_SHEET);
        if (parameters == null) {
            parameters = workbook.createSheet(PARAMETERS_SHEET);
        }
        Row header = parameters.getRow(0);
        if (header == null) {
            header = parameters.createRow(0);
        }
        String[] headers = { "producto", "parametro", "minimo", "maximo", "valoresPermitidos", "obligatorio" };
        for (int index = 0; index < headers.length; index++) {
            header.createCell(index).setCellValue(headers[index]);
        }
        if (parameters.getRow(1) == null) {
            Row amount = parameters.createRow(1);
            amount.createCell(0).setCellValue("Credito popular productivo urbano360");
            amount.createCell(1).setCellValue("monto");
            amount.createCell(2).setCellValue(new BigDecimal("1750905").toPlainString());
            amount.createCell(3).setCellValue(new BigDecimal("10505430").toPlainString());
            amount.createCell(5).setCellValue("SI");
        }
        for (int index = 0; index < headers.length; index++) {
            parameters.autoSizeColumn(index);
        }
    }

    private static void addScenarioDropdowns(Sheet cases) {
        DataValidationHelper helper = cases.getDataValidationHelper();
        int lastRow = Math.max(cases.getLastRowNum() + 100, 101);
        int firstColumn = cases.getRow(0).getLastCellNum() - SCENARIO_HEADERS.length;
        addList(helper, cases, TYPE_OPTIONS, firstColumn + 1, lastRow);
        addList(helper, cases, RESULT_OPTIONS, firstColumn + 2, lastRow);
        addList(helper, cases, BOOLEAN_OPTIONS, firstColumn + 3, lastRow);
    }

    private static void addParameterDropdowns(Sheet cases, Sheet parameters) {
        if (parameters == null || parameters.getRow(0) == null) {
            return;
        }
        Row parameterHeader = parameters.getRow(0);
        int parameterColumn = findColumn(parameterHeader, "parametro");
        int valuesColumn = findColumn(parameterHeader, "valoresPermitidos");
        if (parameterColumn < 0 || valuesColumn < 0) {
            return;
        }
        DataValidationHelper helper = cases.getDataValidationHelper();
        Row caseHeader = cases.getRow(0);
        for (int rowIndex = 1; rowIndex <= parameters.getLastRowNum(); rowIndex++) {
            Row parameterRow = parameters.getRow(rowIndex);
            if (parameterRow == null || parameterRow.getCell(parameterColumn) == null
                    || parameterRow.getCell(valuesColumn) == null) {
                continue;
            }
            String parameterName = parameterRow.getCell(parameterColumn).getStringCellValue().trim();
            String options = parameterRow.getCell(valuesColumn).getStringCellValue().trim();
            int caseColumn = findColumn(caseHeader, parameterName);
            if (caseColumn < 0 || options.isBlank()) {
                continue;
            }
            List<String> values = new ArrayList<>();
            for (String option : options.split("\\|")) {
                if (!option.trim().isBlank()) {
                    values.add(option.trim());
                }
            }
            if (!values.isEmpty()) {
                addList(helper, cases, values.toArray(String[]::new), caseColumn,
                        Math.max(cases.getLastRowNum() + 100, 101));
            }
        }
    }

    private static int findColumn(Row header, String name) {
        for (int index = 0; index < header.getLastCellNum(); index++) {
            Cell cell = header.getCell(index);
            if (cell != null && name.equalsIgnoreCase(cell.getStringCellValue().trim())) {
                return index;
            }
        }
        return -1;
    }

    private static void addList(DataValidationHelper helper, Sheet sheet, String[] values, int column, int lastRow) {
        DataValidationConstraint constraint = helper.createExplicitListConstraint(values);
        DataValidation validation = helper.createValidation(constraint, new org.apache.poi.ss.util.CellRangeAddressList(1, lastRow, column, column));
        validation.setShowErrorBox(true);
        validation.createErrorBox("Valor no permitido", "Selecciona un valor de la lista.");
        sheet.addValidationData(validation);
    }
}
