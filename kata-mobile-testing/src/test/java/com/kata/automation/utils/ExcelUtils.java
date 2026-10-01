package com.kata.automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtils {

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
            int colCount = sheet.getRow(0).getLastCellNum();

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
}