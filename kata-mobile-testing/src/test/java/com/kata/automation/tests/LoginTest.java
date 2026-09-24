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
        loginPage.doLogin("Banco2024*");
    }
}