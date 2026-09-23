package com.kata.automation.tests;

import com.kata.automation.pages.LoginPage;
import com.kata.automation.pages.OrdersPage;
import com.kata.automation.pages.RadicationPage;
import com.kata.automation.utils.ExcelUtils;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Reporter;

public class FlujoCompletoTest extends BaseTest {

    private LoginPage loginPage;
    private OrdersPage ordersPage;
    private RadicationPage radicationPage;

    @BeforeClass
    public void setUpPages() {
        loginPage = new LoginPage(driver);
        ordersPage = new OrdersPage(driver);
        radicationPage = new RadicationPage(driver);

        // FASE 1: Autenticación única al iniciar la clase de prueba
        // System.out.println("--- FASE 1: Autenticación ---");
        // loginPage.firstContinueClickButton();
        // loginPage.doLogin("Banco2024*");
        // loginPage.afterLogin();
    }

    @Test(groups = "smoke")
    public void wholeFlowSmoke() {
        try {
            System.out.println("--- Ejecutando Smoke Test Rápido ---");

            ordersPage.setScreenDensity(380);

            // 1. Iniciar la orden con parámetros fijos
            ordersPage.createOrder("Originación de microcrédito", "SolicitudPersonaNatural");

            System.out.println("Iniciando consulta preliminar");
            ordersPage.preliminaryConsultation(
                    "Cedula ciudadanía",
                    "38569302",
                    "COLOMBIA",
                    "Hernandez",
                    "Naren",
                    10, 9, 2005, // Ajustado el año para que coincida con tus 20 años
                    "3185551310",
                    17, 9, 2020,
                    "60",
                    "Gestión front o contact center",
                    "Continuar");

            System.out.println("Finalizando consulta preliminar");
            System.out.println("Iniciando Radicación");
            radicationPage.completeRadicationExistingClient("38569302", "10",
                    "SISBEN", "Hogar", "ecolorado@greensqa.com", "VALLE DEL CAUCA",
                    "EL CERRITO", "LA ESTRELLA", "3185551311");
            System.out.println("Finalizando Radicacion");
            System.out.println("Cerrando Orden");
            ordersPage.closeOrder();
            System.out.println("Flujo finalizado correctamente");
        } catch (Exception e) {
            takeScreenshot("flujo_completo_error");
            throw new RuntimeException("El flujo completo falló.", e);
        }
    }

    // @DataProvider(name = "excelData")
    // public Object[][] getData() {
    // String excelPath = "src/test/resources/datos_solicitudes.xlsx";
    // return ExcelUtils.getTestData(excelPath, "Hoja1");
    // }

    // @Test(dataProvider = "excelData")
    // public void verificarCreacionDeOrdenMicrocredito(String orderType, String
    // personType,
    // String idType, String numberId, String country,
    // String firstName, String name, String birthDay,
    // String birthMonth, String birthYear, String numberPhone,
    // String corpDay, String corpMonth, String corpYear,
    // String longevity, String origin, String decision) {

    // System.out.println("--- FASE 2: Creación de Orden (Procesando fila del Excel)
    // ---");

    // // 1. Iniciar la orden
    // ordersPage.createOrder(orderType, personType);

    // // 2. Ejecutar consulta preliminar, validaciones y vistas
    // ordersPage.preliminaryConsultation(
    // idType,
    // numberId,
    // country,
    // firstName,
    // name,
    // Integer.parseInt(birthDay.replace(".0", "")),
    // Integer.parseInt(birthMonth.replace(".0", "")),
    // Integer.parseInt(birthYear.replace(".0", "")),
    // numberPhone,
    // Integer.parseInt(corpDay.replace(".0", "")),
    // Integer.parseInt(corpMonth.replace(".0", "")),
    // Integer.parseInt(corpYear.replace(".0", "")),
    // longevity.replace(".0", ""),
    // origin,
    // decision);

    // // 3. Cerrar y guardar la orden para dejar la app lista para la siguiente
    // fila
    // ordersPage.closeOrder();

    // String successMessage = "Orden procesada y guardada correctamente para la
    // cédula: " + numberId;
    // System.out.println(successMessage);
    // Reporter.log(successMessage, true);
    // }
}