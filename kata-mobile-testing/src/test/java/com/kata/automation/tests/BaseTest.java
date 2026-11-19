package com.kata.automation.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
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

    /**
     * Comandos que silencian el celular durante la ejecución: una notificación que cae
     * encima de la app (o un toque sobre ella) saca al robot del flujo.
     * <ul>
     * <li>{@code cmd notification set_dnd on}: No molestar (Android 9+). El antiguo
     * {@code settings put global zen_mode 2} se deja como respaldo para equipos viejos.</li>
     * <li>{@code heads_up_notifications_enabled 0}: quita los avisos flotantes que se
     * despliegan sobre la pantalla.</li>
     * </ul>
     */
    private static final List<String> SILENCE_COMMANDS = List.of(
            "cmd notification set_dnd on",
            "settings put global zen_mode 2",
            "settings put global heads_up_notifications_enabled 0");
    private static final List<String> RESTORE_COMMANDS = List.of(
            "cmd notification set_dnd off",
            "settings put global zen_mode 0",
            "settings put global heads_up_notifications_enabled 1");

    @BeforeSuite
    public void globalSetUp() {
        driver = DriverManager.getDriver();
        runShell(SILENCE_COMMANDS, "silenciar notificaciones");
        // Activa la ubicación en modo de alta precisión por ADB
        runShell(List.of("settings put secure location_mode 3"), "activar ubicación");
    }

    @AfterSuite(alwaysRun = true)
    public void globalTearDown() {
        // Devuelve las notificaciones a la normalidad antes de cerrar la sesión.
        runShell(RESTORE_COMMANDS, "restaurar notificaciones");
        DriverManager.quitDriver();
    }

    /** Ejecuta comandos de shell en el celular; si uno falla lo deja en el log y sigue. */
    private static void runShell(List<String> commands, String purpose) {
        for (String command : commands) {
            try {
                driver.executeScript("mobile: shell", Map.of("command", command));
            } catch (RuntimeException exception) {
                ExecutionLog.error("No se pudo " + purpose + " con '" + command
                        + "' (¿Appium sin --relaxed-security?)", exception);
            }
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
