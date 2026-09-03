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

    @Test(dataProvider = "excelData")
    public void createWholeOrderFromExcel(String orderType, String personType,
            String idType, String numberId, String country,
            String firstName, String name, String birthDay,
            String birthMonth, String birthYear, String numberPhone,
            String corpDay, String corpMonth, String corpYear,
            String longevity, String origin) {

        OrdersPage ordersPage = new OrdersPage(driver);

        // Conversión segura mediante BigDecimal para evitar el error de notación
        // científica de Excel
        long parsedId = new BigDecimal(numberId).longValue();
        long parsedPhone = new BigDecimal(numberPhone).longValue();

        // 1. Iniciar la orden usando los datos inyectados del Excel
        ordersPage.createOrder(orderType, personType);

        // 2. Ejecutar la consulta preliminar con todos los tipos de datos casteados
        // correctamente
        ordersPage.preliminaryConsultation(
                idType,
                parsedId,
                country,
                firstName,
                name,
                Integer.parseInt(birthDay.replace(".0", "")),
                Integer.parseInt(birthMonth.replace(".0", "")),
                Integer.parseInt(birthYear.replace(".0", "")),
                String.valueOf(parsedPhone),
                Integer.parseInt(corpDay.replace(".0", "")),
                Integer.parseInt(corpMonth.replace(".0", "")),
                Integer.parseInt(corpYear.replace(".0", "")),
                longevity.replace(".0", ""),
                origin);

        // cerrar la orden
        ordersPage.closeOrder();
    }

    // @Test
    // public void createWholeOrder() {
    // ordersPage.createOrder("Originación de microcrédito",
    // "SolicitudPersonaNatural");
    // ordersPage.preliminaryConsultation("Cedula ciudadanía", 1116070669,
    // "COLOMBIA", "Colorado", "Esteban", 26, 10,
    // 2004, "3185551310", 03, 10, 2020, "70", "Gestión front o contact center");
    // }

}
