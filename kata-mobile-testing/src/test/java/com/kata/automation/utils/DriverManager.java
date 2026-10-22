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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class DriverManager {
    private static AndroidDriver driver;
    private static final String DEFAULT_APPIUM_URL = "http://127.0.0.1:4723";
    private static final Pattern OVERRIDE_DENSITY = Pattern.compile("Override density:\\s*(\\d+)");

    // Comando que devuelve la pantalla al estado previo; null cuando no hay nada pendiente.
    private static volatile String densityRestoreCommand;
    private static volatile String deviceSerial;

    static {
        // Último recurso si la JVM termina sin pasar por quitDriver (cierre de ventana, Ctrl+C).
        Runtime.getRuntime().addShutdownHook(new Thread(DriverManager::restoreDensityWithAdb,
                "kata-mobile-restore-density"));
    }

    public static AndroidDriver getDriver() {
        if (driver == null) {
            try {
                Properties configuration = loadConfiguration();
                UiAutomator2Options options = new UiAutomator2Options();
                options.setPlatformName("Android");
                deviceSerial = configuration.getProperty("device.name", "RZCX10KND8P");
                options.setDeviceName(deviceSerial);
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
                applyScreenDensity(configuration);

                System.out.println("¡Conexion a el dispositiva EXITOSA!");

            } catch (MalformedURLException e) {
                throw new RuntimeException("La URL es Invalida...");
            }

        }
        return driver;
    }

    private static void applyScreenDensity(Properties configuration) {
        if (!Boolean.parseBoolean(configuration.getProperty("screen.density.enabled", "false"))) {
            return;
        }
        String configuredDensity = configuration.getProperty("screen.density", "").trim();
        if (configuredDensity.isBlank()) {
            return;
        }
        try {
            int density = Integer.parseInt(configuredDensity);
            if (density < 120 || density > 640) {
                throw new IllegalArgumentException("Fuera del rango soportado (120-640): " + density);
            }
            if (densityRestoreCommand == null) {
                densityRestoreCommand = restoreCommandFor(density);
            }
            driver.executeScript("mobile: shell", Map.of("command", "wm density " + density));
            System.out.println("Densidad de pantalla aplicada: " + density + " dpi");
        } catch (NumberFormatException exception) {
            throw new RuntimeException("screen.density debe ser un número entero.", exception);
        } catch (RuntimeException exception) {
            throw new RuntimeException("No se pudo aplicar screen.density=" + configuredDensity, exception);
        }
    }

    /**
     * Conserva un override que el usuario ya tuviera. Si el override coincide con la densidad
     * del robot se asume que quedó de una ejecución interrumpida y se vuelve al valor de fábrica.
     */
    private static String restoreCommandFor(int automationDensity) {
        try {
            Object output = driver.executeScript("mobile: shell", Map.of("command", "wm density"));
            Matcher matcher = OVERRIDE_DENSITY.matcher(String.valueOf(output));
            if (matcher.find() && Integer.parseInt(matcher.group(1)) != automationDensity) {
                return "wm density " + matcher.group(1);
            }
        } catch (RuntimeException exception) {
            ExecutionLog.error("No se pudo leer la densidad original; se usará la de fábrica", exception);
        }
        return "wm density reset";
    }

    private static void restoreScreenDensity() {
        String command = densityRestoreCommand;
        if (command == null) {
            return;
        }
        try {
            driver.executeScript("mobile: shell", Map.of("command", command));
            densityRestoreCommand = null;
            System.out.println("Densidad de pantalla restablecida (" + command + ")");
        } catch (RuntimeException exception) {
            // La sesión puede estar caída (detención o fallo de Appium); se intenta por ADB.
            restoreDensityWithAdb();
        }
    }

    private static void restoreDensityWithAdb() {
        String command = densityRestoreCommand;
        if (command == null) {
            return;
        }
        List<String> adbCommand = new ArrayList<>(List.of(adbExecutable()));
        if (deviceSerial != null && !deviceSerial.isBlank()) {
            adbCommand.addAll(List.of("-s", deviceSerial));
        }
        adbCommand.add("shell");
        adbCommand.addAll(List.of(command.split(" ")));
        try {
            Process process = new ProcessBuilder(adbCommand).redirectErrorStream(true).start();
            if (process.waitFor(10, TimeUnit.SECONDS) && process.exitValue() == 0) {
                densityRestoreCommand = null;
                System.out.println("Densidad de pantalla restablecida por ADB (" + command + ")");
            } else {
                process.destroyForcibly();
                System.err.println("No se pudo restablecer la densidad. Ejecuta manualmente: adb shell " + command);
            }
        } catch (IOException exception) {
            System.err.println("No se pudo restablecer la densidad. Ejecuta manualmente: adb shell " + command);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private static String adbExecutable() {
        for (String variable : List.of("ANDROID_HOME", "ANDROID_SDK_ROOT")) {
            String sdk = System.getenv(variable);
            if (sdk != null && !sdk.isBlank()) {
                Path adb = Paths.get(sdk, "platform-tools", "adb.exe");
                if (Files.exists(adb)) {
                    return adb.toString();
                }
            }
        }
        return "adb";
    }

    private static Properties loadConfiguration() {
        Properties configuration = new Properties();
        String configuredPath = System.getenv("KATA_CONFIG_PATH");
        Path path = Paths.get(configuredPath == null || configuredPath.isBlank()
                ? "config/robot.properties" : configuredPath);
        if ((configuredPath == null || configuredPath.isBlank()) && !Files.exists(path)) {
            path = findConfigurationPath();
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

    private static Path findConfigurationPath() {
        Path nearApplication = configurationPathNearApplication();
        if (Files.exists(nearApplication)) {
            return nearApplication;
        }

        Path currentDirectory = Paths.get(System.getProperty("user.dir"));
        Path parentConfiguration = currentDirectory.resolve("..").resolve("config").resolve("robot.properties")
                .normalize();
        if (Files.exists(parentConfiguration)) {
            return parentConfiguration;
        }
        return nearApplication;
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

    public static synchronized void quitDriver() {
        if (driver != null) {
            restoreScreenDensity();
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
