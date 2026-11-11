package com.kata.automation.utils;

import java.math.BigDecimal;
import java.util.List;

/** Business parameter loaded from the Excel parameter sheet. */
public record ProductParameter(
        String product,
        String parameter,
        BigDecimal minimum,
        BigDecimal maximum,
        List<String> allowedValues,
        boolean required) {

    public boolean accepts(String value) {
        if (value == null || value.isBlank()) {
            return !required;
        }
        if (!allowedValues.isEmpty()) {
            return allowedValues.stream().anyMatch(option -> option.equalsIgnoreCase(value.trim()));
        }
        BigDecimal numericValue;
        try {
            numericValue = new BigDecimal(value.replace(",", "").replace("$", "").trim());
        } catch (NumberFormatException exception) {
            return false;
        }
        return (minimum == null || numericValue.compareTo(minimum) >= 0)
                && (maximum == null || numericValue.compareTo(maximum) <= 0);
    }
}
