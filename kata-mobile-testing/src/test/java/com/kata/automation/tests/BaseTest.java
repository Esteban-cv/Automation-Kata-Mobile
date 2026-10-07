package com.kata.automation.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.kata.automation.utils.DriverManager;
import com.kata.automation.utils.EvidenceStorage;
import com.kata.automation.utils.ExecutionLog;

import io.appium.java_client.android.AndroidDriver;

public class BaseTest {
    protected static AndroidDriver driver;

    @BeforeSuite
    public void globalSetUp() {
        driver = DriverManager.getDriver();
        try {
            // Activa el modo "No Molestar" en Android (2 = Silencio Total)
            driver.executeScript("mobile: shell", java.util.Map.of(
                    "command", "settings put global zen_mode 2"));
            // Activa la ubicación en modo de alta precisión por ADB
            driver.executeScript("mobile: shell", Map.of("command", "settings put secure location_mode 3"));
        } catch (Exception e) {
            /* Ignorado */
        }
    }

    @AfterSuite
    public void globalTearDown() {
        DriverManager.quitDriver();
        try {
            // Desactiva el modo "No Molestar" (0 = Apagado)
            driver.executeScript("mobile: shell", java.util.Map.of(
                    "command", "settings put global zen_mode 0"
            ));
        } catch (Exception e) {
            /* Ignorado */
        }
    }

    protected void takeScreenshot(String name) {
        try {
            String caseId = System.getProperty("kata.current.case.id", name);
            Path evidenceDirectory = EvidenceStorage.caseDirectory(caseId)
                    .resolve("pantallas");
            Files.createDirectories(evidenceDirectory);

            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = evidenceDirectory.resolve(name + "_" + System.currentTimeMillis() + ".png");
            Files.copy(sourceFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | RuntimeException e) {
            ExecutionLog.error("No se pudo guardar la captura", e);
        }
    }
}
