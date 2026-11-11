package com.kata.automation.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import io.appium.java_client.android.AndroidDriver;

/**
 * Captures a consistent diagnostic bundle for unexpected or expected failures.
 * It does not decide whether a test passes; the scenario expectation owns that decision.
 */
public final class ErrorEvidenceHandler {
    private static final Pattern TEXT_ATTRIBUTE = Pattern.compile("(?:text|content-desc)=\"([^\"]*)\"");

    private ErrorEvidenceHandler() {
    }

    public static CaptureResult capture(
            AndroidDriver driver,
            String caseId,
            String step,
            TestScenario scenario,
            Throwable failure) {
        String safeCaseId = safeName(caseId);
        String safeStep = safeName(step);
        Path folder = EvidenceStorage.caseDirectory(safeCaseId).resolve("errores");
        String timestamp = String.valueOf(System.currentTimeMillis());
        try {
            Files.createDirectories(folder);
            Path screenshot = folder.resolve(timestamp + "_" + safeStep + ".png");
            Path source = folder.resolve(timestamp + "_" + safeStep + ".xml");
            Path message = folder.resolve(timestamp + "_" + safeStep + ".txt");
            Path result = folder.resolve(timestamp + "_" + safeStep + ".json");

            Files.copy(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(),
                    screenshot, StandardCopyOption.REPLACE_EXISTING);
            String pageSource = driver.getPageSource();
            Files.writeString(source, pageSource == null ? "" : pageSource, StandardCharsets.UTF_8);
            String visibleText = extractVisibleText(pageSource);
            String exceptionText = failure == null ? "" : String.valueOf(failure.getMessage());
            String fullMessage = visibleText.isBlank() ? exceptionText : visibleText;
            Files.writeString(message, fullMessage, StandardCharsets.UTF_8);
            Category category = classify(fullMessage);
            Files.writeString(result, resultJson(caseId, step, scenario, category, fullMessage, screenshot, source, message),
                    StandardCharsets.UTF_8);
            ExecutionLog.error("Evidencia de error guardada para " + caseId + " en " + folder);
            return new CaptureResult(folder, fullMessage, category);
        } catch (IOException | RuntimeException captureFailure) {
            ExecutionLog.error("No se pudo guardar la evidencia universal de " + caseId, captureFailure);
            return new CaptureResult(folder, "", Category.UNKNOWN);
        }
    }

    private static String extractVisibleText(String pageSource) {
        if (pageSource == null || pageSource.isBlank()) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        Matcher matcher = TEXT_ATTRIBUTE.matcher(pageSource);
        while (matcher.find()) {
            String value = matcher.group(1).replace("&quot;", "\"").trim();
            if (!value.isBlank() && text.indexOf(value) < 0) {
                if (text.length() > 0) {
                    text.append(System.lineSeparator());
                }
                text.append(value);
            }
        }
        return text.toString();
    }

    private static String resultJson(
            String caseId,
            String step,
            TestScenario scenario,
            Category category,
            String message,
            Path screenshot,
            Path source,
            Path messageFile) {
        String expected = scenario == null ? "" : scenario.getExpectedResult().name();
        return "{\n"
                + "  \"caseId\": \"" + json(caseId) + "\",\n"
                + "  \"step\": \"" + json(step) + "\",\n"
                + "  \"capturedAt\": \"" + Instant.now() + "\",\n"
                + "  \"expectedResult\": \"" + json(expected) + "\",\n"
                + "  \"category\": \"" + category.name() + "\",\n"
                + "  \"message\": \"" + json(message) + "\",\n"
                + "  \"screenshot\": \"" + json(screenshot.getFileName().toString()) + "\",\n"
                + "  \"pageSource\": \"" + json(source.getFileName().toString()) + "\",\n"
                + "  \"messageFile\": \"" + json(messageFile.getFileName().toString()) + "\"\n"
                + "}\n";
    }

    private static String json(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n");
    }

    private static String safeName(String value) {
        String clean = value == null || value.isBlank() ? "sin_id" : value;
        return clean.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static Category classify(String message) {
        String normalized = message == null ? "" : message.toLowerCase();
        if (normalized.isBlank()) {
            return Category.NO_RESPONSE;
        }
        if (normalized.contains("tasa obtenida desde el código de pizarra es cero")
                || normalized.contains("tasa obtenida desde el codigo de pizarra es cero")) {
            return Category.CONTROLLED_REJECTION;
        }
        if (normalized.contains("capital indicado es menor al mínimo permitido para el producto")
                || normalized.contains("capital indicado es menor al minimo permitido para el producto")) {
            return Category.CONTROLLED_REJECTION;
        }
        if (normalized.contains("timeout") || normalized.contains("tiempo de espera")
                || normalized.contains("internal server") || normalized.contains("connection")
                || normalized.contains("consulta") || normalized.contains("parametr")) {
            return Category.TECHNICAL_ERROR;
        }
        if (normalized.contains("límite") || normalized.contains("limite")
                || normalized.contains("inválid") || normalized.contains("invalid")
                || normalized.contains("rechaz") || normalized.contains("no permitido")
                || normalized.contains("supera")) {
            return Category.CONTROLLED_REJECTION;
        }
        return Category.UNKNOWN;
    }

    public enum Category {
        CONTROLLED_REJECTION,
        TECHNICAL_ERROR,
        NO_RESPONSE,
        UNKNOWN
    }

    public record CaptureResult(Path folder, String visibleMessage, Category category) {
    }
}
