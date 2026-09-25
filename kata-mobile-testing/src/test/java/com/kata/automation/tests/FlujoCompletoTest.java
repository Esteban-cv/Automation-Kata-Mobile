package com.kata.automation.tests;

import com.kata.automation.pages.LoginPage;
import com.kata.automation.pages.OrdersPage;
import com.kata.automation.pages.ProductRegistrationPage;
import com.kata.automation.pages.RadicationPage;
import com.kata.automation.pages.AnalysisDecisionPage;
import com.kata.automation.pages.AnalysisPage;
import com.kata.automation.utils.ExcelUtils;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Reporter;

public class FlujoCompletoTest extends BaseTest {

    private LoginPage loginPage;
    private OrdersPage ordersPage;
    private RadicationPage radicationPage;
    private ProductRegistrationPage productRegistrationPage;
    private AnalysisPage analysisPage;
    private AnalysisDecisionPage analysisDecisionPage;

    @BeforeClass
    public void setUpPages() {
        loginPage = new LoginPage(driver);
        ordersPage = new OrdersPage(driver);
        radicationPage = new RadicationPage(driver);
        productRegistrationPage = new ProductRegistrationPage(driver);
        analysisPage = new AnalysisPage(driver);
        analysisDecisionPage = new AnalysisDecisionPage(driver);

        System.out.println("--- FASE 1: Autenticación única ---");
        //loginPage.doLogin("Banco2025*");
        ordersPage.setScreenDensity(380);
    }

    // @Test(groups = "smoke")
    // public void wholeFlowSmoke() {
    //     try {
    //         System.out.println("--- Ejecutando Smoke Test Rápido ---");

    //         loginPage.doLogin("Banco2024*");

    //         ordersPage.setScreenDensity(380);

    //         // 1. Iniciar la orden con parámetros fijos
    //         ordersPage.createOrder("Originación de microcrédito", "SolicitudPersonaNatural");

    //         System.out.println("Iniciando consulta preliminar");
    //         ordersPage.preliminaryConsultation(
    //                 "Cedula ciudadanía",
    //                 "38569302",
    //                 "COLOMBIA",
    //                 "Hernandez",
    //                 "Naren",
    //                 10, 9, 2005,
    //                 "3185551310",
    //                 17, 9, 2020,
    //                 "60",
    //                 "Gestión front o contact center");

    //         System.out.println("Finalizando consulta preliminar");
    //         System.out.println("Iniciando Radicación");
    //         radicationPage.completeRadicationExistingClient("38569302", "10",
    //                 "SISBEN", "Hogar", "ecolorado@greensqa.com", "VALLE DEL CAUCA",
    //                 "EL CERRITO", "LA ESTRELLA", "3185551311");
    //         System.out.println("Finalizando Radicacion");
    //         System.out.println("Cerrando Orden");
    //         ordersPage.closeOrder();
    //         System.out.println("Flujo finalizado correctamente");
    //     } catch (Exception e) {
    //         takeScreenshot("flujo_completo_error");
    //         throw new RuntimeException("El flujo completo falló.", e);
    //     }
    // }

    @DataProvider(name = "excelData", parallel = false)
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
            String longevity, String origin, String email, String analystDept,
            String analysCity, String analystNeighborhood,
            String lastYearCompleted, String affiliation, String networkAccess,
            String businessPhone,
            String productType, String product, String amount, String term,
            String paymentFreq, String destination, String hasFNG,
            String hasBail, String hasRealGuarantee, String comprehensiveInsurance,
            String coDebtors, String hasFamilyInsurance,
            String hasHomeBussinessInsurance, String hasMicroEntrepreneursInsurance,
            String hasLifeInsurance, String hasFuneralPlanning,
            String disbDay, String disbMonth, String disbYear, String hasSavings,
            String interviewPlace, String interviewResult,
            String contractualPurpose, String extractsInfo, String analystConcept,
            String analysisDecision) {

        System.out.println("--- Ejecutando flujo completo con datos del Excel ---");

        // 1. Iniciar la orden
        ordersPage.createOrder(orderType, personType);

        // 2. Ejecutar consulta preliminar, validaciones y vistas
        boolean canContinueToRadication = ordersPage.preliminaryConsultation(
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
                origin
            );

        if (!canContinueToRadication) {
            System.out.println("Orden rechazada en consulta preliminar para la cédula: " + numberId);
            return;
        }

        // 3. Completar radicación solo si la consulta preliminar fue aprobada
        radicationPage.completeRadicationExistingClient(numberId, lastYearCompleted,
            affiliation, networkAccess, email, analystDept,
            analysCity, analystNeighborhood, businessPhone);

        // 4. Completar el análisis cualitativo y cuantitativo
        analysisPage.fillQualitativeAndQuantitativeAnalysis();

        // 5. Registrar y simular el producto
        productRegistrationPage.fillProductRegistration(
            numberId, productType, product, amount, term, paymentFreq,
            destination, hasFNG, hasBail, hasRealGuarantee,
            comprehensiveInsurance, coDebtors, hasFamilyInsurance,
            hasHomeBussinessInsurance, hasMicroEntrepreneursInsurance,
            hasLifeInsurance, hasFuneralPlanning,
            parseInt(disbDay), parseInt(disbMonth), parseInt(disbYear), hasSavings);

        // 6. Procesar la decisión final del análisis
        analysisDecisionPage.fillAnalysisDecision(
            interviewPlace, interviewResult, contractualPurpose,
            extractsInfo, analystConcept, analysisDecision, numberId);

        // 7. Cerrar y guardar la orden para dejar la app lista para la siguiente fila
        ordersPage.closeOrder();

        String successMessage = "Flujo procesado y guardado correctamente para la cédula: " + numberId;
        System.out.println(successMessage);
        Reporter.log(successMessage, true);
    }

    private int parseInt(String value) {
        return Integer.parseInt(value.replace(".0", ""));
    }
}