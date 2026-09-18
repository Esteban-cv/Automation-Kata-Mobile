package com.kata.automation.tests;

import com.kata.automation.pages.LoginPage;
import com.kata.automation.pages.OrdersPage;
import com.kata.automation.utils.ExcelUtils;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Reporter;
import java.math.BigDecimal;

public class FlujoCompletoTest extends BaseTest {  

    private LoginPage loginPage;
    private OrdersPage ordersPage;

    @BeforeClass
    public void setUpPages() {
        loginPage = new LoginPage(driver);
        ordersPage = new OrdersPage(driver);
        
        // FASE 1: Autenticación única al iniciar la clase de prueba
        System.out.println("--- FASE 1: Autenticación ---");
        loginPage.firstContinueClickButton();
        loginPage.doLogin("Banco2024*");
        loginPage.afterLogin();
    }

    @DataProvider(name = "excelData")
    public Object[][] getData() {
        String excelPath = "src/test/resources/datos_solicitudes.xlsx";
        return ExcelUtils.getTestData(excelPath, "Hoja1");
    }

    @Test(dataProvider = "excelData")
    public void verificarCreacionDeOrdenMicrocredito(String orderType, String personType,
            String idType, String numberId, String country,
            String firstName, String name, String birthDay,
            String birthMonth, String birthYear, String numberPhone,
            String corpDay, String corpMonth, String corpYear,
            String longevity, String origin, String decision) {

        System.out.println("--- FASE 2: Creación de Orden (Procesando fila del Excel) ---");

        // 1. Iniciar la orden
        ordersPage.createOrder(orderType, personType);

        // 2. Ejecutar consulta preliminar, validaciones y vistas
        ordersPage.preliminaryConsultation(
                idType,
                numberId,
                country,
                firstName,
                name,
                Integer.parseInt(birthDay.replace(".0", "")),
                Integer.parseInt(birthMonth.replace(".0", "")),
                Integer.parseInt(birthYear.replace(".0", "")),
                numberPhone,
                Integer.parseInt(corpDay.replace(".0", "")),
                Integer.parseInt(corpMonth.replace(".0", "")),
                Integer.parseInt(corpYear.replace(".0", "")),
                longevity.replace(".0", ""),
                origin,
                decision);

        // 3. Cerrar y guardar la orden para dejar la app lista para la siguiente fila
        ordersPage.closeOrder();

        String successMessage = "Orden procesada y guardada correctamente para la cédula: " + numberId;
        System.out.println(successMessage);
        Reporter.log(successMessage, true);
    }
}