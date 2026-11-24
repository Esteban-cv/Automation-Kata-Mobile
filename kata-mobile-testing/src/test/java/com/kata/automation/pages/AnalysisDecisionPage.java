package com.kata.automation.pages;

import org.openqa.selenium.By;

import io.appium.java_client.android.AndroidDriver;

public class AnalysisDecisionPage extends BasePage {

        // ========================================================================
        // LOCALIZADORES: DECISIÓN DEL ANÁLISIS
        // ========================================================================

        private static final By DECISION_BUTTON = By.xpath(
                        "//android.widget.TextView[@text=\"Decisión del análisis\"]");

        // Botones de resumen (Comentados por si los necesitas a futuro)
        private static final By COMMITTEE_SUMMARY_BTN = By
                        .xpath("//android.widget.TextView[@text=\"Resumen de comité\"]/following::android.view.View[1]");
        // private static final By COMMITTEE_TABLE_BTN =
        // By.xpath("//android.widget.TextView[@text=\"Tabla resumen del
        // comité\"]/following::android.view.View[1]");

        private static final By ACCEPT_BUTTON = By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]");

        private static final By INTERVIEW_PLACE = By.xpath(
                        "//android.widget.TextView[@text=\"Lugar de entrevista\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        private static final By INTERVIEW_RESULT = By.xpath(
                        "//android.widget.TextView[@text=\"Resultado de entrevista\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        private static final By CONTRACTUAL_PURPOSE = By.xpath(
                        "//android.widget.TextView[@text=\"Propósito de la relación contractual\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        // Ojo: "Donde" está sin tilde, igualito a la app
        private static final By EXTRACTS_INFO_WHERE = By.xpath(
                        // Por contains: en microcrédito la etiqueta empieza con "¿" y en inmueble no.
                        "//android.widget.TextView[contains(@text, \"Donde recibe información de extractos\")]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        private static final By ANALYST_CONCEPT = By.xpath(
                        "//android.widget.TextView[@text=\"Concepto de aprobación del analista\"]/following::android.widget.EditText[1]");

        private static final By ANALYSIS_DECISION = By.xpath(
                        "//android.widget.TextView[@text=\"Decisión del análisis\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        private static final By PROCESS_DECISION_BTN = By.xpath(
                        "//android.widget.TextView[@text=\"Procesar decisión\"]/following::android.view.View[1]");

        private static final By DECISION_PROCESSED_MESSAGE = By.xpath(
                        "//*[contains(@text, \"Validación exitosa y procesamiento exitoso.\") or contains(@text, \"Decisión procesada\")]");

        private static final By DECISION_PROCESSED_OR_SAVED = By.xpath(
            "//*[contains(@text, \"Validación exitosa y procesamiento exitoso.\")"
                    + " or contains(@text, \"Decisión procesada\")"
                    + " or contains(@text, \"Orden guardada exitosamente\")]");

    // Diálogo de rechazo del motor de crédito al procesar la decisión.
        private static final By ENGINE_ERROR_DIALOG = By.xpath(
                        "//android.widget.TextView[starts-with(@text, \"Error de actualización en Engine\")"
                    + " or contains(@text, \"no cumple validaciones\")]");

        private static final By TIMEOUT_ERROR_MESSAGE = By.xpath(
                        "//*[contains(translate(@text, \"ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜ\", "
                                        + "\"abcdefghijklmnopqrstuvwxyzáéíóúü\"), \"tiempo de espera\") "
                                        + "or contains(translate(@text, \"ABCDEFGHIJKLMNOPQRSTUVWXYZÁÉÍÓÚÜ\", "
                                        + "\"abcdefghijklmnopqrstuvwxyzáéíóúü\"), \"timeout\")]");

        public AnalysisDecisionPage(AndroidDriver driver) {
                super(driver);
        }

        /**
         * Llena la pestaña final de Decisión del Análisis y procesa la solicitud.
         */
        public void fillAnalysisDecision(String interviewPlace, String interviewResult,
                        String contractualPurpose, String extractsInfo,
                        String analystConcept,
                        String decision, String numberId) {

                waitForVisibility(DECISION_BUTTON);
                click(DECISION_BUTTON);
                // En un reintento la pestaña puede quedar desplazada; scrollToText solo baja.
                for (int i = 0; i < 6; i++) {
                        scrollDownPercent(-0.8);
                }

                click(COMMITTEE_SUMMARY_BTN);
                waitForVisibility(ACCEPT_BUTTON);
                click(ACCEPT_BUTTON);

                scrollAndClickReliable("Lugar de entrevista", INTERVIEW_PLACE, 0.15);
                selectDropdownOption(interviewPlace);

                scrollAndClickReliable("Resultado de entrevista", INTERVIEW_RESULT, 0.15);
                selectDropdownOption(interviewResult);

                scrollAndClickReliable("Propósito de la relación contractual", CONTRACTUAL_PURPOSE, 0.15);
                selectDropdownOption(contractualPurpose);

                scrollAndClickReliable("Donde recibe información de extractos", EXTRACTS_INFO_WHERE, 0.20);
                selectDropdownOption(extractsInfo);

                scrollToText("Concepto de aprobación del analista", 0.15);
                type(ANALYST_CONCEPT, analystConcept);

                takeEvidence(numberId, "Resumen_comite");

                // scrollToText("Observaciones del gerente de oficina", 0.15);
                // type(MANAGER_OBSERVATIONS, managerObservations);

                scrollAndClickReliable("Decisión del análisis", ANALYSIS_DECISION, 0.15);
                selectDropdownOption(decision);

                scrollToText("Procesar decisión", 0.15);
                // La respuesta puede ser éxito o un rechazo del motor (ej. "Error de actualización
                // en Engine: ... value not exist in the catalog 'DestinoEconomicoInmueble'").
                // El rechazo no se reintenta: falla de una vez y deja el mensaje para la evidencia.
                By anyResponse = By.xpath("//*[contains(@text, \"Validación exitosa y procesamiento exitoso.\")"
                                + " or contains(@text, \"Decisión procesada\")"
                                + " or contains(@text, \"Orden guardada exitosamente\")"
                                + " or contains(@text, \"Error de actualización en Engine\")"
                                + " or contains(@text, \"no cumple validaciones\")] | "
                                + BUSINESS_REJECTION_DIALOG.toString().replace("By.xpath: ", ""));
                clickWithRetry(PROCESS_DECISION_BTN, anyResponse, TIMEOUT_ERROR_MESSAGE);
                if (findNow(ENGINE_ERROR_DIALOG).isEmpty() && findNow(DECISION_PROCESSED_OR_SAVED).isEmpty()
                                && !findNow(BUSINESS_REJECTION_DIALOG).isEmpty()) {
                        String message = visibleDialogText();
                        takeEvidence(numberId, "decision_rechazada");
                        throw new IllegalStateException("La app rechazó la decisión del análisis: \"" + message + "\".");
                }
                java.util.List<org.openqa.selenium.WebElement> engineError = findNow(ENGINE_ERROR_DIALOG);
                if (!engineError.isEmpty()) {
                        String message = engineError.get(0).getText();
                        takeEvidence(numberId, "decision_rechazada_engine");
                        throw new IllegalStateException("La app rechazó la decisión del análisis: \"" + message
                                        + "\". " + (message.contains("listas") ? "Algún dato de la solicitud (p. ej. la cédula del cónyuge) está en listas restrictivas: use otra en el Excel." : "Revise en el Excel los datos del producto (por ejemplo el destino económico)."));
                }

                // Normalmente aquí salta un pop-up de confirmación, así que agregamos un sleep
                // y el click en Aceptar
                sleep(1000);
                try {
                        waitForVisibility(By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]"));
                        takeEvidence(numberId, "Orden_procesada");
                        clickSafely(By.xpath("//android.widget.TextView[@text=\"ACEPTAR\"]"));

                } catch (Exception e) {
                }

        }
}
