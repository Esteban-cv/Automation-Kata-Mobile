package com.kata.automation.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.kata.automation.pages.OrdersPage;

public class OrdersTest extends BaseTest {
    private OrdersPage ordersPage;

    @BeforeClass
    public void setUp() {
        ordersPage = new OrdersPage(driver);
    }

    @Test
    public void createWholeOrder() {
        //ordersPage.createOrder("Originación de microcrédito", "SolicitudPersonaNatural");
        ordersPage.preliminaryConsultation("Cedula ciudadanía", 1116070669, "COLOMBIA", "Colorado", "Esteban", 26,10,2004);
    }
}
