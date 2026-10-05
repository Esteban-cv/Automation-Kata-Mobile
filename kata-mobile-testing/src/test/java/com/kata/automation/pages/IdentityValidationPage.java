package com.kata.automation.pages;

import org.openqa.selenium.By;
import io.appium.java_client.android.AndroidDriver;

public class IdentityValidationPage extends BasePage {

    // ========================================================================
    // LOCALIZADORES: VALIDACIÓN DE IDENTIDAD Y FIRMA DE DOCUMENTOS
    // ========================================================================

    // Botón/Pestaña principal superior (por si necesitas asegurarte de entrar ahí)
    private static final By IDENTITY_VALIDATION_TAB = By.xpath(
            "//android.widget.TextView[contains(@text, 'Validación de identidad y firma')]");

    // Botón: Iniciar validación del cliente
    private static final By START_VALIDATION_BTN = By.xpath(
            "//android.widget.TextView[@text=\"Iniciar validación del cliente\"]/following::android.view.View");

    // Botón: Consultar resultados de validación y firmas
    private static final By CONSULT_RESULTS_BTN = By.xpath(
            "//android.widget.TextView[@text=\"Consultar resultados de validación y firmas\"]/following::android.view.View");

    // Desplegable: Decisión
    private static final By DECISION_DROPDOWN = By.xpath(
            "//android.widget.TextView[@text=\"Decisión\"]/following::android.view.View[@content-desc=\"Menú desplegable\"]");

    // Botón: Procesar decisión
    private static final By PROCESS_DECISION_BTN = By.xpath(
            "//android.widget.TextView[@text=\"Procesar decisión\"]/following::android.view.View");

    // Botón de pop-up (reutilizado de tu otra clase)
    private static final By ACCEPT_BUTTON = By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");

    public IdentityValidationPage(AndroidDriver driver) {
        super(driver);
    }

        public boolean isAvailable() {
                return !driver.findElements(IDENTITY_VALIDATION_TAB).isEmpty();
        }

    /**
     * Ejecuta el flujo de la pestaña "Validación de identidad y firma de
     * documentos".
     * Realiza los clics en los botones de validación, selecciona la decisión y
     * procesa.
     *
     * @param decision La decisión a tomar (ej: "Aprobar (En campo)")
     * @param numberId Número de identificación para la evidencia
     */
    public void processIdentityValidation(String decision, String numberId) {

        // 1. Asegurar que estamos en la pestaña correcta (opcional si el flujo ya te
        // deja ahí)
        waitForVisibility(IDENTITY_VALIDATION_TAB);
        click(IDENTITY_VALIDATION_TAB);

        takeEvidence(numberId, "Validacion_Identidad");

        // 2. Clic en "Iniciar validación del cliente"
        scrollAndClickReliable("Iniciar validación del cliente", START_VALIDATION_BTN, 0.15);

        waitForVisibility(ACCEPT_BUTTON);
        click(ACCEPT_BUTTON);

        // 3. Clic en "Consultar resultados de validación y firmas"
        scrollAndClickReliable("Consultar resultados de validación y firmas", CONSULT_RESULTS_BTN, 0.15);
        waitForVisibility(ACCEPT_BUTTON);
        click(ACCEPT_BUTTON);

        scrollToText("Procesar decisión", 0.15);

        // 4. Seleccionar la decisión (ej: "Aprobar (En campo)")
        scrollAndClickReliable("Procesar decisión", DECISION_DROPDOWN, 0.15);
        clickSafely(byVisibleText(decision));
    
        // 5. Clic en "Procesar decisión"
        scrollAndClickReliable("Procesar decisión", PROCESS_DECISION_BTN, 0.15);

        waitForVisibility(ACCEPT_BUTTON);
        takeEvidence(numberId, "Identidad_Procesada");
        clickSafely(ACCEPT_BUTTON); 
    }
}