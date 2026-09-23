package com.kata.automation.pages;

import org.openqa.selenium.By;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class OrdersPage extends BasePage {

    // --- LOCALIZADORES DE ELEMENTOS ---
    private static final By CREATE_ORDER_BUTTON = By.xpath("//android.widget.Button");
    private static final By NEW_ORDER_BUTTON = By.xpath(
            "//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View[4]/android.widget.Button");
    private static final By OPEN_ID_TYPE_DROPDOWN = By.xpath(
            "//android.widget.TextView[@text=\"Tipo de identificación\"]/following-sibling::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By NUMBER_ID_INPUT = By.xpath(
            "//android.widget.TextView[@text=\"Número de identificación del solicitante\"]/following-sibling::android.widget.EditText");
    private static final By NUMBER_ID_CONFIRM_INPUT = By.xpath(
            "//android.widget.TextView[@text=\"Confirmar número de identificación del solicitante\"]/following-sibling::android.widget.EditText");
    private static final By OPEN_HOME_COUNTRY_DROPDOWN = By.xpath(
            "//android.widget.TextView[@text=\"País de origen de la identificación del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
    private static final By FIRST_NAME_INPUT = By.xpath(
            "//android.widget.TextView[@text=\"Primer apellido solicitante\"]/following-sibling::android.widget.EditText");
    private static final By NAME_INPUT = By.xpath(
            "//android.widget.TextView[@text=\"Primer nombre solicitante\"]/following-sibling::android.widget.EditText");
    private static final By SELECT_DATE = By
            .xpath("//android.widget.TextView[@text=\"Fecha de nacimiento del solicitante\"]/following-sibling::android.view.View");
    private static final By SELECT_BIRTH_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"País de nacimiento del solicitante\"]/following-sibling::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By NUMBER_PHONE_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Celular del solicitante\"]/following-sibling::android.widget.EditText");
    private static final By CONFIRM_PHONE_CLIENT = By.xpath(
            "//android.widget.TextView[@text=\"Confirmar Celular solicitante\"]/following-sibling::android.widget.EditText");
    private static final By BUSSINESS_INCORPORATION_DATE = By
            .xpath("//android.widget.TextView[@text=\"Fecha de constitución del negocio\"]/following-sibling::android.view.View");
    private static final By BUSSINESS_LONGEVITY_LOCATION = By.xpath(
            "//android.widget.TextView[@text=\"Antiguedad del negocio en la ubicación actual (meses)\"]/following-sibling::android.widget.EditText");
    private static final By ORIGIN_ORDER = By
            .xpath("//android.widget.TextView[@text=\"Origen de solicitud\"]/following-sibling::android.view.View");
    private static final By CONSULT_CLIENT_BUTTON = By
            .xpath("//android.widget.TextView[@text=\"Consultar cliente\"]/following-sibling::android.view.View[1]");
    private static final By CLIENT_ALREADY_CONSULTED_MESSAGE = By.xpath("//*[contains(@text,'Consulta exitosa')]");
    private static final By VIEW360_CONSULTED_MESSAGE = By.xpath("//*[contains(@text,'Consulta Exitosa')]");
    private static final By ACCEPT_CONSULT_CLIENT_BUTTON = By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");
    private static final By GO_BACK = AppiumBy.accessibilityId("Ir atrás");
    private static final By YES_BUTTON_GO_BACK = By.xpath(
            "//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.widget.Button");
    private static final By GENERATE_VIEW360_BUTTON = By.xpath(
            "//android.widget.TextView[@text=\"Vista 360 del cliente\"]/following::android.view.View[.//android.widget.Button][1]");
    private static final By DECISION_DROPDOWN = By
            .xpath("//android.widget.TextView[@text=\"Decisión\"]/..//android.widget.TextView[@text=\"Seleccionar\"]");
    private static final By SAVE_DECISION_BUTTON = By
            .xpath("//android.widget.TextView[@text=\"Guardar decisión\"]/following-sibling::android.view.View[1]");
    private static final By SUCCESS_UPDATING = By.xpath("//*[contains(@text,'Actualización exitosa')]");

    private static final By REJECTION_CAUSAL_DROPDOWN = By.xpath(
            "//android.widget.TextView[@text=\"Causal de rechazo\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

    // ⚠️ PENDIENTE: reemplaza 'Tiempo excedido' por el texto EXACTO que veas en el inspector
    private static final By TIMEOUT_ERROR_MESSAGE = By.xpath("//*[contains(@text,'Tiempo excedido')]");        

    public OrdersPage(AndroidDriver driver) {
        super(driver); // Envía el driver a BasePage
    }

    public void createOrder(String type, String personType) {
        click(NEW_ORDER_BUTTON);
        click(byVisibleText(type));
        click(byVisibleText(personType));
        click(CREATE_ORDER_BUTTON);
    }

    public void preliminaryConsultation(String idType, String numberId, String country, String firstName, String name,
            int birthDay, int birthMonth, int birthYear, String numberPhone, int corporationDay, int corporationMonth,
            int corporationYear, String bussinessLongevity, String originOrderText, String decision) {

        click(OPEN_ID_TYPE_DROPDOWN);
        click(byVisibleText(idType));

        type(NUMBER_ID_INPUT, numberId);
        type(NUMBER_ID_CONFIRM_INPUT, numberId);

        try {
            driver.hideKeyboard();
        } catch (Exception e) {
            /* Ignorado */ }

        takeEvidence(numberId, "01_cedula_ingresada");

        scrollAndClickReliable("País de origen de la identificación del solicitante", OPEN_HOME_COUNTRY_DROPDOWN, 0.10);
        click(byVisibleText(country));
        sleep(200);

        type(FIRST_NAME_INPUT, firstName);

        scrollToText("Primer nombre solicitante", 0.40);
        type(NAME_INPUT, name);

        scrollToText("Fecha de nacimiento del solicitante", 0.50);
        clickSafely(SELECT_DATE);
        selectDateInCalendar(birthDay, birthMonth, birthYear);

        sleep(500);

        scrollAndClickReliable("País de nacimiento del solicitante", SELECT_BIRTH_CLIENT, 0.40);
        click(byVisibleText(country));

        scrollToText("Aceptación políticas", 0.25);
        System.out.println("Antes de escribir numero de telefono");
        type(NUMBER_PHONE_CLIENT, numberPhone);
        type(CONFIRM_PHONE_CLIENT, numberPhone);
        System.out.println("Despues de escribir numero de telefono");

        sleep(1000);
        answerQuestion("¿Autoriza el tratamiento de datos personales?", "SI");
        answerQuestion("¿Autoriza la consulta y reporte a las centrales de información?", "SI");
        answerQuestion("¿Acepta Autorización de canales, horarios de contacto y visitas?", "SI");
        answerQuestion("¿La solicitud de crédito es una refinanciación o reestructuración?", "NO");

        scrollToText("Fecha de constitución del negocio", 0.30);
        clickSafely(BUSSINESS_INCORPORATION_DATE);
        selectDateInCalendar(corporationDay, corporationMonth, corporationYear);
        type(BUSSINESS_LONGEVITY_LOCATION, bussinessLongevity);

        scrollToText("Origen de solicitud", 0.40);
        sleep(200);
        clickComposeDropdown(ORIGIN_ORDER);
        click(byVisibleText(originOrderText));

        clickSafely(CONSULT_CLIENT_BUTTON);

        // IMPLEMENTAR LOGICA DE MORA, SARLAFT, CONSULTA EXITOSA O CLIENTE NUEVO

        String systemMessage = getAlertMessageText().toLowerCase();
        System.out.println("mensaje capturado " + systemMessage);

        if (systemMessage.contains("mora mayor") || systemMessage.contains("listas")) {
            System.out.println("❌ Cliente bloqueado por políticas de riesgo (Mora mayor / SARLAFT).");
            takeEvidence(numberId, "02_bloqueo_mora");
            clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);
            sleep(200);
            scrollAndClickReliable("Causal de rechazo", REJECTION_CAUSAL_DROPDOWN, 0.30);
            click(byVisibleText("Cliente dio información falsa"));
            clickSafely(SAVE_DECISION_BUTTON);
            waitForVisibility(SUCCESS_UPDATING);
            takeEvidence(numberId, "03_resultado_decisión");
            clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);
            clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);
            return;
        } else {
            System.out.println("✅ Cliente apto para continuar el flujo (Acepta mora menor o sin restricciones).");
            takeEvidence(numberId, "02_consulta_exitosa");
            clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);

            scrollToText("Decisión consulta preliminar", 0.40);

            clickSafely(GENERATE_VIEW360_BUTTON);
            waitForVisibility(VIEW360_CONSULTED_MESSAGE);
            clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);

            scrollToText("Guardar decisión", 0.40);
            sleep(200);

            clickComposeDropdown(DECISION_DROPDOWN);
            clickSafely(byVisibleText(decision));
            sleep(200);

            clickSafely(SAVE_DECISION_BUTTON);
            waitForVisibility(SUCCESS_UPDATING);

            takeEvidence(numberId, "03_resultado_decisión");
            clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);
        }
    }

    public void closeOrder() {
        sleep(800);
        click(GO_BACK);
        sleep(500);
        click(YES_BUTTON_GO_BACK);
        sleep(1000);
    }
}