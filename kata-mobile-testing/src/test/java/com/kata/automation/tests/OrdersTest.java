package com.kata.automation.tests;

import java.math.BigDecimal;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.kata.automation.pages.OrdersPage;
import com.kata.automation.utils.ExcelUtils;

public class OrdersTest extends BaseTest {
    private OrdersPage ordersPage;

    @BeforeClass
    public void setUp() {
        ordersPage = new OrdersPage(driver);
    }

    @DataProvider(name = "excelData")
    public Object[][] getData() {
        String excelPath = "src/test/resources/datos_solicitudes.xlsx";
        return ExcelUtils.getTestData(excelPath, "Hoja1");
    }

    // @Test(groups = "smoke")
    // public void smokeTestCreacionOrden() {
    // System.out.println("--- Ejecutando Smoke Test Rápido ---");

    // ordersPage.setScreenDensity(380);

    // // 1. Iniciar la orden con parámetros fijos
    // ordersPage.createOrder("Originación de microcrédito",
    // "SolicitudPersonaNatural");

    // // 2. Consulta preliminar con los datos de prueba de Esteban
    // ordersPage.preliminaryConsultation(
    // "Cedula ciudadanía",
    // "1116070669",
    // "COLOMBIA",
    // "Colorado",
    // "Esteban",
    // 10, 9, 2005, // Ajustado el año para que coincida con tus 20 años
    // "3185551310",
    // 17, 9, 2020,
    // "60",
    // "Gestión front o contact center",
    // "Continuar"
    // );

    // // 3. Cerrar la orden
    // ordersPage.closeOrder();
    // }

    @Test(dataProvider = "excelData")
    public void createWholeOrderFromExcel(String orderType, String personType,
            String idType, String numberId, String country,
            String firstName, String name, String birthDay,
            String birthMonth, String birthYear, String numberPhone,
            String corpDay, String corpMonth, String corpYear,
            String longevity, String origin, String decision) {

        ordersPage.setScreenDensity(350);
        ordersPage.createOrder(orderType, personType);

        // Se pasan los datos pasándolos por los métodos auxiliares para limpiarlos al
        // vuelo
        ordersPage.preliminaryConsultation(
                idType,
                clean(numberId),
                country,
                firstName,
                name,
                toInt(birthDay), toInt(birthMonth), toInt(birthYear),
                clean(numberPhone),
                toInt(corpDay), toInt(corpMonth), toInt(corpYear),
                clean(longevity),
                origin,
                decision);
    }

    // --- MÉTODOS AUXILIARES (Ponlos al final de tu clase OrdersTest) ---

    // Limpia decimales, espacios y convierte notación científica de Excel
    private String clean(String value) {
        if (value == null)
            return "";
        value = value.trim();

        try {
            // Si Excel lo mandó como notación científica (ej: 8.6065519E7)
            if (value.toUpperCase().contains("E")) {
                return new BigDecimal(value).toPlainString();
            }
        } catch (Exception e) {
            /* Ignorado */ }

        return value.replace(".0", "");
    }

    // Limpia y convierte automáticamente los números para el calendario
    private int toInt(String value) {
        return Integer.parseInt(clean(value));
    }
}
