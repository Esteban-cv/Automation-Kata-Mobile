package com.kata.automation.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class DriverManager {
    private static AndroidDriver driver;
    private static final String DEFAULT_APPIUM_URL = "http://127.0.0.1:4723";

    public static AndroidDriver getDriver() {
        if (driver == null) {
            try {
                Properties configuration = loadConfiguration();
                UiAutomator2Options options = new UiAutomator2Options();
                options.setPlatformName("Android");
                options.setDeviceName(configuration.getProperty("device.name", "RZCX10KND8P"));
                options.setAppPackage(configuration.getProperty("app.package", "com.kata.mobile"));
                options.setAutomationName(configuration.getProperty("automation.name", "UiAutomator2"));

                // Mantiene los datos y la caché de la app entre sesiones de Appium
                // para que no se reinicie desde cero en cada prueba.
                options.setNoReset(true);
                options.setFullReset(false);
                options.setCapability("appium:dontStopAppOnReset", true);
                options.setCapability("skipUnlock", true);
                //options.setCapability("appium:forceAppLaunch", true);
                options.setCapability("disableWindowAnimation", true);

                String appiumUrl = configuration.getProperty("appium.server.url", DEFAULT_APPIUM_URL);
                String environmentUrl = System.getenv("APPIUM_SERVER_URL");
                if (environmentUrl != null && !environmentUrl.isBlank()) {
                    appiumUrl = environmentUrl;
                }

                URL appiumServer = new URL(appiumUrl);
                driver = new AndroidDriver(appiumServer,options);

                long implicitTimeout = Long.parseLong(
                    configuration.getProperty("implicit.timeout.seconds", "10"));
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitTimeout));

                System.out.println("¡Conexion a el dispositiva EXITOSA!");

            } catch (MalformedURLException e) {
                throw new RuntimeException("La URL es Invalida...");
            }
        }
        return driver;
    }

    private static Properties loadConfiguration() {
        Properties configuration = new Properties();
        String configuredPath = System.getenv("KATA_CONFIG_PATH");
        Path path = Paths.get(configuredPath == null || configuredPath.isBlank()
                ? "config/robot.properties" : configuredPath);
        if ((configuredPath == null || configuredPath.isBlank()) && !Files.exists(path)) {
            path = configurationPathNearApplication();
        }
        if (!Files.exists(path)) {
            return configuration;
        }
        try (InputStream input = Files.newInputStream(path)) {
            configuration.load(input);
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo leer la configuracion del robot: " + path, exception);
        }
        return configuration;
    }

    private static Path configurationPathNearApplication() {
        try {
            Path applicationLocation = Paths.get(DriverManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            Path applicationDirectory = Files.isDirectory(applicationLocation)
                    ? applicationLocation : applicationLocation.getParent();
            return applicationDirectory.resolve("config").resolve("robot.properties");
        } catch (URISyntaxException | NullPointerException exception) {
            return Paths.get("config", "robot.properties");
        }
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            System.out.println("Sesion de Appium finalizada correctamente...");
        }
    }

    public static void restartApplication() {
        if (driver == null) {
            return;
        }
        Properties configuration = loadConfiguration();
        String appPackage = configuration.getProperty("app.package", "com.kata.mobile");
        try {
            driver.terminateApp(appPackage);
            driver.activateApp(appPackage);
        } catch (RuntimeException exception) {
            throw new RuntimeException("No se pudo reiniciar la aplicación para el reintento.", exception);
        }
    }
}
