package com.kata.automation.pages;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.kata.automation.utils.TestScenario;

import io.appium.java_client.android.AndroidDriver;

/**
 * Registro del producto para "Originación de inmueble" (Cartera comercial).
 *
 * <p>
 * Difiere del registro de microcrédito: la categoría del cliente y las fechas las pone
 * la app, no hay seguros voluntarios, ahorro ni fianza, y las garantías son propias
 * (garantía real tipo hipoteca con su valor, seguro todo riesgo, seguro deudores y
 * comisiones Data Crédito, Cifin y MiPyme).
 * </p>
 *
 * <p>
 * Los valores salen de las columnas del Excel por nombre; si una columna está vacía se usa
 * el valor por defecto indicado en cada paso.
 * </p>
 */
public class RealEstateProductRegistrationPage extends BasePage {

    private static final By PRODUCT_REGISTRATION_TAB = By.xpath(
            "//android.widget.TextView[@text=\"Registro del producto\"]");
    private static final By VALIDATE_GUARANTEES_BTN = By.xpath(
            "//android.widget.TextView[@text=\"Validar garantías y seguros\"]/following::android.view.View[1]");
    private static final By SIMULATE_CREDIT_BTN = By.xpath(
            "//android.widget.TextView[@text=\"Simular crédito\"]/following::android.view.View[.//android.widget.Button][1]");
    private static final By ACCEPT_BUTTON = By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");

    public RealEstateProductRegistrationPage(AndroidDriver driver) {
        super(driver);
    }

    public void fillRealEstateProduct(String numberId, TestScenario data) {
        dismissMessageIfPresent();
        click(PRODUCT_REGISTRATION_TAB);
        // Si la pestaña ya tenía datos (reintento), el formulario puede quedar abajo:
        // scrollToText solo baja, así que se vuelve al inicio.
        for (int i = 0; i < 6; i++) {
            scrollDownPercent(-0.8);
        }

        // --- Selección del producto (la categoría del cliente la pone la app) ---
        selectOption("Tipo de producto", value(data, "Cartera comercial", "Tipo de producto"));
        selectOption("Producto", value(data, "Inmueble Productivo", "Producto"));
        // Monto y plazo seguidos y sin ocultar el teclado entre ellos (igual que en
        // microcrédito): al ocultarlo, el campo de moneda quedaba vacío y la simulación
        // respondía "No se recibió el Monto de Capital".
        String amount = required(data, "Monto");
        scrollToText("Monto sugerido", 0.15);
        type(textBelow("Monto sugerido"), amount);
        type(textBelow("Plazo sugerido"), required(data, "Plazo"));
        ensureAmountTyped(amount);
        try {
            driver.hideKeyboard();
        } catch (Exception e) {
            /* sin teclado */ }
        takeEvidence(numberId, "Selección_producto_inmueble");

        selectOption("Frecuencia de pago sugerida", value(data, "30", "Frecuencia de pago"));
        selectOption("Destino económico del crédito", value(data, "Compra de Inmueble", "Destino del crédito"));

        // Fechas: desembolso ~15 días después de hoy (o el del Excel) y primera cuota un
        // mes después del desembolso. Con la fecha que propone la app la simulación responde
        // "Fecha de Primer Pago indicada es Incorrecta" o "No se cumple el período Mínimo...".
        LocalDate disbursement = disbursementDate(data);
        LocalDate firstInstallment = disbursement.plusMonths(1);
        selectDate("Fecha de desembolso", disbursement);
        selectDate("Fecha de primera cuota", firstInstallment);
        // Micropyme (y algunas versiones de inmueble) preguntan si el desembolso va a ahorros.
        answerIfPresent("¿El desembolso sería a cuenta de ahorros?", value(data, "No", "Ahorro"));

        // --- Garantías y seguros ---
        scrollAndClickReliable("Validar garantías y seguros", VALIDATE_GUARANTEES_BTN, 0.15);
        waitForVisibility(ACCEPT_BUTTON);
        takeEvidence(numberId, "Mensaje_validacion_garantias");
        click(ACCEPT_BUTTON);
        sleep(2000); // la app marca sola FNG y garantía real

        // FNG: en inmueble la app suele decidirlo sola al validar (la pregunta queda
        // deshabilitada). "Si" intenta marcarla; un tipo (p. ej. RECONSTRUYE, micropyme) además
        // lo elige en "Garantía FNG". Si la app no lo deja, se respeta lo que decidió.
        String fng = value(data, "No", "Garantía FNG");
        if (!"no".equalsIgnoreCase(fng)) {
            answerIfPresent("¿Requiere garantía FNG", "Si");
            if (!"si".equalsIgnoreCase(fng)) {
                try {
                    selectOption("Garantía FNG", fng);
                } catch (RuntimeException notShown) {
                    System.out.println("ℹ️ La app no mostró el tipo de garantía FNG para elegir '" + fng + "'.");
                }
            }
        }

        // Columna compartida con microcrédito (Si/No). En inmueble "Si" equivale a la única
        // garantía real que ofrece la app: hipoteca.
        String guaranteeType = value(data, "hipoteca", "Garantía real");
        if ("si".equalsIgnoreCase(guaranteeType)) {
            guaranteeType = "hipoteca";
        }
        if (!"no".equalsIgnoreCase(guaranteeType)) {
            selectOption("Garantía garantía real", guaranteeType);
            typeField("Valor de la garantía", required(data, "Valor de la garantía"));
        }

        answerIfPresent("¿Requiere Seguro todo riesgo?", value(data, "No", "Seguro todo riesgo"));
        // Con codeudor (columna "Codeudor") el seguro deudores va en Si: así la app muestra
        // el botón "Codeudor(es)".
        answerIfPresent("¿Requiere seguro Deudores:",
                CodebtorPage.requested(data) ? "Si" : value(data, "No", "Seguro deudores"));
        answerIfPresent("¿Requiere comision Data Crédito?", value(data, "No", "Comisión Data Crédito"));
        answerIfPresent("¿Requiere comisión Cifin:", value(data, "No", "Comisión Cifin"));
        answerIfPresent("¿Requiere comisión MiPyme:", value(data, "No", "Comisión MiPyme"));
        takeEvidence(numberId, "Garantias_inmueble");

        // --- Simulación ---
        scrollAndClickReliable("Simular crédito", SIMULATE_CREDIT_BTN, 0.15);
        waitForSimulationResponse();
        takeEvidence(numberId, "simulacion_credito_respuesta");
        click(ACCEPT_BUTTON);

        // --- Codeudor (hoja "Datos codeudor"), si la solicitud lo trae ---
        new CodebtorPage(driver).registerCodebtor(numberId, data);
    }

    private void selectDate(String label, LocalDate date) {
        scrollAndClickReliable(label, textBelow(label), 0.15);
        selectDateInCalendar(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    private static LocalDate disbursementDate(TestScenario data) {
        String day = value(data, "", "Día de desembolso");
        String month = value(data, "", "Mes de desembolso");
        String year = value(data, "", "Año de desembolso");
        if (day.isBlank() || month.isBlank() || year.isBlank()) {
            return LocalDate.now().plusDays(15);
        }
        return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
    }

    private void waitForSimulationResponse() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(60)).until(currentDriver ->
                    !currentDriver.findElements(ACCEPT_BUTTON).isEmpty());
        } catch (TimeoutException exception) {
            throw new RuntimeException("La simulación no respondió en 60 segundos.", exception);
        }
        String source = driver.getPageSource().toLowerCase();
        if (source.contains("ocurrió un error") || source.contains("ocurrio un error")
                || source.contains("error durante") || source.contains("generó el error")) {
            throw new RuntimeException("La simulación devolvió un error. Revise la evidencia del caso.");
        }
    }

    /** Responde la pregunta solo si existe en el formulario (algunas dependen del producto). */
    private void answerIfPresent(String question, String answer) {
        try {
            scrollToText(question, 0.15);
        } catch (RuntimeException notFound) {
            System.out.println("ℹ️ El formulario no muestra la pregunta '" + question + "'.");
            return;
        }
        // Se usa el texto real de la etiqueta: varía entre productos (p. ej. "¿Requiere
        // garantía FNG?" en inmueble y "¿Requiere garantía FNG" sin "?" en micropyme).
        List<WebElement> label = findNow(By.xpath("//android.widget.TextView[contains(@text, "
                + xpathLiteral(question) + ")]"));
        answerQuestion(label.isEmpty() ? question : label.get(0).getText(), answer);
    }

    private void selectOption(String label, String option) {
        scrollAndClickReliable(label, dropdownBelow(label), 0.15);
        sleep(500);
        selectDropdownOption(option);
    }

    /** Verifica que el monto quedó escrito; si el campo de moneda lo borró, lo escribe tecla por tecla. */
    private void ensureAmountTyped(String amount) {
        WebElement field = driver.findElement(textBelow("Monto sugerido"));
        String digits = field.getText().replaceAll("[^0-9]", "");
        if (digits.equals(amount.replaceAll("[^0-9]", ""))) {
            return;
        }
        System.out.println("⚠️ El monto quedó como '" + field.getText() + "'; se escribe de nuevo tecla por tecla.");
        field.click();
        field.clear();
        for (char digit : amount.replaceAll("[^0-9]", "").toCharArray()) {
            field.sendKeys(String.valueOf(digit));
        }
        digits = driver.findElement(textBelow("Monto sugerido")).getText().replaceAll("[^0-9]", "");
        if (!digits.equals(amount.replaceAll("[^0-9]", ""))) {
            throw new IllegalStateException("No se pudo escribir el monto " + amount + " en 'Monto sugerido'.");
        }
    }

    private void typeField(String label, String text) {
        scrollToText(label, 0.15);
        type(textBelow(label), text);
        try {
            driver.hideKeyboard();
        } catch (Exception e) {
            /* sin teclado */ }
    }

    private static By dropdownBelow(String label) {
        return By.xpath("//android.widget.TextView[@text=" + xpathLiteral(label) + "]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
    }

    private static By textBelow(String label) {
        return By.xpath("//android.widget.TextView[@text=" + xpathLiteral(label) + "]/following::android.widget.EditText[1]");
    }

    private static String value(TestScenario data, String defaultValue, String... columns) {
        String value = data.get(columns);
        return value.isBlank() ? defaultValue : value.replaceAll("\\.0$", "");
    }

    private static String required(TestScenario data, String column) {
        String value = value(data, "", column);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Falta la columna '" + column + "' en el caso " + data.getCaseId());
        }
        return value;
    }
}
