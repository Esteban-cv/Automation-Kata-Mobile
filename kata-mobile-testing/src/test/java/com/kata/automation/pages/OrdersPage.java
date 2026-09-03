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

    // Configuración regional y formateadores de fecha para parsing y accesibilidad
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

    // Selector del año en la cabecera del calendario nativo
    private static final By PICKER_HEADER_YEAR = By.id("android:id/date_picker_header_year");

    // Localiza el día 15 en la vista del mes para extraer dinámicamente el mes/año
    // actual
    private static final By CURRENT_MONTH_DAY_INDICATOR = By.xpath(
            "(//android.view.View[@resource-id=\"android:id/month_view\"]/android.view.View[@content-desc])[15]");

    private static final By PREVIOUS_MONTH_BUTTON = AppiumBy.accessibilityId("Mes anterior");
    private static final By NEXT_MONTH_BUTTON = AppiumBy.accessibilityId("Mes siguiente");

    private static final By SELECT_DATE_CALENDAR = By.xpath(
            "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.widget.Button");

    private static final By SELECT_BIRTH_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"País de nacimiento del solicitante\"]/following-sibling::android.view.View[@content-desc=\"Menú desplegable\"]");

    // Selector del celular del solicitante basado en su etiqueta de texto
    private static final By NUMBER_PHONE_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Celular del solicitante\"]/following-sibling::android.widget.EditText");

    // Selector del campo de confirmación de celular basado en su etiqueta de texto
    private static final By CONFIRM_PHONE_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Confirmar Celular solicitante\"]/following-sibling::android.widget.EditText");

    // Selector fecha constitucion del negocio
    private static final By BUSSINESS_INCORPORATION_DATE = By.xpath(
            "//android.widget.EditText[@text=\"Selecciona una fecha.\"]/android.view.View/android.view.View[2]");

    // Selector de antiguedad del negocio en la ubicacion actual
    private static final By BUSSINESS_LONGEVITY_LOCATION = By.xpath(
            "//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View[1]/android.widget.EditText");

    // Selector de origen de solicitud
    private static final By ORIGIN_ORDER = By.xpath(
            "//android.widget.TextView[@text=\"Origen de solicitud\"]/following-sibling::android.view.View[@content-desc=\"Menú desplegable\"]");

    // Boton de consultar cliente
    private static final By CONSULT_CLIENT_BUTTON = By.xpath(
            "//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View[1]/android.view.View[4]");

    // Boton aceptar consultar cliente
    private static final By ACCEPT_CONSULT_CLIENT_BUTTON = By.className("android.widget.Button");

    //**************************************IR ATRAS**************************************************** */
    private static final By GO_BACK = AppiumBy.accessibilityId("Ir atrás");

    // boton "SI" para ir atras
    private static final By YES_BUTTON_GO_BACK = By.xpath(
            "//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.widget.Button");

    // Constructor: Inicializa el driver y un WebDriverWait predeterminado de 30
    // segundos
    public OrdersPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /**
     * Realiza un scroll dinámico robusto buscando un texto visible en la pantalla.
     * Forzamos la búsqueda dentro del contenedor scrollable principal de la app.
     */
    private void scrollByText(String visibleText) {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true).instance(0))" +
                        ".setAsVerticalList()" +
                        ".scrollIntoView(new UiSelector().textContains(\"" + visibleText + "\").instance(0));"));
    }

    /**
     * Realiza un gesto de scroll hacia abajo usando coordenadas W3C (PointerInput).
     */
    public void scrollDownPixels(int pixels) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        int startX = 360; // Exactamente la mitad de los 720px de ancho (centrado)
        int startY = 1100; // Zona segura en la parte baja de la pantalla de 1600px
        int endY = startY - pixels; // Restar desplaza la vista hacia abajo

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(250), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    /**
     * Realiza un gesto de scroll hacia arriba usando coordenadas W3C para buscar
     * elementos en el pasado (ej. años anteriores).
     */
    public void scrollUpPixels(int pixels) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        int startX = 500;
        int startY = 800; // Posición central dentro del modal
        int endY = startY + pixels; // Sumar desplaza el contenido hacia abajo (revela elementos superiores)

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(250), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    /**
     * Genera un localizador dinámico By buscando por texto exacto de TextView.
     */
    private By byVisibleText(String text) {
        return By.xpath("//android.widget.TextView[@text=\"" + text + "\"]");
    }

    /**
     * Helper de clic con espera explícita para asegurar que el elemento sea
     * clickeable.
     */
    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Helper especializado para componentes de Jetpack Compose que fallan con
     * elementToBeClickable.
     */
    private void clickComposeDropdown(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.click();
    }

    /**
     * Helper para campos de texto: limpia el input y escribe el valor indicado.
     */
    private void type(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Flujo inicial para la creación de una orden seleccionando su tipo y
     * categoría.
     */
    public void createOrder(String type, String personType) {
        click(NEW_ORDER_BUTTON);
        click(byVisibleText(type));
        click(byVisibleText(personType));
        click(CREATE_ORDER_BUTTON);
    }

    /**
     * Método orquestador del calendario: maneja la búsqueda inteligente del año,
     * navegación mes a mes y selección exacta del día.
     */
    public void selectDateInCalendar(int day, int month, int year) {
        LocalDate targetDate = LocalDate.of(year, month, day);

        // 1. Abrir selector de año en la cabecera del calendario
        click(PICKER_HEADER_YEAR);

        // 2. Búsqueda y scroll bidireccional optimizado para encontrar el año objetivo
        int currentYear = LocalDate.now().getYear();
        By yearLocator = AppiumBy.androidUIAutomator("new UiSelector().text(\"" + year + "\")");

        boolean yearFound = false;
        int maxScrolls = 10;

        // Desactiva temporalmente las esperas implícitas para acelerar la detección
        // visual
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
                // Si el año no está visible, realiza scroll inteligente hacia el pasado o
                // futuro
                if (year < currentYear) {
                    scrollUpPixels(400);
                } else {
                    scrollDownPixels(400);
                }
            }
        }

        if (!yearFound) {
            throw new IllegalStateException("No se encontró el año tras " + maxScrolls + " intentos.");
        }

        // 3. Navegar mes a mes de forma automatizada hasta el mes/año objetivo
        navigateToMonth(YearMonth.of(year, month));

        // 4. Seleccionar el día exacto usando su accessibility id formateado
        String dayAccessibilityId = targetDate.format(DAY_ACCESSIBILITY_FORMAT);
        click(AppiumBy.accessibilityId(dayAccessibilityId));

        // 5. Confirmar la selección en el botón final del calendario
        click(SELECT_DATE_CALENDAR);
    }

    /**
     * Navega dinámicamente mes a mes comparando la fecha actual leída del DOM
     * contra el objetivo.
     */
    private void navigateToMonth(YearMonth targetYearMonth) {
        int maxAttempts = 100;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("es", "ES"));

        for (int i = 0; i < maxAttempts; i++) {
            // Extrae la fecha en texto desde el content-desc del día indicador
            String dayContentDesc = wait.until(ExpectedConditions.presenceOfElementLocated(CURRENT_MONTH_DAY_INDICATOR))
                    .getAttribute("content-desc");

            // Normaliza el texto para adaptarlo al formato de parsing de Java
            String cleanDateText = dayContentDesc.toLowerCase(new Locale("es", "ES")).trim();
            LocalDate currentDate = LocalDate.parse(cleanDateText, formatter);
            YearMonth currentYearMonth = YearMonth.from(currentDate);

            // Valida si ya nos encontramos en el mes y año deseados
            if (targetYearMonth.equals(currentYearMonth)) {
                return;
            }

            // Decide si presiona el botón de mes anterior o siguiente según la comparativa
            if (targetYearMonth.isBefore(currentYearMonth)) {
                click(PREVIOUS_MONTH_BUTTON);
            } else {
                click(NEXT_MONTH_BUTTON);
            }

            // Pausa breve para estabilizar las animaciones nativas del componente visual
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        throw new IllegalStateException("No se pudo llegar al mes/año objetivo: " + targetYearMonth);
    }

    /**
     * Responde de forma dinámica a cualquier pregunta de opción múltiple (Sí/No)
     * basándose en el texto de la pregunta.
     * 
     * @param questionText Texto exacto de la pregunta (ej. "¿Autoriza el
     *                     tratamiento de datos personales?")
     * @param decision     "SI" o "NO"
     */
    public void answerQuestion(String questionText, String decision) {
        // Traduce la decisión a cómo se muestra el texto en la app ("Si" o "No")
        String optionText = "SI".equalsIgnoreCase(decision) ? "Si" : "No";

        // XPath inteligente: Busca la pregunta, sube al contenedor padre y busca el
        // "Si" o "No" correspondiente
        By dynamicLocator = By.xpath(
                "//android.widget.TextView[@text=\"" + questionText + "\"]/..//android.widget.TextView[@text=\""
                        + optionText + "\"]");

        wait.until(ExpectedConditions.elementToBeClickable(dynamicLocator)).click();
    }

    /**
     * Relleno completo de la consulta preliminar y datos del solicitante,
     * incluyendo fecha de nacimiento.
     */
    public void preliminaryConsultation(String idType, long numberId, String country, String firstName, String name,
            int birthDay, int birthMonth, int birthYear, String numberPhone, int corporationDay, int corporationMonth,
            int corporationYear, String bussinessLongevity, String originOrderText) {

        // Selección de tipo de identificación
        click(OPEN_ID_TYPE_DROPDOWN);
        click(byVisibleText(idType));

        String idAsText = String.valueOf(numberId);
        type(NUMBER_ID_INPUT, idAsText);
        type(NUMBER_ID_CONFIRM_INPUT, idAsText);

        // Selección del país de origen del documento
        clickComposeDropdown(OPEN_HOME_COUNTRY_DROPDOWN);
        click(byVisibleText(country));

        // Desplazamiento dinámico para rellenar nombres y apellidos
        scrollByText("Primer apellido solicitante");
        type(FIRST_NAME_INPUT, firstName);
        type(NAME_INPUT, name);

        // Fecha de nacimiento del cliente
        click(SELECT_DATE);
        selectDateInCalendar(birthDay, birthMonth, birthYear);

        // *************************************** BIEN
        // *****************************************/

        // 1. Salto corto para revelar país de nacimiento y teléfonos del cliente
        scrollDownPixels(450);

        // Selección del pais de nacimiento
        clickComposeDropdown(SELECT_BIRTH_CLIENT);
        click(byVisibleText(country));

        // Diligenciar campos de celular del cliente
        type(NUMBER_PHONE_CLIENT, numberPhone);
        type(CONFIRM_PHONE_CLIENT, numberPhone);

        // 2. Salto medio para mostrar el bloque completo de autorizaciones (Sí/No)
        scrollDownPixels(550);

        answerQuestion("¿Autoriza el tratamiento de datos personales?", "SI");
        answerQuestion("¿Autoriza la consulta y reporte a las centrales de información?", "SI");
        answerQuestion("¿Acepta Autorización de canales, horarios de contacto y visitas?", "SI");
        // 2. Salto medio para mostrar el bloque completo de autorizaciones (Sí/No)
        scrollDownPixels(300);
        answerQuestion("¿La solicitud de crédito es una refinanciación o reestructuración?", "NO");

        // 3. Salto para llegar a la fecha de constitución del negocio
        scrollDownPixels(500);

        // Seleccionar fecha de constitución del negocio
        click(BUSSINESS_INCORPORATION_DATE);
        selectDateInCalendar(corporationDay, corporationMonth, corporationYear);

        // Diligenciar meses del negocio en la ubicacion actual
        type(BUSSINESS_LONGEVITY_LOCATION, bussinessLongevity);

        // Seleccionar el origen de la orden
        clickComposeDropdown(ORIGIN_ORDER);
        click(byVisibleText(originOrderText));

        // Boton de consultar cliente
        click(CONSULT_CLIENT_BUTTON);

        // Boton aceptar mensaje de consulta
        click(ACCEPT_CONSULT_CLIENT_BUTTON);
    }

    public void closeOrder () {
        click(GO_BACK);
        click(YES_BUTTON_GO_BACK);
    }
}