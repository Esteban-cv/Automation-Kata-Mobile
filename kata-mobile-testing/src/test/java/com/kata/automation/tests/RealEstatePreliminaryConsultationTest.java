package com.kata.automation.tests;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.kata.automation.pages.OrdersPage.ConsultationOutcome;
import com.kata.automation.pages.RealEstatePreliminaryConsultationPage;
import com.kata.automation.utils.ErrorEvidenceHandler;
import com.kata.automation.utils.ExcelUtils;
import com.kata.automation.utils.TestScenario;

/**
 * Consulta preliminar de "Originación de inmueble" para los cuatro tipos de cliente:
 * nuevo, renovado, mora menor a 30 días (siguen a radicación) y mora mayor o listas
 * (la solicitud termina en la consulta).
 *
 * <p>
 * Lee el Excel por nombre de columna. Ruta y hoja configurables con
 * {@code -Dkata.inmueble.excel.path} y {@code -Dkata.inmueble.excel.sheet}.
 * La columna opcional "Resultado consulta esperado" (NUEVO, RENOVADO, MORA_MENOR,
 * MORA_MAYOR o LISTAS) hace que el test valide el mensaje que devolvió la app.
 * </p>
 */
public class RealEstatePreliminaryConsultationTest extends BaseTest {

    static final String DEFAULT_EXCEL = "src/test/resources/datos_inmueble.xlsx";
    static final String DEFAULT_SHEET = "Hoja1";

    private static final Map<String, ConsultationOutcome> EXPECTED_OUTCOMES = Map.of(
            "NUEVO", ConsultationOutcome.NEW_CLIENT,
            "CLIENTENUEVO", ConsultationOutcome.NEW_CLIENT,
            "RENOVADO", ConsultationOutcome.RENEWED_CLIENT,
            "CLIENTERENOVADO", ConsultationOutcome.RENEWED_CLIENT,
            "MORAMENOR", ConsultationOutcome.MINOR_ARREARS,
            "MORAMAYOR", ConsultationOutcome.BLOCKED_BY_ARREARS,
            "LISTAS", ConsultationOutcome.BLOCKED_BY_LISTS,
            "APROBADO", ConsultationOutcome.APPROVED);

    private RealEstatePreliminaryConsultationPage consultationPage;

    @BeforeClass
    public void setUpPages() {
        consultationPage = new RealEstatePreliminaryConsultationPage(driver);
    }

    @DataProvider(name = "inmuebleData", parallel = false)
    public Object[][] getData() {
        String excelPath = System.getProperty("kata.inmueble.excel.path", DEFAULT_EXCEL);
        String sheetName = System.getProperty("kata.inmueble.excel.sheet", DEFAULT_SHEET);
        List<TestScenario> scenarios = ExcelUtils.getScenarios(excelPath, sheetName);
        return scenarios.stream()
                .filter(TestScenario::shouldContinue)
                .map(scenario -> new Object[] { scenario })
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "inmuebleData")
    public void consultaPreliminarInmueble(TestScenario scenario) {
        String caseId = scenario.getCaseId().isBlank() ? scenario.getNumberId() : scenario.getCaseId();
        System.setProperty("kata.current.case.id", caseId);

        ConsultationOutcome outcome;
        try {
            consultationPage.createRealEstateOrder();
            outcome = consultationPage.completePreliminaryConsultation(scenario);
        } catch (RuntimeException exception) {
            ErrorEvidenceHandler.capture(driver, caseId, "consulta_preliminar_inmueble", scenario, exception);
            takeScreenshot("Error_Consulta_Inmueble_" + scenario.getNumberId());
            throw exception;
        }

        Reporter.log("Caso " + caseId + ": resultado de la consulta = " + outcome, true);

        ConsultationOutcome expected = expectedOutcome(scenario);
        if (expected != null) {
            Assert.assertEquals(outcome, expected,
                    "La consulta del caso " + caseId + " no devolvió el tipo de cliente esperado.");
        }
    }

    /** Null cuando la fila no indica un resultado esperado. */
    static ConsultationOutcome expectedOutcome(TestScenario scenario) {
        String raw = scenario.get("Resultado consulta esperado", "resultadoConsulta");
        if (raw.isBlank()) {
            return null;
        }
        String key = Normalizer.normalize(raw, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT).replaceAll("[^A-Z]", "");
        ConsultationOutcome outcome = EXPECTED_OUTCOMES.get(key);
        if (outcome == null) {
            throw new IllegalArgumentException("Valor inválido en 'Resultado consulta esperado': " + raw
                    + ". Use NUEVO, RENOVADO, MORA_MENOR, MORA_MAYOR o LISTAS.");
        }
        return outcome;
    }
}
