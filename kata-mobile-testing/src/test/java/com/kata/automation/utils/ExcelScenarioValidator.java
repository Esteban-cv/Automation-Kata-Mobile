package com.kata.automation.utils;

import java.util.ArrayList;
import java.util.List;

/** Applies configured business parameters without blocking intentional negative tests. */
public final class ExcelScenarioValidator {
    private ExcelScenarioValidator() {
    }

    public static List<ScenarioValidationIssue> validate(
            List<TestScenario> scenarios,
            List<ProductParameter> parameters) {
        List<ScenarioValidationIssue> issues = new ArrayList<>();
        for (TestScenario scenario : scenarios) {
            if (scenario.getCaseId().isBlank()) {
                issues.add(issue(scenario, "idCaso", "El caso debe tener un identificador.", true));
            }
            if (scenario.getAmountText().isBlank()) {
                issues.add(issue(scenario, "amount", "El monto es obligatorio.", true));
                continue;
            }
            ProductParameter amountParameter = find(scenario, parameters, "monto", "amount");
            if (amountParameter != null && !amountParameter.accepts(scenario.getAmountText())) {
                ScenarioValidationIssue.Severity severity = scenario.getType() == TestScenario.Type.VALID
                        ? ScenarioValidationIssue.Severity.ERROR
                        : ScenarioValidationIssue.Severity.WARNING;
                issues.add(new ScenarioValidationIssue(
                        scenario.getCaseId(),
                        "amount",
                        "El monto está fuera de los parámetros configurados para el producto.",
                        severity));
            }
            for (ProductParameter parameter : parameters) {
                // Con el encabezado "Monto" la regla de cada producto solo aplica a su propio producto.
                if (!appliesTo(parameter, scenario)) {
                    continue;
                }
                String value = scenario.get(parameter.parameter());
                if (!value.isBlank() && !parameter.accepts(value)) {
                    ScenarioValidationIssue.Severity severity = scenario.getType() == TestScenario.Type.VALID
                            ? ScenarioValidationIssue.Severity.ERROR
                            : ScenarioValidationIssue.Severity.WARNING;
                    issues.add(new ScenarioValidationIssue(
                            scenario.getCaseId(),
                            parameter.parameter(),
                            "El valor no coincide con los parámetros configurados.",
                            severity));
                }
            }
        }
        return issues;
    }

    private static ProductParameter find(TestScenario scenario, List<ProductParameter> parameters, String... names) {
        return parameters.stream()
                .filter(parameter -> matches(parameter.parameter(), names))
                .filter(parameter -> appliesTo(parameter, scenario))
                .findFirst()
                .orElse(null);
    }

    private static boolean appliesTo(ProductParameter parameter, TestScenario scenario) {
        String product = scenario.getProduct();
        return product.isBlank() || parameter.product().equalsIgnoreCase(product);
    }

    private static boolean matches(String value, String[] names) {
        for (String name : names) {
            if (name.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private static ScenarioValidationIssue issue(
            TestScenario scenario,
            String field,
            String message,
            boolean error) {
        return new ScenarioValidationIssue(
                scenario.getCaseId(),
                field,
                message,
                error ? ScenarioValidationIssue.Severity.ERROR : ScenarioValidationIssue.Severity.WARNING);
    }
}
