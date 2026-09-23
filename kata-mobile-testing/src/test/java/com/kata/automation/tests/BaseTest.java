package com.kata.automation.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
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

    protected void takeScreenshot(String name) {
        try {
            Path evidenceDirectory = Paths.get("evidencias");
            Files.createDirectories(evidenceDirectory);

            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = evidenceDirectory.resolve(name + "_" + System.currentTimeMillis() + ".png");
            Files.copy(sourceFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Captura guardada en: " + destination.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("No se pudo guardar la captura: " + e.getMessage());
        }
    }
}
