package com.kata.automation.pages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class OrdersPage {
    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private static final Locale LOCALE_ES = new Locale("es", "ES");
    private static final DateTimeFormatter DAY_ACCESSIBILITY_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy",
            LOCALE_ES);

    // --- LOCALIZADORES DE ELEMENTOS ---
    private static final By CREATE_ORDER_BUTTON = By.xpath("//android.widget.Button");
    private static final By NEW_ORDER_BUTTON = By.xpath(
            "//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View[4]/android.widget.Button");
    private static final By NUMBER_ID_INPUT = By.xpath(
            "//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View[1]/android.widget.EditText[2]");
    private static final By NUMBER_ID_CONFIRM_INPUT = By.xpath(
            "//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View[1]/android.widget.EditText[3]");
    private static final By OPEN_ID_TYPE_DROPDOWN = By.xpath(
            "(//android.widget.TextView[@text=\"Seleccionar\"])[1]");
    private static final By OPEN_HOME_COUNTRY_DROPDOWN = By.xpath(
            "//android.widget.TextView[@text=\"País de origen de la identificación del solicitante\"]/following-sibling::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By FIRST_NAME_INPUT = By.xpath(
            "//android.widget.TextView[@text=\"Primer apellido solicitante\"]/following-sibling::android.widget.EditText");
    private static final By NAME_INPUT = By.xpath(
            "//android.widget.TextView[@text=\"Primer nombre solicitante\"]/following-sibling::android.widget.EditText");
    private static final By SELECT_DATE = By.xpath(
            "//android.widget.EditText[@text=\"Selecciona una fecha.\"]/android.view.View/android.view.View[2]");
    private static final By PICKER_HEADER_YEAR = By.id("android:id/date_picker_header_year");
    private static final By CURRENT_MONTH_DAY_INDICATOR = By.xpath(
            "(//android.view.View[@resource-id=\"android:id/month_view\"]/android.view.View[@content-desc])[15]");
    private static final By PREVIOUS_MONTH_BUTTON = AppiumBy.accessibilityId("Mes anterior");
    private static final By NEXT_MONTH_BUTTON = AppiumBy.accessibilityId("Mes siguiente");
    private static final By SELECT_DATE_CALENDAR = By.xpath(
            "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.widget.Button");
    private static final By SELECT_BIRTH_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"País de nacimiento del solicitante\"]/following-sibling::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By NUMBER_PHONE_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Celular del solicitante\"]/following-sibling::android.widget.EditText");
    private static final By CONFIRM_PHONE_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Confirmar Celular solicitante\"]/following-sibling::android.widget.EditText");
    private static final By BUSSINESS_INCORPORATION_DATE = By.xpath(
            "//android.widget.EditText[@text=\"Selecciona una fecha.\"]/android.view.View/android.view.View[2]");
    private static final By BUSSINESS_LONGEVITY_LOCATION = By.xpath(
            "//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View[1]/android.widget.EditText");
    private static final By ORIGIN_ORDER = By.xpath(
            "//android.widget.TextView[@text=\"Origen de solicitud\"]/following-sibling::android.view.View");

    // Selector dinámico que ubica el texto "Consultar cliente" y hace clic en el
    // contenedor (botón) inmediatamente inferior
    private static final By CONSULT_CLIENT_BUTTON = By.xpath(
            "//android.widget.TextView[@text=\"Consultar cliente\"]/following-sibling::android.view.View[1]");

    // mensaje de consulta exitosa captura
    private static final By CLIENT_ALREADY_CONSULTED_MESSAGE = By.xpath("//*[contains(@text,'Consulta exitosa')]");
    // Mensaje de consulta vista360
    private static final By VIEW360_CONSULTED_MESSAGE = By.xpath("//*[contains(@text,'Consulta Exitosa')]");
    private static final By ACCEPT_CONSULT_CLIENT_BUTTON = By.xpath("//*[@text='ACEPTAR']");
    private static final By GO_BACK = AppiumBy.accessibilityId("Ir atrás");
    private static final By YES_BUTTON_GO_BACK = By.xpath(
            "//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.widget.Button");
    // Generar vista360
    private static final By GENERATE_VIEW360_BUTTON = By.xpath(
            "//android.widget.TextView[@text=\"Vista 360 del cliente\"]/following-sibling::android.view.View[1]");

    // Decision
    // Busca el texto "Decisión", sube al bloque contenedor general y busca el campo
    // interno que dice "Seleccionar"
    private static final By DECISION_DROPDOWN = By.xpath(
            "//android.widget.TextView[@text=\"Decisión\"]/..//android.widget.TextView[@text=\"Seleccionar\"]");

    // Guardar decision
    private static final By SAVE_DECISION_BUTTON = By.xpath(
            "//android.widget.TextView[@text=\"Guardar decisión\"]/following-sibling::android.view.View[1]");

    // actualizacion exitosa
    private static final By SUCCESS_UPDATING = By.xpath("//*[contains(@text,'Actualización exitosa')]");

    public OrdersPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    private void waitForClientAlreadyConsulted(By locator) {
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void scrollDownPixels(int pixels) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        int startX = 540;
        int startY = 1500;
        int endY = startY - pixels;

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(250), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    public void scrollUpPixels(int pixels) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        int startX = 500;
        int startY = 800;
        int endY = startY + pixels;

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(250), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    public void scrollDownPercent(double percentAmount) {
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();

        int startX = size.width / 2;
        // FIX W3C: Se inicia desde el centro exacto (50%) para evitar que el teclado
        // bloquee la acción táctil
        int startY = (int) (size.height * 0.50);
        int endY = startY - (int) (size.height * percentAmount);

        // Límite de seguridad estricto para que la coordenada final no desborde la
        // pantalla
        endY = Math.max(10, Math.min(size.height - 10, endY));

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(150)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(350), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    public void scrollToTextAndClick(String text) {
        By locator = By.xpath("//android.widget.TextView[contains(@text, '" + text + "')]");
        int maxScrolls = 5;

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        for (int i = 0; i < maxScrolls; i++) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed()) {
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
                    return;
                }
            } catch (Exception e) {
                scrollDownPercent(0.35);
            }
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        throw new RuntimeException("No se encontró el texto '" + text + "' tras " + maxScrolls + " scrolls.");
    }

    public void scrollToText(String text) {
        scrollToText(text, 0.20);
    }

    public void scrollToText(String text, double jumpPercent) {
        By locator = By.xpath("//android.widget.TextView[contains(@text, '" + text + "')]");
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        for (int i = 0; i < 8; i++) {
            try {
                if (driver.findElement(locator).isDisplayed()) {
                    scrollDownPercent(0.12);
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
                    return;
                }
            } catch (Exception e) {
                scrollDownPercent(jumpPercent);
            }
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        throw new RuntimeException("No se encontró el texto '" + text + "' tras 8 scrolls."); // antes moría en silencio
    }

    private By byVisibleText(String text) {
        return By.xpath("//android.widget.TextView[@text=\"" + text + "\"]");
    }

    private void click(By locator) {
        clickSafely(locator);
    }

    private void clickComposeDropdown(By locator) {
        int maxAttempts = 3;
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                element.click();
                return;
            } catch (Exception e) {
                lastError = e;
                sleep(400); // Pausa de estabilización para menús desplegables de Compose
            }
        }
        throw new RuntimeException("No se pudo abrir el dropdown " + locator + " tras " + maxAttempts + " intentos.",
                lastError);
    }

    private void type(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear();
        element.sendKeys(text);
    }

    public void createOrder(String type, String personType) {
        click(NEW_ORDER_BUTTON);
        click(byVisibleText(type));
        click(byVisibleText(personType));
        click(CREATE_ORDER_BUTTON);
    }

    public void selectDateInCalendar(int day, int month, int year) {
        LocalDate targetDate = LocalDate.of(year, month, day);

        click(PICKER_HEADER_YEAR);

        int currentYear = LocalDate.now().getYear();
        By yearLocator = AppiumBy.androidUIAutomator("new UiSelector().text(\"" + year + "\")");

        boolean yearFound = false;
        int maxScrolls = 20;

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

        for (int i = 0; i < maxScrolls; i++) {
            try {
                WebElement yearElement = driver.findElement(yearLocator);
                if (yearElement.isDisplayed()) {
                    yearElement.click();
                    yearFound = true;
                    break;
                }
            } catch (Exception e) {
                if (year < currentYear) {
                    scrollUpPixels(450);
                } else {
                    scrollDownPixels(450);
                }
            }
        }

        if (!yearFound) {
            throw new IllegalStateException("No se encontró el año tras " + maxScrolls + " intentos.");
        }

        navigateToMonth(YearMonth.of(year, month));

        String dayAccessibilityId = targetDate.format(DAY_ACCESSIBILITY_FORMAT);
        click(AppiumBy.accessibilityId(dayAccessibilityId));

        click(SELECT_DATE_CALENDAR);
    }

    private void navigateToMonth(YearMonth targetYearMonth) {
        int maxAttempts = 100;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("es", "ES"));

        for (int i = 0; i < maxAttempts; i++) {
            String dayContentDesc = wait.until(ExpectedConditions.presenceOfElementLocated(CURRENT_MONTH_DAY_INDICATOR))
                    .getAttribute("content-desc");

            String cleanDateText = dayContentDesc.toLowerCase(new Locale("es", "ES")).trim();
            LocalDate currentDate = LocalDate.parse(cleanDateText, formatter);
            YearMonth currentYearMonth = YearMonth.from(currentDate);

            if (targetYearMonth.equals(currentYearMonth)) {
                return;
            }

            if (targetYearMonth.isBefore(currentYearMonth)) {
                click(PREVIOUS_MONTH_BUTTON);
            } else {
                click(NEXT_MONTH_BUTTON);
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        throw new IllegalStateException("No se pudo llegar al mes/año objetivo: " + targetYearMonth);
    }

    public void answerQuestion(String questionText, String decision) {
        String optionText = "SI".equalsIgnoreCase(decision) ? "Si" : "No";

        By dynamicLocator = By.xpath(
                "//android.widget.TextView[normalize-space(@text)=\"" + questionText + "\"]" +
                        "/..//android.widget.TextView[normalize-space(@text)=\"" + optionText + "\"]");

        scrollToText(questionText, 0.20); // red de seguridad: si ya está visible, esto es casi gratis
        clickSafely(dynamicLocator);
        sleep(400);
    }

    private void clickSafely(By locator) {
        int maxAttempts = 3;
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                return; // éxito
            } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                lastError = e;
                sleep(300); // pequeña pausa para que el layout se estabilice
            }
        }

        throw new RuntimeException("No se pudo hacer click en " + locator + " tras " + maxAttempts + " intentos.",
                lastError);
    }

    public void takeEvidence(String orderIdentifier, String stepName) {
        try {
            String folderPath = "evidencias/orden_" + orderIdentifier;
            Files.createDirectories(Paths.get(folderPath));

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(folderPath + "/" + stepName + "_" + System.currentTimeMillis() + ".png");

            Files.copy(srcFile.toPath(), destFile.toPath());
            System.out.println("Evidencia guardada: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al guardar la evidencia: " + e.getMessage());
        }
    }

    private boolean isFullyInViewport(WebElement element) {
        org.openqa.selenium.Rectangle rect = element.getRect();
        org.openqa.selenium.Dimension screen = driver.manage().window().getSize();
        return rect.getY() >= 0 && (rect.getY() + rect.getHeight()) <= screen.getHeight();
    }

    private void scrollAndClickReliable(String anchorText, By targetLocator, double jumpPercent) {
        int maxAttempts = 3;
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                scrollToText(anchorText, jumpPercent);

                WebElement target = wait.until(ExpectedConditions.visibilityOfElementLocated(targetLocator));

                // Si el "ajuste fino" lo dejó cortado o fuera de pantalla, corrige
                if (!isFullyInViewport(target)) {
                    scrollDownPercent(-0.08); // sube un poco (negativo = hacia arriba)
                    target = wait.until(ExpectedConditions.visibilityOfElementLocated(targetLocator));
                }

                target.click();
                return; // éxito
            } catch (Exception e) {
                lastError = e;
                sleep(400);
            }
        }

        throw new RuntimeException("No se pudo hacer click en " + targetLocator + " tras " + maxAttempts + " intentos.",
                lastError);
    }

    public void preliminaryConsultation(String idType, long numberId, String country, String firstName, String name,
            int birthDay, int birthMonth, int birthYear, String numberPhone, int corporationDay, int corporationMonth,
            int corporationYear, String bussinessLongevity, String originOrderText, String decision) {

        click(OPEN_ID_TYPE_DROPDOWN);
        click(byVisibleText(idType));

        String idAsText = String.valueOf(numberId);
        type(NUMBER_ID_INPUT, idAsText);
        type(NUMBER_ID_CONFIRM_INPUT, idAsText);

        try {
            driver.hideKeyboard();
        } catch (Exception e) {
            /* Ignorar si ya está cerrado */ }

        // 📌 PRIMERA EVIDENCIA: Cédula ingresada y confirmada
        takeEvidence(idAsText, "01_cedula_ingresada");

        // Sección: país + nombres (van en la misma pantalla, un solo salto grande)
        scrollAndClickReliable("País de origen de la identificación del solicitante", OPEN_HOME_COUNTRY_DROPDOWN, 0.30);
        click(byVisibleText(country));
        sleep(500);

        type(FIRST_NAME_INPUT, firstName); // ya visible, sin re-scroll

        scrollToText("Primer nombre solicitante", 0.30);
        type(NAME_INPUT, name);

        // Sección: fecha de nacimiento
        scrollToText("Fecha de nacimiento del solicitante", 0.25);
        click(SELECT_DATE);
        selectDateInCalendar(birthDay, birthMonth, birthYear);

        clickComposeDropdown(SELECT_BIRTH_CLIENT);
        click(byVisibleText(country));

        // Sección: país nacimiento + celular (suelen ir juntos)
        scrollToText("Aceptación políticas", 0.25);
        type(NUMBER_PHONE_CLIENT, numberPhone);
        type(CONFIRM_PHONE_CLIENT, numberPhone);

        // Sección: las 4 preguntas SI/NO — un solo scroll grande al bloque, luego
        // responden sin re-scroll
        answerQuestion("¿Autoriza el tratamiento de datos personales?", "SI");
        answerQuestion("¿Autoriza la consulta y reporte a las centrales de información?", "SI");
        answerQuestion("¿Acepta Autorización de canales, horarios de contacto y visitas?", "SI");
        answerQuestion("¿La solicitud de crédito es una refinanciación o reestructuración?", "NO");

        // Sección: fecha de constitución + antigüedad del negocio (texto corregido)
        scrollToText("Fecha de constitución del negocio", 0.25);
        click(BUSSINESS_INCORPORATION_DATE);
        selectDateInCalendar(corporationDay, corporationMonth, corporationYear);
        type(BUSSINESS_LONGEVITY_LOCATION, bussinessLongevity); // ya visible debajo, sin re-scroll

        // Sección: origen de solicitud
        scrollToText("Origen de solicitud", 0.30);
        sleep(500);
        clickComposeDropdown(ORIGIN_ORDER);
        click(byVisibleText(originOrderText));

        click(CONSULT_CLIENT_BUTTON);
        waitForClientAlreadyConsulted(CLIENT_ALREADY_CONSULTED_MESSAGE);
        click(ACCEPT_CONSULT_CLIENT_BUTTON);

        // Sección: origen de solicitud
        scrollToText("Decisión consulta preliminar", 0.30);

        // Generacion de la vista360
        click(GENERATE_VIEW360_BUTTON);
        waitForClientAlreadyConsulted(VIEW360_CONSULTED_MESSAGE);
        click(ACCEPT_CONSULT_CLIENT_BUTTON);

        // Sección: Guardar decision
        scrollToText("Guardar decisión", 0.30);

        sleep(500);
        // aprobar decision
        clickComposeDropdown(DECISION_DROPDOWN);
        click(byVisibleText(decision));

        sleep(500);

        // Guardar decision
        click(SAVE_DECISION_BUTTON);
        // Aceptar la decision
        waitForClientAlreadyConsulted(SUCCESS_UPDATING);
        // 📌 SEGUNDA EVIDENCIA: Modal con el mensaje de consulta exitosa antes de
        // aceptar
        takeEvidence(idAsText, "02_resultado_consulta");
        click(ACCEPT_CONSULT_CLIENT_BUTTON);

    }

    public void closeOrder() {
        sleep(800);
        click(GO_BACK);
        sleep(500);
        click(YES_BUTTON_GO_BACK);
        sleep(1000);
    }
}