package com.kata.automation.tests;

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.kata.automation.pages.AnalysisDecisionPage;

public class AnalysisDecisionTest extends BaseTest {

    private AnalysisDecisionPage analysisDecisionPage;

    @BeforeClass
    public void setUp() {
        analysisDecisionPage = new AnalysisDecisionPage(driver);
    }

    @Test(groups = "smoke", description = "Verifica el procesamiento de la decisión del análisis")
    public void verificarDecisionDelAnalisis() {
        try {
            System.out.println("========== INICIANDO PRUEBA DE DECISIÓN DEL ANÁLISIS ==========");

            analysisDecisionPage.fillAnalysisDecision(
                    "Negocio del Cliente",
                    "Satisfactorio",
                    "Capital de trabajo",
                    "Correo electrónico P",
                    "Solicitud viable para aprobación.",
                    "Continuar","38569302");

            String successMessage = "Prueba superada: se procesó la decisión del análisis correctamente.";
            System.out.println(successMessage);
            Reporter.log(successMessage, true);
        } catch (Exception e) {
            takeScreenshot("error_decision_analisis");
            throw new RuntimeException("Falló la ejecución en la decisión del análisis.", e);
        }
    }
}
