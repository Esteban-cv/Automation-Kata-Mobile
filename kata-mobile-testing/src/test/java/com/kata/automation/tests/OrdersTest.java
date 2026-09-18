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

    @Test(groups = "smoke")
    public void smokeTestCreacionOrden() {
        System.out.println("--- Ejecutando Smoke Test Rápido ---");

        // 1. Iniciar la orden con parámetros fijos
        ordersPage.createOrder("Originación de microcrédito", "SolicitudPersonaNatural");

        // 2. Consulta preliminar con los datos de prueba de Esteban
        ordersPage.preliminaryConsultation(
                "Cedula ciudadanía", 
                "1116070669", 
                "COLOMBIA", 
                "Colorado", 
                "Esteban", 
                10, 9, 2005, // Ajustado el año para que coincida con tus 20 años
                "3185551310", 
                17, 9, 2020, 
                "60", 
                "Gestión front o contact center", 
                "Continuar"
        );

        // 3. Cerrar la orden
        ordersPage.closeOrder();
    }

    // @Test(dataProvider = "excelData")
    // public void createWholeOrderFromExcel(String orderType, String personType,
    //         String idType, String numberId, String country,
    //         String firstName, String name, String birthDay,
    //         String birthMonth, String birthYear, String numberPhone,
    //         String corpDay, String corpMonth, String corpYear,
    //         String longevity, String origin, String decision) {

    //     // Conversión segura mediante BigDecimal para evitar el error de notación científica de Excel
    //     long parsedId = new BigDecimal(numberId).longValue();
    //     long parsedPhone = new BigDecimal(numberPhone).longValue();

    //     // 1. Iniciar la orden usando los datos inyectados del Excel
    //     ordersPage.createOrder(orderType, personType);

    //     // 2. Ejecutar la consulta preliminar con todos los tipos de datos casteados correctamente
    //     ordersPage.preliminaryConsultation(
    //             idType,
    //             numberId,
    //             country,
    //             firstName,
    //             name,
    //             Integer.parseInt(birthDay.replace(".0", "")),
    //             Integer.parseInt(birthMonth.replace(".0", "")),
    //             Integer.parseInt(birthYear.replace(".0", "")),
    //             String.valueOf(parsedPhone),
    //             Integer.parseInt(corpDay.replace(".0", "")),
    //             Integer.parseInt(corpMonth.replace(".0", "")),
    //             Integer.parseInt(corpYear.replace(".0", "")),
    //             longevity.replace(".0", ""),
    //             origin, decision);

    //     // 3. Guardar y cerrar la orden para limpiar la pantalla de cara a la siguiente iteración
    //     ordersPage.closeOrder();
    // }
}
