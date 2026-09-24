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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

/**
 * Clase base para las paginas de la aplicacion movil.
 *
 * <p>
 * Contiene operaciones comunes de Appium que pueden reutilizar las paginas
 * concretas mediante herencia. Una pagina nueva debe extender esta clase y
 * llamar a {@code super(driver)} desde su constructor.
 * </p>
 */
public class BasePage {
    protected AndroidDriver driver;
    protected WebDriverWait wait;

    private static final Locale LOCALE_ES = new Locale("es", "ES");
    private static final DateTimeFormatter DAY_ACCESSIBILITY_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy",
            LOCALE_ES);

    /** Inicializa el driver y una espera explicita de hasta 45 segundos. */
    public BasePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(50));
    }

    /** Pausa el hilo durante la cantidad de milisegundos indicada. */
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Espera hasta que un elemento sea visible. */
    protected void waitForVisibility(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Desplaza la pantalla hacia arriba usando una distancia fija en pixeles. */
    protected void scrollDownPixels(int pixels) {
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.70); // Inicia desde el 70% de la pantalla hacia abajo
        int endY = startY - pixels;

        // Evita que el swipe se salga de la pantalla
        endY = Math.max(10, endY);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(250), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    /** Desplaza la pantalla hacia abajo usando una distancia fija en pixeles. */
    protected void scrollUpPixels(int pixels) {
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.40); // Inicia desde el 40% de la pantalla hacia arriba
        int endY = startY + pixels;

        // Evita que el swipe se salga de la pantalla
        endY = Math.min(size.height - 10, endY);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(250), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    /** Desplaza la pantalla usando un porcentaje de su alto como distancia. */
    protected void scrollDownPercent(double percentAmount) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.50);
        int endY = startY - (int) (size.height * percentAmount);
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

    /** Busca un texto realizando scroll y deja el elemento listo para usarlo. */
    protected void scrollToTextAndClick(String text) {
        By locator = By.xpath("//*[normalize-space(@text)=\"" + text
            + "\" or normalize-space(@content-desc)=\"" + text + "\"]");
        int maxScrolls = 50;
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        try {
            for (int i = 0; i < maxScrolls; i++) {
                for (WebElement element : driver.findElements(locator)) {
                    if (element.isDisplayed() && isFullyInViewport(element)) {
                        clickSafely(locator);
                        return;
                    }
                }
                scrollDownPercent(0.25);
                sleep(250);
            }
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        }

        throw new RuntimeException("No se encontró el texto '" + text + "' tras " + maxScrolls + " scrolls.");
    }

    /** Busca un texto con el salto de scroll predeterminado del 20 por ciento. */
    protected void scrollToText(String text) {
        scrollToText(text, 0.20);
    }

    /** Busca un texto realizando scroll con el porcentaje indicado por intento. */
    protected void scrollToText(String text, double jumpPercent) {
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
        throw new RuntimeException("No se encontró el texto '" + text + "' tras 8 scrolls.");
    }

    /**
     * Crea un localizador XPath para un TextView cuyo texto coincide exactamente.
     */
    protected By byVisibleText(String text) {
        return By.xpath("//android.widget.TextView[@text=\"" + text + "\"]");
    }

    /** Hace clic usando el mecanismo de reintentos de {@link #clickSafely(By)}. */
    protected void click(By locator) {
        clickSafely(locator);
    }

    /** Abre un dropdown de Jetpack Compose con hasta tres intentos. */
    protected void clickComposeDropdown(By locator) {
        int maxAttempts = 3;
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                element.click();
                return;
            } catch (Exception e) {
                lastError = e;
                sleep(400);
            }
        }
        throw new RuntimeException("No se pudo abrir el dropdown " + locator + " tras " + maxAttempts + " intentos.",
                lastError);
    }

    /**
     * Simula un toque físico real en el centro del elemento usando gestos nativos de Android.
     * Ideal para evadir los clics fantasmas de Jetpack Compose.
     */
    protected void forceClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        driver.executeScript("mobile: clickGesture", java.util.Map.of(
                "elementId", ((org.openqa.selenium.remote.RemoteWebElement) element).getId()
        ));
    }

    /** Limpia un campo y escribe el texto cuando el elemento es interactuable. */
    protected void type(By locator, String text) {
        String value = text == null ? "" : text.trim();

        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.clear();
            element.sendKeys(value);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo escribir '" + value + "' en " + locator, e);
        }
    }

    /** Selecciona una fecha completa en el calendario nativo de Android. */
    protected void selectDateInCalendar(int day, int month, int year) {
        LocalDate targetDate = LocalDate.of(year, month, day);

        int startYear = readSelectedYearBeforeOpening(); // se lee ANTES de abrir el picker
        if (year != startYear) {
            clickSafely(By.id("android:id/date_picker_header_year"));
            selectYearInPicker(year, startYear);
        }
        navigateToMonth(YearMonth.of(year, month));

        String dayAccessibilityId = targetDate.format(DAY_ACCESSIBILITY_FORMAT);
        clickSafely(AppiumBy.accessibilityId(dayAccessibilityId));
        clickSafely(By.xpath(
                "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.widget.Button"));
    }

    private int readSelectedYearBeforeOpening() {
        try {
            String headerText = driver.findElement(By.id("android:id/date_picker_header_year")).getText();
            return Integer.parseInt(headerText.trim());
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo leer el año inicial del header, usando el año actual como referencia.");
            return LocalDate.now().getYear();
        }
    }

    private void selectYearInPicker(int year, int startYear) {
        boolean goBackward = year < startYear;
        By yearLocator = AppiumBy.androidUIAutomator("new UiSelector().text(\"" + year + "\")");

        int maxSteps = 30;
        String previousSource = null;
        int noProgressCount = 0;

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(200));

        for (int i = 0; i < maxSteps; i++) {
            try {
                WebElement yearElement = driver.findElement(yearLocator);
                if (yearElement.isDisplayed()) {
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(45));
                    yearElement.click();
                    return;
                }
            } catch (Exception e) {
                String currentSource = driver.getPageSource();
                if (currentSource.equals(previousSource)) {
                    noProgressCount++;
                    if (noProgressCount >= 2) {
                        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(45));
                        throw new IllegalStateException(
                                "El picker dejó de moverse buscando el año " + year + " (arrancó en " + startYear
                                        + ").");
                    }
                } else {
                    noProgressCount = 0;
                }
                previousSource = currentSource;

                // Un paso nativo de UiAutomator en la dirección correcta — más confiable que
                // pixeles fijos
                driver.findElement(AppiumBy.androidUIAutomator(
                        "new UiScrollable(new UiSelector().scrollable(true).instance(0))" +
                                ".setAsVerticalList()." + (goBackward ? "scrollBackward();" : "scrollForward();")));
            }
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(45));
        throw new IllegalStateException("No se encontró el año " + year + " tras " + maxSteps + " pasos.");
    }

    /** Navega mes a mes hasta encontrar el mes y anio solicitado. */
    private void navigateToMonth(YearMonth targetYearMonth) {
        int maxAttempts = 100;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("es", "ES"));
        By anyDayCell = By.xpath(
                "//android.view.View[@resource-id=\"android:id/month_view\"]/android.view.View[@content-desc]");

        for (int i = 0; i < maxAttempts; i++) {
            YearMonth currentYearMonth = readCurrentMonthFromCalendar(anyDayCell, formatter);
            if (currentYearMonth == null) {
                sleep(300); // aún no terminó de renderizar el grid, reintenta la lectura
                continue;
            }

            if (targetYearMonth.equals(currentYearMonth)) {
                return;
            }

            clickSafely(targetYearMonth.isBefore(currentYearMonth)
                    ? AppiumBy.accessibilityId("Mes anterior")
                    : AppiumBy.accessibilityId("Mes siguiente"));
            sleep(350);
        }
        throw new IllegalStateException("No se pudo llegar al mes/año objetivo: " + targetYearMonth);
    }

    private YearMonth readCurrentMonthFromCalendar(By anyDayCell, DateTimeFormatter formatter) {
        List<WebElement> dayCells = driver.findElements(anyDayCell);
        for (WebElement cell : dayCells) {
            String desc = cell.getAttribute("content-desc");
            if (desc == null || desc.isBlank())
                continue;
            try {
                LocalDate date = LocalDate.parse(desc.toLowerCase(new Locale("es", "ES")).trim(), formatter);
                return YearMonth.from(date);
            } catch (Exception ignored) {
                // celda vacía de otro mes o formato inesperado — sigue buscando en la siguiente
            }
        }
        return null; // ninguna celda parseable todavía
    }

    /** Responde una pregunta de tipo SI/NO identificando su texto. */
    protected void answerQuestion(String questionText, String decision) {
        String optionText = "SI".equalsIgnoreCase(decision) ? "Si" : "No";
        // Se usan comillas simples (') para envolver las variables en lugar de (\" )
        By dynamicLocator = By.xpath("//android.widget.TextView[normalize-space(@text)='" + questionText
                + "']/..//android.widget.TextView[normalize-space(@text)='" + optionText + "']");

        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            scrollToText(questionText, 0.20);
            clickSafely(dynamicLocator); // ya usa waitForStableElement internamente
            sleep(400);

            // Verifica releyendo: si el TextView de la opción sigue existiendo con el mismo
            // texto,
            // al menos confirmamos que el tap cayó sobre el elemento correcto (no lo
            // movió/rompió)
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(dynamicLocator));
                return; // el bloque de la pregunta sigue íntegro tras el click
            } catch (Exception e) {
                System.out.println("⚠️ Reintentando pregunta '" + questionText + "' (intento " + attempt + ")");
            }
        }
    }

    /**
     * Espera a que un elemento deje de moverse (fin de animación/inercia de scroll)
     * antes de retornarlo.
     */
    protected WebElement waitForStableElement(By locator, int maxChecks) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        org.openqa.selenium.Rectangle previousRect = element.getRect();

        for (int i = 0; i < maxChecks; i++) {
            sleep(120); // ventana corta para detectar si sigue en inercia
            element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            org.openqa.selenium.Rectangle currentRect = element.getRect();

            if (currentRect.equals(previousRect)) {
                return element; // dos lecturas iguales seguidas = ya se asentó
            }
            previousRect = currentRect;
        }
        return element; // no se estabilizó del todo, pero devolvemos la última lectura
    }

    /** Hace clic y usa un toque nativo como respaldo si el clic WebDriver falla. */
    protected void clickSafely(By locator) {
        int maxAttempts = 5;
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                WebElement element = waitForStableElement(locator, 3); // <-- el cambio clave
                element.click();
                return;
            } catch (Exception e) {
                lastError = e;
                System.out.println(
                        "⚠️ Intento " + attempt + " falló para " + locator + ": " + e.getClass().getSimpleName());

                try {
                    WebElement element = waitForStableElement(locator, 3);
                    driver.executeScript("mobile: clickGesture", Map.of(
                            "elementId", ((org.openqa.selenium.remote.RemoteWebElement) element).getId()));
                    System.out.println("Clic nativo ejecutado para " + locator + ".");
                    return;
                } catch (Exception fallbackError) {
                    lastError = fallbackError;
                    System.out.println("⚠️ También falló el clic nativo para " + locator + ": "
                            + fallbackError.getClass().getSimpleName());
                    sleep(400);
                }
            }
        }
        throw new RuntimeException("No se pudo hacer click en " + locator + " tras " + maxAttempts + " intentos.",
                lastError);
    }

    /**
     * Guarda una captura en evidencias/orden_<identificador> con el nombre del
     * paso.
     */
    protected void takeEvidence(String orderIdentifier, String stepName) {
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

    /** Indica si el elemento completo esta dentro de los limites visibles. */
    protected boolean isFullyInViewport(WebElement element) {
        org.openqa.selenium.Rectangle rect = element.getRect();
        org.openqa.selenium.Dimension screen = driver.manage().window().getSize();
        return rect.getY() >= 0 && (rect.getY() + rect.getHeight()) <= screen.getHeight();
    }

    /**
     * Busca un texto ancla, ajusta la posicion y hace clic en el elemento objetivo.
     */
    protected void scrollAndClickReliable(String anchorText, By targetLocator, double jumpPercent) {
        int maxAttempts = 3;
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                System.out.println("Buscando elemento " + targetLocator + " (intento " + attempt + " de "
                        + maxAttempts + ").");
                scrollToText(anchorText, jumpPercent);
                WebElement target = wait.until(ExpectedConditions.visibilityOfElementLocated(targetLocator));

                if (!isFullyInViewport(target)) {
                    scrollDownPercent(-0.08);
                }

                // Usa el clic normal y, si falla por Compose/layout, el fallback nativo.
                clickSafely(targetLocator);
                System.out.println("Clic ejecutado para " + targetLocator + ".");
                return;
            } catch (Exception e) {
                lastError = e;
                System.out.println("No se pudo hacer clic en " + targetLocator + " durante el intento " + attempt
                        + ": " + e.getClass().getSimpleName());
                sleep(400);
            }
        }
        throw new RuntimeException("No se pudo hacer click en " + targetLocator + " tras " + maxAttempts + " intentos.",
                lastError);
    }

    // Método auxiliar en BasePage o OrdersPage para leer alertas dinámicas
    public String getAlertMessageText() {
        try {
            String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜ";
            String lowercase = "abcdefghijklmnopqrstuvwxyzáéíóúü";
            WebElement alertElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath(
                            "//android.widget.TextView["
                                    + "contains(translate(@text, '" + uppercase + "', '" + lowercase
                                    + "'), 'consulta exitosa') or "
                                    + "contains(translate(@text, '" + uppercase + "', '" + lowercase
                                    + "'), 'mora mayor') or "
                                    + "contains(translate(@text, '" + uppercase + "', '" + lowercase
                                    + "'), 'registrado en listas')]")));
            return alertElement.getText();
        } catch (Exception e) {
            System.err.println("No se pudo leer el mensaje de consulta: " + e.getMessage());
            return "DESCONOCIDO";
        }
    }

    // Cambia la densidad de pantalla (ej: bajar de 450 a 380 para que quepa más
    // contenido)
    public void setScreenDensity(int densityDpi) {
        try {
            driver.executeScript("mobile: shell", Map.of("command", "wm density " + densityDpi));
            System.out.println("Densidad de pantalla ajustada a: " + densityDpi);
        } catch (Exception e) {
            System.err.println("No se pudo cambiar la densidad de pantalla: " + e.getMessage());
        }
    }

    // Restaura la densidad original del dispositivo al terminar la prueba
    public void resetScreenDensity() {
        try {
            driver.executeScript("mobile: shell", Map.of("command", "wm density reset"));
            System.out.println("Densidad de pantalla restaurada a la original.");
        } catch (Exception e) {
            System.err.println("No se pudo restablecer la densidad: " + e.getMessage());
        }
    }

    /** Hace hasta dos intentos y reintenta cuando la consulta no devuelve respuesta. */
    protected void clickWithRetry(By buttonLocator, By resultLocator, By timeoutLocator) {
        Exception lastError = null;
        int maxAttempts = 2;
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        try {
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    System.out.println("Consulta de cliente: intento " + attempt + " de " + maxAttempts);
                    clickSafely(buttonLocator);

                    WebDriverWait consultationWait = new WebDriverWait(driver, Duration.ofMinutes(1));
                    consultationWait.until(currentDriver -> {
                        boolean resultFound = !currentDriver.findElements(resultLocator).isEmpty();
                        boolean timeoutFound = !currentDriver.findElements(timeoutLocator).isEmpty();
                        return resultFound || timeoutFound;
                    });

                    if (!driver.findElements(resultLocator).isEmpty()) {
                        System.out.println("Consulta de cliente respondida en el intento " + attempt + ".");
                        return;
                    }

                    System.out.println("La aplicación informó timeout en el intento " + attempt + ".");
                } catch (TimeoutException e) {
                    lastError = e;
                    System.out.println("La consulta no respondió en el intento " + attempt + ".");
                } catch (Exception e) {
                    lastError = e;
                    System.out.println("Falló el intento " + attempt + " de consulta: " + e.getMessage());
                }

                if (attempt < maxAttempts) {
                    sleep(1000);
                }
            }
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        }

        throw new RuntimeException("La consulta de cliente no devolvió resultado tras " + maxAttempts
                + " intentos.", lastError);
    }

    public void findTextInDropDown(By locator, String location) {
        sleep(1000);
        clickComposeDropdown(locator);
        scrollToTextAndClick(location);
    }

    public void findTextWithSearch(By locator,By searchLocator, String text){
        clickComposeDropdown(locator);
        waitForVisibility(searchLocator);
        type(searchLocator, text);
        try {driver.hideKeyboard();} catch (Exception e) {}
        wait.until(ExpectedConditions.visibilityOfElementLocated(byVisibleText(text)));
        sleep(300);
        click(byVisibleText(text));
    }
}