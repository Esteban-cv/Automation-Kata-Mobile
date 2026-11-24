package com.kata.automation.tests;

import org.testng.annotations.Test;

import com.kata.automation.pages.CodebtorPage;
import com.kata.automation.utils.ExcelUtils;
import com.kata.automation.utils.TestScenario;

/**
 * Prueba aislada del codeudor, con la orden abierta en "Registro del producto": toma la
 * primera solicitud del Excel que trae codeudor (o la del caso -Dkata.codeudor.caso=ID), abre
 * "Codeudor(es)", lo registra y vuelve al registro del producto.
 * Uso: mvn test "-Dsuite=testng-codeudor.xml"
 */
public class CodebtorFormTest extends BaseTest {

    @Test
    public void registerCodebtorOnOpenOrder() {
        String excel = System.getProperty("kata.excel.path", "src/test/resources/datos_solicitudes_configurado.xlsx");
        String wantedCase = System.getProperty("kata.codeudor.caso", "");
        TestScenario scenario = ExcelUtils.getScenarios(excel, "Hoja1").stream()
                .filter(CodebtorPage::requested)
                .filter(s -> wantedCase.isBlank() || wantedCase.equalsIgnoreCase(s.getCaseId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Ninguna solicitud del Excel trae codeudor."));
        CodebtorPage page = new CodebtorPage(driver);
        if (page.isFormOpen()) {
            page.completeOpenForm(scenario.get("Número de identificación"), scenario);
        } else {
            page.registerCodebtor(scenario.get("Número de identificación"), scenario);
        }
    }
}
