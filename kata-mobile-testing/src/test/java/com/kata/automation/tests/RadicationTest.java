package com.kata.automation.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.kata.automation.pages.RadicationPage;

public class RadicationTest extends BaseTest{
    RadicationPage radicationPage;

    @BeforeClass
    public void setUp(){
        radicationPage = new RadicationPage(driver);
    }

    @Test(groups = "smoke")
    public void smokeTestRadicationOrder(){
        System.out.println("Ejecutando smoke");

        radicationPage.radicationProcess("ANTIOQUIA","MEDELLÍN",18,9,26);
    }
}
