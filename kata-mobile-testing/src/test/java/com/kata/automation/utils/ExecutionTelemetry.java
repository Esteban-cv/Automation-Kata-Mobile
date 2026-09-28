package com.kata.automation.utils;

/** Sends the five business-stage events from the end-to-end flow to the dashboard. */
public final class ExecutionTelemetry {
    public static final int TOTAL_STAGES = 5;

    public interface Listener {
        void onStageStarted(String stageName, int stageNumber, int totalStages);

        void onStageFinished(String stageName, int stageNumber, boolean successful,
                String detail, long durationMillis);
    }

    private static volatile Listener listener;

    private ExecutionTelemetry() {
    }

    public static void setListener(Listener newListener) {
        listener = newListener;
    }

    public static void clearListener(Listener expectedListener) {
        if (listener == expectedListener) {
            listener = null;
        }
    }

    public static void stageStarted(String stageName, int stageNumber) {
        Listener currentListener = listener;
        if (currentListener != null) {
            currentListener.onStageStarted(stageName, stageNumber, TOTAL_STAGES);
        }
    }

    public static void stageFinished(String stageName, int stageNumber, boolean successful,
            String detail, long durationMillis) {
        Listener currentListener = listener;
        if (currentListener != null) {
            currentListener.onStageFinished(stageName, stageNumber, successful, detail, durationMillis);
        }
    }
}
