package com.kata.automation.tests;

import java.util.Map;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.kata.automation.utils.DriverManager;

import io.appium.java_client.android.AndroidDriver;

public class BaseTest {
    protected static AndroidDriver driver;

    @BeforeSuite
    public void globalSetUp() {
        driver = DriverManager.getDriver();
        try {
            // Activa la ubicación en modo de alta precisión por ADB
            driver.executeScript("mobile: shell", Map.of("command", "settings put secure location_mode 3"));
        } catch (Exception e) {
            /* Ignorado */
        }
    }

    @AfterSuite
    public void globalTearDown() {
        DriverManager.quitDriver();
    }
}
