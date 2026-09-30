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
                        "//android.widget.TextView[@text=\"¿Donde recibe información de extractos, certificaciones tributarias y reporte anual de costos totales?\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        private static final By ANALYST_CONCEPT = By.xpath(
                        "//android.widget.TextView[@text=\"Concepto de aprobación del analista\"]/following::android.widget.EditText[1]");

        private static final By ANALYSIS_DECISION = By.xpath(
                        "//android.widget.TextView[@text=\"Decisión del análisis\"]/following::android.view.View[@content-desc=\"Menú desplegable\"][1]");

        private static final By PROCESS_DECISION_BTN = By.xpath(
                        "//android.widget.TextView[@text=\"Procesar decisión\"]/following::android.view.View[1]");

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

                click(COMMITTEE_SUMMARY_BTN);
                waitForVisibility(ACCEPT_BUTTON);
                click(ACCEPT_BUTTON);

                scrollAndClickReliable("Lugar de entrevista", INTERVIEW_PLACE, 0.15);
                clickSafely(byVisibleText(interviewPlace));

                scrollAndClickReliable("Resultado de entrevista", INTERVIEW_RESULT, 0.15);
                clickSafely(byVisibleText(interviewResult));

                scrollAndClickReliable("Propósito de la relación contractual", CONTRACTUAL_PURPOSE, 0.15);
                clickSafely(byVisibleText(contractualPurpose));

                scrollAndClickReliable(
                                "¿Donde recibe información de extractos, certificaciones tributarias y reporte anual de costos totales?",
                                EXTRACTS_INFO_WHERE, 0.20);
                clickSafely(byVisibleText(extractsInfo));

                scrollToText("Concepto de aprobación del analista", 0.15);
                type(ANALYST_CONCEPT, analystConcept);

                takeEvidence(numberId, "Resumen_comite");

                // scrollToText("Observaciones del gerente de oficina", 0.15);
                // type(MANAGER_OBSERVATIONS, managerObservations);

                scrollAndClickReliable("Decisión del análisis", ANALYSIS_DECISION, 0.15);
                clickSafely(byVisibleText(decision));

                // Botón final para enviar todo
                scrollAndClickReliable("Procesar decisión", PROCESS_DECISION_BTN, 0.15);

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
