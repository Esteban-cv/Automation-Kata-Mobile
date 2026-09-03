package com.kata.automation.tests;

import com.kata.automation.pages.LoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    //private WelcomePage welcomePage;
    private LoginPage loginPage;

    @BeforeClass
    public void setUp() {
        // Ya NO se pide el driver aquí, se usa el heredado de BaseTest
        //welcomePage = new WelcomePage(driver);
        loginPage = new LoginPage(driver);
    }

    @Test
    public void verifyWholeFlowLogin() {

        // 1. Primera pantalla: Ingresamos el dato inicial y continuamos
        //welcomePage.completeWelcomePage("mfbwqa");
        loginPage.firstContinueClickButton();
        // 2. Segunda pantalla: Ya estamos en el login, ingresamos credenciales
        loginPage.doLogin("Banco2024*");

        // 3. Tercera pantalla final del login -> Menú
        loginPage.afterLogin();
    }
}