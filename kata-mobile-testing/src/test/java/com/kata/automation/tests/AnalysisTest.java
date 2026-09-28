package com.kata.automation.tests;

import com.kata.automation.pages.AnalysisPage;

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AnalysisTest extends BaseTest {

    AnalysisPage analysisPage;

    @BeforeClass
    public void setUp() {
        analysisPage = new AnalysisPage(driver);
    }

    @Test(description = "Verifica el llenado completo de la pestaña Análisis Cualitativo y Cuantitativo")
    public void verificarPestanaAnalisis() {
        try {
            System.out.println("========== INICIANDO PRUEBA DE ANÁLISIS ==========");


            System.out.println("Ejecutando llenado de Análisis...");
            analysisPage.fillQualitativeAndQuantitativeAnalysis();
            // 3. Validación de éxito
            String successMessage = "✅ Prueba superada: Se completaron las Geo Referencias, Ingreso de Referencias y el Formulario Financiero sin errores.";
            System.out.println(successMessage);
            Reporter.log(successMessage, true);

        } catch (Exception e) {
            takeScreenshot("error_pestana_analisis_" + e);
            throw new RuntimeException("❌ Falló la ejecución en la pestaña de Análisis.", e);
        }
    }
}