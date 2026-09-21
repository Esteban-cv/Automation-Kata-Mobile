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
    //     System.out.println("--- Ejecutando Smoke Test Rápido ---");

    //     // 1. Iniciar la orden con parámetros fijos
    //     ordersPage.createOrder("Originación de microcrédito", "SolicitudPersonaNatural");

    //     // 2. Consulta preliminar con los datos de prueba de Esteban
    //     ordersPage.preliminaryConsultation(
    //             "Cedula ciudadanía", 
    //             "1116070669", 
    //             "COLOMBIA", 
    //             "Colorado", 
    //             "Esteban", 
    //             10, 9, 2005, // Ajustado el año para que coincida con tus 20 años
    //             "3185551310", 
    //             17, 9, 2020, 
    //             "60", 
    //             "Gestión front o contact center", 
    //             "Continuar"
    //     );

    //     // 3. Cerrar la orden
    //     ordersPage.closeOrder();
    // }

    @Test(dataProvider = "excelData")
    public void createWholeOrderFromExcel(String orderType, String personType,
            String idType, String numberId, String country,
            String firstName, String name, String birthDay,
            String birthMonth, String birthYear, String numberPhone,
            String corpDay, String corpMonth, String corpYear,
            String longevity, String origin, String decision) {

        ordersPage.setScreenDensity(350);

        // 2. LIMPIEZA DE ENTEROS (Evita que el calendario se rompa por espacios o decimales)
        int bDay = Integer.parseInt(birthDay.replace(".0", "").trim());
        int bMonth = Integer.parseInt(birthMonth.replace(".0", "").trim());
        int bYear = Integer.parseInt(birthYear.replace(".0", "").trim());

        int cDay = Integer.parseInt(corpDay.replace(".0", "").trim());
        int cMonth = Integer.parseInt(corpMonth.replace(".0", "").trim());
        int cYear = Integer.parseInt(corpYear.replace(".0", "").trim());

        // 3. Iniciar la orden
        ordersPage.createOrder(orderType, personType);

        // 4. Ejecutar la consulta preliminar con los datos 100% limpios
        ordersPage.preliminaryConsultation(
                idType, 
                numberId, 
                country, 
                firstName, 
                name,
                bDay, bMonth, bYear, 
                numberPhone,
                cDay, cMonth, cYear, 
                longevity,
                origin, 
                decision
        );

        // 5. Guardar y cerrar
        ordersPage.closeOrder();
    }
}
