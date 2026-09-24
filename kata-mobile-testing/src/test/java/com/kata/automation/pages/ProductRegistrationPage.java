package com.kata.automation.pages;

import java.time.LocalDate;

import org.openqa.selenium.By;
import io.appium.java_client.android.AndroidDriver;

public class ProductRegistrationPage extends BasePage {

    // ========================================================================
    // LOCALIZADORES: REGISTRO DEL PRODUCTO
    // ========================================================================

    // --- Selección del producto ---
    private static final By PRODUCT_TYPE = By.xpath(
            "//android.widget.TextView[@text=\"Tipo de producto\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
    private static final By PRODUCT = By.xpath(
            "//android.widget.TextView[@text=\"Producto\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

    private static final By SUGGESTED_AMOUNT = By.xpath(
            "//android.widget.TextView[@text=\"Monto sugerido\"]/following::android.widget.EditText[1]");
    private static final By SUGGESTED_TERM = By.xpath(
            "//android.widget.TextView[@text=\"Plazo sugerido\"]/following::android.widget.EditText[1]");

    private static final By PAYMENT_FREQUENCY = By.xpath(
            "//android.widget.TextView[@text=\"Frecuencia de pago sugerida\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
    private static final By ECONOMIC_DESTINATION = By.xpath(
            "//android.widget.TextView[@text=\"Destino económico del crédito\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

    // --- Fechas ---
    // Nota: En Compose, al lado del texto suele estar el contenedor del EditText o
    // la Vista cliqueable.
    private static final By DISBURSEMENT_DATE = By.xpath(
            "//android.widget.TextView[@text=\"Fecha de desembolso\"]/following::android.view.View[1]");
    private static final By FIRST_INSTALLMENT_DATE = By.xpath(
            "//android.widget.TextView[@text=\"Fecha de primera cuota\"]/following::android.view.View[1]");

    // --- Botones de acción final ---
    private static final By VALIDATE_GUARANTEES_BTN = By.xpath(
            "//android.widget.TextView[@text=\"Validar garantías y seguros\"]/following::android.view.View[1]");

    // Asumiendo la estructura estándar de botones de la app para el último paso
    private static final By SIMULATE_CREDIT_BTN = By.xpath(
            "//android.widget.TextView[@text=\"Simular crédito\"]/following::android.view.View[.//android.widget.Button][1]");

    private static final By ACCEPT_BUTTON = By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");

    // --- Checkboxes de Seguros (Para usar después) ---
    // Ojo al error de la app: "Microempesarios" (sin R)
    // private static final By INS_FAMILY =
    // By.xpath("//android.widget.TextView[@text=\"Tu familia
    // protegida\"]/following-sibling::android.widget.CheckBox");
    // private static final By INS_MICRO =
    // By.xpath("//android.widget.TextView[@text=\"Microempesarios
    // protegidos\"]/following-sibling::android.widget.CheckBox");

    public ProductRegistrationPage(AndroidDriver driver) {
        super(driver);
    }

    /**
     * Llena el flujo principal de Registro del Producto usando parámetros externos
     * (Ej: Desde Excel).
     */
    public void fillProductRegistration(String numberId, String productType, String product, String amount, String term,
            String paymentFreq, String destination,
            int disbDay, int disbMonth, int disbYear,
            String hasSavings) {

        System.out.println("--- Iniciando Registro del Producto ---");

        LocalDate calculatedDate = LocalDate.of(disbYear, disbMonth, disbDay).plusDays(30);

        // 1. Selección del producto y montos
        clickComposeDropdown(PRODUCT_TYPE);
        clickSafely(byVisibleText(productType));

        clickComposeDropdown(PRODUCT);
        clickSafely(byVisibleText(product));

        type(SUGGESTED_AMOUNT, amount);

        // No necesita scroll porque está pegado al monto
        type(SUGGESTED_TERM, term);

        takeEvidence(numberId, "07_Selección_producto");

        scrollAndClickReliable("Frecuencia de pago sugerida", PAYMENT_FREQUENCY, 0.15);
        clickSafely(byVisibleText(paymentFreq));

        scrollAndClickReliable("Destino económico del crédito", ECONOMIC_DESTINATION, 0.15);
        clickSafely(byVisibleText(destination));

        // AQUI VA EL SALTO: Los seguros voluntarios se ignoran en este método
        // principal.

        // 2. Ahorros y Fechas
        scrollToText("¿Tiene ahorro?", 0.15);
        answerQuestion("¿Tiene ahorro?", hasSavings);

        scrollAndClickReliable("Fecha de desembolso", DISBURSEMENT_DATE, 0.15);
        selectDateInCalendar(disbDay, disbMonth, disbYear);

        scrollAndClickReliable("Fecha de primera cuota", FIRST_INSTALLMENT_DATE, 0.15);
        selectDateInCalendar(calculatedDate.getDayOfMonth(),calculatedDate.getMonthValue(),calculatedDate.getYear());

        // 3. Validación de Garantías
        scrollAndClickReliable("Validar garantías y seguros", VALIDATE_GUARANTEES_BTN, 0.15);

        waitForVisibility(ACCEPT_BUTTON);
        takeEvidence(numberId, "08_Mensaje_validacion_seguros");
        click(ACCEPT_BUTTON);

        System.out.println("Esperando que el sistema auto-complete las garantías...");
        sleep(3000); // Pausa vital para que el sistema procese y marque los radio buttons solos.

        // 4. Llenar obligatoriamente FNG y Garantía Real (Como lo pediste)
        // OJO: Texto exacto de la app, sin signo de interrogación al final
        answerQuestion("¿Requiere garantía FNG", "No");

        answerQuestion("¿Tiene garantía real?", "No");

        // 5. Simular
        scrollAndClickReliable("Simular crédito", SIMULATE_CREDIT_BTN, 0.15);
        waitForVisibility(ACCEPT_BUTTON);
        takeEvidence(numberId, "09_Simulación_crédito");

        click(ACCEPT_BUTTON);

        System.out.println("--- Registro del Producto Finalizado. Simulando... ---");
    }

    /**
     * Método aislado para configurar los seguros voluntarios.
     * Se deja vacío por ahora para no afectar el flujo principal, tal como se
     * solicitó.
     * Cuando vayas a automatizar los modales de familiares protegidos, lo
     * construimos aquí.
     */
    public void configureVoluntaryInsurances() {
        System.out.println("Configurando seguros voluntarios...");
        // TODO: Implementar la lógica de checkboxes y modales que se abren
        // dinámicamente
    }
}