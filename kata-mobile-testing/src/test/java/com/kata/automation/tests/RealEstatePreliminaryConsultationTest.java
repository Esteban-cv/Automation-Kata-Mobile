package com.kata.automation.tests;

import java.util.List;

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.kata.automation.pages.AnalysisDecisionPage;
import com.kata.automation.pages.AnalysisPage;
import com.kata.automation.pages.OrdersPage.ConsultationOutcome;
import com.kata.automation.pages.RadicationPage;
import com.kata.automation.pages.RealEstatePreliminaryConsultationPage;
import com.kata.automation.pages.RealEstateProductRegistrationPage;
import com.kata.automation.utils.ErrorEvidenceHandler;
import com.kata.automation.utils.ExcelUtils;
import com.kata.automation.utils.TestScenario;

/**
 * Originación de inmueble: consulta preliminar y radicación.
 *
 * <p>
 * El tipo de cliente no viene del Excel: lo dice la app después de "Consultar cliente".
 * </p>
 * <ul>
 * <li>Mora mayor o listas: la solicitud termina en la consulta preliminar.</li>
 * <li>Cliente nuevo: la radicación se llena campo por campo.</li>
 * <li>Cliente renovado o mora menor: la radicación se completa como en microcrédito
 * (la app ya trae parte de la información).</li>
 * </ul>
 *
 * <p>
 * Lee el Excel por nombre de columna. Ruta y hoja configurables con
 * {@code -Dkata.inmueble.excel.path} y {@code -Dkata.inmueble.excel.sheet}.
 * Para depurar la radicación sobre una orden que ya está abierta en el celular (con la
 * consulta hecha) use {@code -Dkata.inmueble.desde=radicacion} y, si no es cliente nuevo,
 * {@code -Dkata.inmueble.cliente=RENEWED_CLIENT}.
 * </p>
 */
public class RealEstatePreliminaryConsultationTest extends BaseTest {

    static final String DEFAULT_EXCEL = "src/test/resources/datos_solicitudes_configurado.xlsx";
    static final String DEFAULT_SHEET = "Hoja1";

    private RealEstatePreliminaryConsultationPage consultationPage;
    private RadicationPage radicationPage;
    private AnalysisPage analysisPage;
    private RealEstateProductRegistrationPage productPage;
    private AnalysisDecisionPage decisionPage;

    @BeforeClass
    public void setUpPages() {
        consultationPage = new RealEstatePreliminaryConsultationPage(driver);
        radicationPage = new RadicationPage(driver);
        analysisPage = new AnalysisPage(driver);
        productPage = new RealEstateProductRegistrationPage(driver);
        decisionPage = new AnalysisDecisionPage(driver);
    }

    @DataProvider(name = "inmuebleData", parallel = false)
    public Object[][] getData() {
        String excelPath = System.getProperty("kata.inmueble.excel.path", DEFAULT_EXCEL);
        String sheetName = System.getProperty("kata.inmueble.excel.sheet", DEFAULT_SHEET);
        List<TestScenario> scenarios = ExcelUtils.getScenarios(excelPath, sheetName);
        return scenarios.stream()
                .filter(TestScenario::shouldContinue)
                // Plantilla única: este test solo toma las filas de inmueble.
                .filter(scenario -> FlujoCompletoTest.isRealEstateOrder(scenario.get("Tipo de orden", "orderType")))
                // -Dkata.inmueble.caso=ID corre solo esa fila.
                .filter(scenario -> System.getProperty("kata.inmueble.caso", "").isBlank()
                        || System.getProperty("kata.inmueble.caso").equalsIgnoreCase(scenario.getCaseId()))
                .map(scenario -> new Object[] { scenario })
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "inmuebleData")
    public void originacionInmueble(TestScenario scenario) {
        String caseId = scenario.getCaseId().isBlank() ? scenario.getNumberId() : scenario.getCaseId();
        System.setProperty("kata.current.case.id", caseId);
        // Etapa desde la que arranca (para depurar sobre una orden ya abierta en el celular):
        // consulta (por defecto), radicacion, analisis, producto o decision.
        String startAt = System.getProperty("kata.inmueble.desde", "consulta").toLowerCase();
        int firstStage = switch (startAt) {
            case "radicacion" -> 2;
            case "analisis" -> 3;
            case "producto" -> 4;
            case "decision" -> 5;
            default -> 1;
        };

        ConsultationOutcome outcome = ConsultationOutcome.valueOf(
                System.getProperty("kata.inmueble.cliente", "NEW_CLIENT"));
        if (firstStage <= 1) {
            // -Dkata.inmueble.crearOrden=false usa la orden que ya está abierta en el celular.
            boolean createOrder = !"false".equalsIgnoreCase(System.getProperty("kata.inmueble.crearOrden", "true"));
            outcome = runStage(scenario, caseId, "consulta_preliminar_inmueble", () -> {
                if (createOrder) {
                    consultationPage.createRealEstateOrder();
                }
                return consultationPage.completePreliminaryConsultation(scenario);
            });
            Reporter.log("Caso " + caseId + ": resultado de la consulta = " + outcome, true);
            if (outcome.isBlocked()) {
                Reporter.log("Caso " + caseId + ": la solicitud termina en la consulta preliminar.", true);
                return;
            }
        } else {
            Reporter.log("Caso " + caseId + ": se inicia en '" + startAt + "' como " + outcome, true);
        }

        // -Dkata.inmueble.hasta=consulta detiene la corrida después de la consulta preliminar.
        if ("consulta".equalsIgnoreCase(System.getProperty("kata.inmueble.hasta", ""))) {
            Reporter.log("Caso " + caseId + ": se detiene después de la consulta (kata.inmueble.hasta).", true);
            return;
        }

        final ConsultationOutcome clientType = outcome;
        if (firstStage <= 2) {
            runStage(scenario, caseId, "radicacion_inmueble", () -> {
                radicate(scenario, clientType);
                return null;
            });
            Reporter.log("Caso " + caseId + ": radicación completada (" + clientType + ").", true);
        }

        if (firstStage <= 3) {
            runStage(scenario, caseId, "analisis_inmueble", () -> {
                analysisPage.fillQualitativeAndQuantitativeAnalysis(scenario.getNumberId());
                return null;
            });
            Reporter.log("Caso " + caseId + ": análisis cualitativo y cuantitativo completado.", true);
        }

        if (firstStage <= 4) {
            runStage(scenario, caseId, "registro_producto_inmueble", () -> {
                productPage.fillRealEstateProduct(scenario.getNumberId(), scenario);
                return null;
            });
            Reporter.log("Caso " + caseId + ": registro del producto completado.", true);
        }

        runStage(scenario, caseId, "decision_analisis_inmueble", () -> {
            decisionPage.fillAnalysisDecision(
                    valueOr(scenario, "Negocio del Cliente", "Lugar de la entrevista"),
                    valueOr(scenario, "Satisfactorio", "Resultado de la entrevista"),
                    valueOr(scenario, "Capital de trabajo", "Propósito de la relación contractual"),
                    valueOr(scenario, "Correo electrónico P", "Recepción de extractos"),
                    valueOr(scenario, "Solicitud viable para aprobación.", "Concepto del analista"),
                    valueOr(scenario, "Continuar", "Decisión de análisis"),
                    scenario.getNumberId());
            return null;
        });
        Reporter.log("Caso " + caseId + ": decisión del análisis procesada.", true);
    }

    private void radicate(TestScenario data, ConsultationOutcome clientType) {
        String numberId = data.getNumberId();
        // Inmueble: siempre la radicación que solo llena lo que la app trae vacío
        // (cliente nuevo: todo; renovado o mora menor: lo que falte).
        if (true) {
            radicationPage.completeRadicationNewClient(numberId, data);
            return;
        }
        radicationPage.completeRadicationExistingClient(numberId,
                data.get("Último año cursado"),
                data.get("Afiliación"),
                data.get("Acceso a internet"),
                data.get("Correo electrónico"),
                data.get("Departamento"),
                data.get("Ciudad"),
                data.get("Barrio"),
                data.get("Teléfono del negocio"));
    }

    private static String valueOr(TestScenario data, String defaultValue, String column) {
        String value = data.get(column);
        return value.isBlank() ? defaultValue : value;
    }

    private <T> T runStage(TestScenario scenario, String caseId, String stage, java.util.function.Supplier<T> step) {
        try {
            return step.get();
        } catch (RuntimeException exception) {
            ErrorEvidenceHandler.capture(driver, caseId, stage, scenario, exception);
            takeScreenshot("Error_" + stage + "_" + scenario.getNumberId());
            throw exception;
        }
    }
}
