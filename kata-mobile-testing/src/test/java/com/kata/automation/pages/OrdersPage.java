package com.kata.automation.pages;

import java.time.Duration;

import org.openqa.selenium.By;

import com.kata.automation.utils.ExecutionTelemetry;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class OrdersPage extends BasePage {

        /**
         * Resultado de "Consultar cliente". Los dos bloqueos detienen la solicitud en la
         * consulta preliminar; los demás dejan continuar a la radicación.
         */
        public enum ConsultationOutcome {
                APPROVED,
                NEW_CLIENT,
                RENEWED_CLIENT,
                MINOR_ARREARS,
                BLOCKED_BY_LISTS,
                BLOCKED_BY_ARREARS;

                public boolean isBlocked() {
                        return this == BLOCKED_BY_LISTS || this == BLOCKED_BY_ARREARS;
                }
        }

        private ConsultationOutcome lastConsultationOutcome = ConsultationOutcome.APPROVED;

        // --- LOCALIZADORES DE ELEMENTOS ---

        // Botón del paso de confirmación (texto CREAR). Antes era el primer Button de la
        // pantalla, que podía coincidir con otro botón.
        private static final By CREATE_ORDER_BUTTON = By.xpath(
                        "//android.widget.TextView[@text=\"CREAR\"]/following-sibling::android.widget.Button");

        // Botón flotante de la lista de órdenes, por su descripción de accesibilidad.
        private static final By NEW_ORDER_BUTTON = AppiumBy.accessibilityId("Nueva orden");

        // Paso 3 de "Crear orden": aparece directo cuando el proceso tiene un solo
        // formulario (ej. Originación de inmueble -> SolicitudInmueblePersonaNatural).
        private static final By ORDER_CONFIRMATION_TITLE = By.xpath(
                        "//android.widget.TextView[@text=\"Confirmación\"]");

        // --- Campos opcionales y nuevos de la consulta preliminar (versión 2026.06.22) ---
        private static final By SECOND_SURNAME_INPUT = By.xpath(
                        "//android.widget.TextView[@text=\"Segundo apellido solicitante\"]/following::android.widget.EditText[1]");
        // La app lo rotula "cliente", no "solicitante".
        private static final By SECOND_NAME_INPUT = By.xpath(
                        "//android.widget.TextView[@text=\"Segundo nombre cliente\"]/following::android.widget.EditText[1]");
        private static final String ELECTRONIC_SIGNATURE_LABEL = "Iniciar Firma electrónica";
        private static final By ELECTRONIC_SIGNATURE_BUTTON = By.xpath(
                        "//android.widget.TextView[@text=\"" + ELECTRONIC_SIGNATURE_LABEL
                                        + "\"]/following::android.widget.Button[1]");

        // ✅ Corregidos a following + [1]
        private static final By OPEN_ID_TYPE_DROPDOWN = By.xpath(
                        "//android.widget.TextView[@text=\"Tipo de identificación\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By NUMBER_ID_INPUT = By.xpath(
                        "//android.widget.TextView[@text=\"Número de identificación del solicitante\"]/following::android.widget.EditText[1]");
        private static final By NUMBER_ID_CONFIRM_INPUT = By.xpath(
                        "//android.widget.TextView[@text=\"Confirmar número de identificación del solicitante\"]/following::android.widget.EditText[1]");

        // Este ya estaba perfecto
        private static final By OPEN_HOME_COUNTRY_DROPDOWN = By.xpath(
                        "//android.widget.TextView[@text=\"País de origen de la identificación del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // ✅ Corregidos a following + [1]
        private static final By FIRST_NAME_INPUT = By.xpath(
                        "//android.widget.TextView[@text=\"Primer apellido solicitante\"]/following::android.widget.EditText[1]");
        private static final By NAME_INPUT = By.xpath(
                        "//android.widget.TextView[@text=\"Primer nombre solicitante\"]/following::android.widget.EditText[1]");
        private static final By SELECT_DATE = By.xpath(
                        "//android.widget.TextView[@text=\"Fecha de nacimiento del solicitante\"]/following::android.view.View[1]");

        // Este ya estaba perfecto
        private static final By SELECT_BIRTH_CLIENT = By.xpath(
                        "//android.widget.TextView[@text=\"País de nacimiento del solicitante\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // ✅ Corregidos a following + [1]
        private static final By NUMBER_PHONE_CLIENT = By.xpath(
                        "//android.widget.TextView[@text=\"Celular del solicitante\"]/following::android.widget.EditText[1]");
        private static final By CONFIRM_PHONE_CLIENT = By.xpath(
                        "//android.widget.TextView[@text=\"Confirmar Celular solicitante\"]/following::android.widget.EditText[1]");
        private static final By BUSSINESS_INCORPORATION_DATE = By.xpath(
                        "//android.widget.TextView[@text=\"Fecha de constitución del negocio\"]/following::android.view.View[1]");
        private static final By BUSSINESS_LONGEVITY_LOCATION = By.xpath(
                        "//android.widget.TextView[@text=\"Antiguedad del negocio en la ubicación actual (meses)\"]/following::android.widget.EditText[1]");
        private static final By ORIGIN_ORDER = By.xpath(
                        "//android.widget.TextView[@text=\"Origen de solicitud\"]/following::android.view.View[1]");
        private static final By CONSULT_CLIENT_BUTTON = By.xpath(
                        "//android.widget.TextView[@text=\"Consultar cliente\"]/following::android.view.View[1]");

        // Lógica condicional, se mantienen igual
        private static final By CONSULTATION_RESULT = By.xpath(
                        "//*[contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜ', 'abcdefghijklmnopqrstuvwxyzáéíóúü'), 'consulta exitosa') "
                                        + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜ', 'abcdefghijklmnopqrstuvwxyzáéíóúü'), 'mora mayor') "
                                        + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜ', 'abcdefghijklmnopqrstuvwxyzáéíóúü'), 'registrado en listas') "
                                        + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜ', 'abcdefghijklmnopqrstuvwxyzáéíóúü'), 'cliente nuevo') "
                                        + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜ', 'abcdefghijklmnopqrstuvwxyzáéíóúü'), 'renovado') "
                                        + "or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜ', 'abcdefghijklmnopqrstuvwxyzáéíóúü'), 'mora menor')]");
        private static final By VIEW360_CONSULTED_MESSAGE = By.xpath("//*[contains(@text,'Consulta Exitosa')]");
        private static final By ACCEPT_CONSULT_CLIENT_BUTTON = By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");
        private static final By GO_BACK = AppiumBy.accessibilityId("Ir atrás");

        // ⚠️ Nota: XPath absoluto
        private static final By YES_BUTTON_GO_BACK = By.xpath(
                        "//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.widget.Button");

        // Estos ya estaban blindados y perfectos
        private static final By GENERATE_VIEW360_BUTTON = By.xpath(
                        "//android.widget.TextView[@text=\"Vista 360 del cliente\"]/following::android.view.View[.//android.widget.Button][1]");
        private static final By DECISION_DROPDOWN = By.xpath(
                        "//android.widget.TextView[@text=\"Decisión\"]/..//android.widget.TextView[@text=\"Seleccionar\"]");

        // ✅ Corregido a following + [1]
        private static final By SAVE_DECISION_BUTTON = By.xpath(
                        "//android.widget.TextView[@text=\"Guardar decisión\"]/following::android.view.View[1]");

        // Ya estaban perfectos
        private static final By SUCCESS_UPDATING = By.xpath("//*[contains(@text,'Actualización exitosa')]");
        private static final By REJECTION_CAUSAL_DROPDOWN = By.xpath(
                        "//android.widget.TextView[@text=\"Causal de rechazo\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");
        private static final By TIMEOUT_ERROR_MESSAGE = By.xpath(
                        "//*[contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'tiempo excedido') or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'tiempo de espera') or contains(translate(@text, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'timeout')]");

        public OrdersPage(AndroidDriver driver) {
                super(driver); // Envía el driver a BasePage
        }

        public ConsultationOutcome getLastConsultationOutcome() {
                return lastConsultationOutcome;
        }

        /**
         * Crea la orden. Cuando el proceso tiene un único formulario (Originación de
         * inmueble) la app salta directo a la confirmación y no se elige tipo de persona.
         */
        public void createOrder(String type, String personType) {
                click(NEW_ORDER_BUTTON);
                click(byVisibleText(type));
                if (!isPresentWithin(ORDER_CONFIRMATION_TITLE, Duration.ofSeconds(3))) {
                        click(byVisibleText(personType));
                }
                waitForVisibility(ORDER_CONFIRMATION_TITLE);
                click(CREATE_ORDER_BUTTON);
        }

        /** Consulta preliminar sin segundo nombre ni segundo apellido (compatibilidad). */
        public boolean preliminaryConsultation(String idType, String numberId, String country, String firstName,
                        String name,
                        int birthDay, int birthMonth, int birthYear, String numberPhone, String restructuring,
                        int corporationDay, int corporationMonth,
                        int corporationYear, String bussinessLongevity, String originOrderText) {
                return preliminaryConsultation(idType, numberId, country, firstName, "", name, "",
                                birthDay, birthMonth, birthYear, numberPhone, restructuring,
                                corporationDay, corporationMonth, corporationYear, bussinessLongevity, originOrderText);
        }

        /**
         * Consulta preliminar. El segundo apellido y el segundo nombre son opcionales:
         * si vienen vacíos no se tocan.
         */
        public boolean preliminaryConsultation(String idType, String numberId, String country, String firstName,
                        String secondSurname, String name, String secondName,
                        int birthDay, int birthMonth, int birthYear, String numberPhone, String restructuring,
                        int corporationDay, int corporationMonth,
                        int corporationYear, String bussinessLongevity, String originOrderText) {

                lastConsultationOutcome = ConsultationOutcome.APPROVED;

                click(OPEN_ID_TYPE_DROPDOWN);
                click(byVisibleText(idType));

                type(NUMBER_ID_INPUT, numberId);
                type(NUMBER_ID_CONFIRM_INPUT, numberId);

                try {
                        driver.hideKeyboard();
                } catch (Exception e) {
                        /* Ignorado */ }

                takeEvidence(numberId, "cedula_ingresada");

                scrollAndClickReliable("País de origen de la identificación del solicitante",
                                OPEN_HOME_COUNTRY_DROPDOWN, 0.10);
                click(byVisibleText(country));
                sleep(200);

                type(FIRST_NAME_INPUT, firstName);
                typeIfPresent("Segundo apellido solicitante", SECOND_SURNAME_INPUT, secondSurname);

                scrollToText("Primer nombre solicitante", 0.40);
                type(NAME_INPUT, name);
                typeIfPresent("Segundo nombre cliente", SECOND_NAME_INPUT, secondName);

                scrollToText("Fecha de nacimiento del solicitante", 0.50);
                clickSafely(SELECT_DATE);
                selectDateInCalendar(birthDay, birthMonth, birthYear);

                sleep(500);

                scrollAndClickReliable("País de nacimiento del solicitante", SELECT_BIRTH_CLIENT, 0.20);
                click(byVisibleText(country));

                scrollToText("Aceptación políticas", 0.15);
                type(NUMBER_PHONE_CLIENT, numberPhone);
                type(CONFIRM_PHONE_CLIENT, numberPhone);

                sleep(1000);
                answerQuestion("¿Autoriza el tratamiento de datos personales?", "Si");
                answerQuestion("¿Autoriza la consulta y reporte a las centrales de información?", "Si");
                answerQuestion("¿Acepta Autorización de canales, horarios de contacto y visitas?", "Si");
                startElectronicSignatureIfPresent();
                answerQuestion("¿La solicitud de crédito es una refinanciación o reestructuración?", restructuring);

                scrollToText("Fecha de constitución del negocio", 0.15);
                clickSafely(BUSSINESS_INCORPORATION_DATE);
                selectDateInCalendar(corporationDay, corporationMonth, corporationYear);
                type(BUSSINESS_LONGEVITY_LOCATION, bussinessLongevity);

                scrollToText("Origen de solicitud", 0.15);
                sleep(200);
                clickComposeDropdown(ORIGIN_ORDER);
                click(byVisibleText(originOrderText));

                scrollToText("Consultar cliente", 0.10);
                long consultationStarted = System.nanoTime();
                try {
                        clickWithRetry(CONSULT_CLIENT_BUTTON, CONSULTATION_RESULT, TIMEOUT_ERROR_MESSAGE);
                        ExecutionTelemetry.actionFinished("Consultar cliente", true,
                                        (System.nanoTime() - consultationStarted) / 1_000_000);
                } catch (RuntimeException exception) {
                        ExecutionTelemetry.actionFinished("Consultar cliente", false,
                                        (System.nanoTime() - consultationStarted) / 1_000_000);
                        throw exception;
                }

                // IMPLEMENTAR LOGICA DE MORA, SARLAFT, CONSULTA EXITOSA O CLIENTE NUEVO

                String systemMessage = getAlertMessageText().toLowerCase();
                lastConsultationOutcome = classifyConsultation(systemMessage);
                System.out.println("ℹ️ Resultado de la consulta: " + lastConsultationOutcome + " (" + systemMessage + ")");

                if (lastConsultationOutcome.isBlocked()) {
                        System.out.println("❌ Cliente bloqueado por políticas de riesgo (Mora mayor / SARLAFT).");
                        takeEvidence(numberId, "bloqueo_mora");
                        clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);
                        sleep(200);
                        scrollAndClickReliable("Causal de rechazo", REJECTION_CAUSAL_DROPDOWN, 0.30);
                        click(byVisibleText("Cliente dio información falsa"));
                        clickSafely(SAVE_DECISION_BUTTON);
                        waitForVisibility(SUCCESS_UPDATING);
                        takeEvidence(numberId, "resultado_decisión");
                        clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);
                        clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);
                        return false;
                } else {
                        System.out.println(
                                        "✅ Cliente apto para continuar el flujo (Acepta mora menor o sin restricciones).");
                        takeEvidence(numberId, "consulta_exitosa");
                        clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);

                        scrollToText("Decisión consulta preliminar", 0.40);

                        clickSafely(GENERATE_VIEW360_BUTTON);
                        waitForVisibility(VIEW360_CONSULTED_MESSAGE);
                        clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);

                        scrollToText("Guardar decisión", 0.40);
                        sleep(200);

                        clickComposeDropdown(DECISION_DROPDOWN);
                        clickSafely(byVisibleText("Continuar"));
                        sleep(200);

                        clickSafely(SAVE_DECISION_BUTTON);
                        waitForVisibility(SUCCESS_UPDATING);

                        takeEvidence(numberId, "resultado_decisión");
                        clickSafely(ACCEPT_CONSULT_CLIENT_BUTTON);
                        return true;
                }
        }

        /** Traduce el mensaje de "Consultar cliente" al resultado del flujo. */
        static ConsultationOutcome classifyConsultation(String message) {
                String text = message == null ? "" : message.toLowerCase();
                if (text.contains("listas")) {
                        return ConsultationOutcome.BLOCKED_BY_LISTS;
                }
                if (text.contains("mora mayor")) {
                        return ConsultationOutcome.BLOCKED_BY_ARREARS;
                }
                if (text.contains("mora menor")) {
                        return ConsultationOutcome.MINOR_ARREARS;
                }
                if (text.contains("nuevo")) {
                        return ConsultationOutcome.NEW_CLIENT;
                }
                if (text.contains("renovado")) {
                        return ConsultationOutcome.RENEWED_CLIENT;
                }
                return ConsultationOutcome.APPROVED;
        }

        /** El botón de firma electrónica solo existe en la versión nueva; basta con tocarlo. */
        private void startElectronicSignatureIfPresent() {
                if (!isPresentWithin(byVisibleText(ELECTRONIC_SIGNATURE_LABEL), Duration.ofSeconds(2))) {
                        System.out.println("ℹ️ El formulario no muestra '" + ELECTRONIC_SIGNATURE_LABEL + "'.");
                        return;
                }
                scrollAndClickReliable(ELECTRONIC_SIGNATURE_LABEL, ELECTRONIC_SIGNATURE_BUTTON, 0.15);
                sleep(500);
        }

        /** Escribe solo si hay valor; el campo es opcional en el formulario. */
        private void typeIfPresent(String label, By input, String value) {
                if (value == null || value.isBlank()) {
                        return;
                }
                scrollToText(label, 0.20);
                type(input, value);
        }

        public void closeOrder() {
                click(GO_BACK);
                waitForVisibility(YES_BUTTON_GO_BACK);
                click(YES_BUTTON_GO_BACK);
                sleep(1000);
        }
}