package com.kata.automation.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class DriverManager {
    private static AndroidDriver driver;
    private static final String DEFAULT_APPIUM_URL = "http://127.0.0.1:4723";

    public static AndroidDriver getDriver() {
        if (driver == null) {
            try {
                UiAutomator2Options options = new UiAutomator2Options();
                options.setPlatformName("Android");
                options.setDeviceName("RZCX10KND8P");
                options.setAppPackage("com.kata.mobile");
                options.setAutomationName("UiAutomator2");

                // Mantiene los datos y la caché de la app entre sesiones de Appium
                // para que no se reinicie desde cero en cada prueba.
                options.setNoReset(true);
                options.setFullReset(false);
                options.setCapability("appium:dontStopAppOnReset", true);
                options.setCapability("appium:forceAppLaunch", true);

                String appiumUrl = System.getenv("APPIUM_SERVER_URL");
                if (appiumUrl == null || appiumUrl.isBlank()) {
                    appiumUrl = DEFAULT_APPIUM_URL;
                }

                URL appiumServer = new URL(appiumUrl);
                driver = new AndroidDriver(appiumServer,options);

                //Espera implicita de 10 segundos
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

                System.out.println("¡Conexion a el dispositiva EXITOSA!");

            } catch (MalformedURLException e) {
                throw new RuntimeException("La URL es Invalida...");
            }
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            System.out.println("Sesion de Appium finalizada correctamente...");
        }
    }
}
