package com.kata.automation.pages;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import org.openqa.selenium.By;
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
    private static final DateTimeFormatter DAY_ACCESSIBILITY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMMM yyyy", LOCALE_ES);
    private static final DateTimeFormatter MONTH_YEAR_HEADER_FORMAT =
            DateTimeFormatter.ofPattern("MMMM 'de' yyyy", LOCALE_ES);

    // TODO: XPaths posicionales, frágiles ante cambios en el árbol de Compose.
    // Reemplazar por resource-id / accessibility-id cuando estén disponibles.

    // ⚠️ Regresó a ser genérico (matchea cualquier botón en pantalla).
    // Si en algún momento confirmas que el texto es "CREAR", cambia a:
    // By.xpath("//android.widget.Button[@text=\"CREAR\"]")
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

    
    // selector de Año
    private static final By PICKER_HEADER_YEAR = By.id("android:id/date_picker_header_year");

    // ⚠️ Mismo caso: no confirmado en tu captura. Verifica resource-id / texto real.
    private static final By DATE_PICKER_HEADER = By.id("android:id/date_picker_header_date");
    private static final By PREVIOUS_MONTH_BUTTON = AppiumBy.accessibilityId("Mes anterior");
    private static final By NEXT_MONTH_BUTTON = AppiumBy.accessibilityId("Mes siguiente");

    // Constructor: Inicializa el driver y un wait explícito
    public OrdersPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // SCROLL DINAMICO
    private void scrollByText(String visibleText) {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(new UiSelector().text(\"" + visibleText + "\"));"));
    }

    // SCROLL FIJO
    public void scrollDownPixels(int pixels) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        int startX = 500;
        int startY = 1500;
        int endY = startY - pixels; // Restar baja la pantalla (muestra el contenido de abajo)

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(250), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    // METODO QUE ENCUENTRA TEXTOS
    private By byVisibleText(String text) {
        return By.xpath("//android.widget.TextView[@text=\"" + text + "\"]");
    }

    // Helper interno: click con espera explícita (evita repetir wait.until en cada método)
    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    // Método exclusivo para elementos de Compose que fallan con elementToBeClickable
    private void clickComposeDropdown(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.click();
    }

    // Helper interno: escritura con espera explícita + limpieza previa del campo
    private void type(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear();
        element.sendKeys(text);
    }

    public void createOrder(String type, String personType) {
        click(NEW_ORDER_BUTTON);

        // Seleccion de Tipo de originacion
        click(byVisibleText(type));

        // Selecionar tipo de persona
        click(byVisibleText(personType));

        // clic en boton "CREAR"
        click(CREATE_ORDER_BUTTON);
    }

    /**
     * Abre el calendario nativo de Android y selecciona la fecha indicada.
     * Asume que el calendario ya está abierto en pantalla (se abre desde
     * preliminaryConsultation antes de llamar a este método).
     */
    public void selectDateInCalendar(int day, int month, int year) {
        LocalDate targetDate = LocalDate.of(year, month, day);

        // 1. Abrir selector de año (evita cientos de clics en las flechas)
        click(PICKER_HEADER_YEAR);

        // 2. Scrollear la lista de años y hacer clic en el año objetivo
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().text(\"" + year + "\"));"))
                .click();

        // 3. Navegar mes a mes hasta el mes/año objetivo
        navigateToMonth(YearMonth.of(year, month));

        // 4. Clic en el día exacto por su accessibility id
        String dayAccessibilityId = targetDate.format(DAY_ACCESSIBILITY_FORMAT);
        click(AppiumBy.accessibilityId(dayAccessibilityId));
    }

    private void navigateToMonth(YearMonth targetYearMonth) {
        int maxAttempts = 400; // cubre varias décadas hacia atrás o adelante
        for (int i = 0; i < maxAttempts; i++) {
            String headerText = wait.until(ExpectedConditions.visibilityOfElementLocated(DATE_PICKER_HEADER)).getText();
            YearMonth currentYearMonth = YearMonth.parse(headerText.toLowerCase(LOCALE_ES), MONTH_YEAR_HEADER_FORMAT);

            if (targetYearMonth.equals(currentYearMonth)) {
                return;
            }
            click(targetYearMonth.isBefore(currentYearMonth) ? PREVIOUS_MONTH_BUTTON : NEXT_MONTH_BUTTON);
        }
        throw new IllegalStateException("No se pudo llegar al mes/año objetivo: " + targetYearMonth);
    }

    /**
     * Relleno de consulta preliminar completo, incluyendo fecha de nacimiento.
     */
    public void preliminaryConsultation(String idType, long numberId, String country, String firstName, String name,
            int birthDay, int birthMonth, int birthYear) {

        // // Abrir menu desplegable tipo de identificacion
        // click(OPEN_ID_TYPE_DROPDOWN);

        // // Seleccionar tipo de identificacion
        // click(byVisibleText(idType));

        // String idAsText = String.valueOf(numberId);

        // // campo de identificacion del solicitante
        // type(NUMBER_ID_INPUT, idAsText);

        // // confirmar campo de indentificacion del solicitante
        // type(NUMBER_ID_CONFIRM_INPUT, idAsText);

        // // Abrir menu desplegable Pais origen
        // clickComposeDropdown(OPEN_HOME_COUNTRY_DROPDOWN);

        // // Seleccionar pais
        // click(byVisibleText(country));

        // // Scroll Dinamico
        // scrollByText("Primer apellido solicitante");

        // // Ingresar apellido
        // type(FIRST_NAME_INPUT, firstName);

        // // Ingresar nombre
        // type(NAME_INPUT, name);

        // Abrir selector de fecha de nacimiento y seleccionar la fecha
        click(SELECT_DATE);
        selectDateInCalendar(birthDay, birthMonth, birthYear);
    }
}