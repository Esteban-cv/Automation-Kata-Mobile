package com.kata.automation.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.kata.automation.pages.ProductRegistrationPage;

public class ProductRegistrationTest extends BaseTest {
    private ProductRegistrationPage productRegistrationPage;

    @BeforeClass
    public void setUp() {
        productRegistrationPage = new ProductRegistrationPage(driver);
    }

    @Test(groups = "smoke")
    public void smokeTestProductRegister() {
        System.out.println("Iniciando prueba en registro de producto");

        productRegistrationPage.fillProductRegistration(
                "38569302",
                "Microcrédito",
                "Crédito productivo urbano360",
                "20000000",
                "24",
                "30",
                "Capital de Trabajo",
                "No",
                "No",
                "No",
                "No",
                "No",
                "NO",
                "NO",
                "NO",
                "NO",
                "NO",
                28,
                9,
                2026,
                "No");
    }
}
