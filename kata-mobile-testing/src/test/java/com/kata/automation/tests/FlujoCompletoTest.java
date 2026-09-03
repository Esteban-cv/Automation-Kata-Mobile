package com.kata.automation.tests;

import com.kata.automation.pages.LoginPage;
import com.kata.automation.pages.OrdersPage;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Reporter;

public class FlujoCompletoTest extends BaseTest { 

    private LoginPage loginPage;
    private OrdersPage ordersPage;

    @BeforeClass
    public void setUpPages() {
        loginPage = new LoginPage(driver);
        ordersPage = new OrdersPage(driver);
    }

    @Test
    public void verificarCreacionDeOrdenMicrocredito() {
        System.out.println("--- FASE 1: Autenticación ---");
        loginPage.firstContinueClickButton();
        loginPage.doLogin("Banco2024*");
        loginPage.afterLogin();

        System.out.println("--- FASE 2: Creación de Orden ---");
        // Aquí enganchamos directamente con los métodos que estás construyendo
        ordersPage.createOrder("Originación de microcrédito", "SolicitudPersonaNatural");
        
        //ordersPage.preliminaryConsultation("Cedula ciudadanía", 1116070669);

        String successMessage = "Flujo ejecutado correctamente";
        System.out.println(successMessage);
        Reporter.log(successMessage, true);
    }
}