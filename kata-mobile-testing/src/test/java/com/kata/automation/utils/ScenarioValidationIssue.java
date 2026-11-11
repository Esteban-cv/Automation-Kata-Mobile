package com.kata.automation.utils;

/** A non-destructive validation finding for one Excel scenario. */
public record ScenarioValidationIssue(
        String caseId,
        String field,
        String message,
        Severity severity) {

    public enum Severity {
        ERROR,
        WARNING
    }
}
