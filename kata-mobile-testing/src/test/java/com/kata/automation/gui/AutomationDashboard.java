package com.kata.automation.gui;

import com.kata.automation.tests.FlujoCompletoTest;
import com.kata.automation.utils.DriverManager;
import com.kata.automation.utils.ExcelUtils;
import org.testng.TestNG;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Locale;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Small operator dashboard for launching the existing TestNG flow. */
public final class AutomationDashboard extends JFrame {
    private final JTextArea logArea = new JTextArea();
    private final JLabel selectedFileLabel = new JLabel("Ningún archivo seleccionado");
    private final JSpinner repetitionsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JButton runButton = new JButton("Ejecutar solicitudes");
    private final JButton stopButton = new JButton("Detener ejecución");
    private final JLabel executionStatusLabel = new JLabel("Listo para ejecutar");
    private final JLabel currentRequestLabel = new JLabel("Sin solicitud activa");
    private final JLabel elapsedLabel = new JLabel("00:00");
    private final JLabel liveTimerLabel = new JLabel("00:00");
    private final JLabel passedLabel = new JLabel("0");
    private final JLabel failedLabel = new JLabel("0");
    private final JLabel skippedLabel = new JLabel("0");
    private final JLabel completedLabel = new JLabel("0 / 0");
    private final JProgressBar progressBar = new JProgressBar();
    private final DefaultTableModel statisticsModel = new DefaultTableModel(
            new Object[] {"Corrida", "Estado", "Duración", "Pasaron", "Fallaron", "Omitidas"}, 0);
    private final DefaultListModel<String> eventModel = new DefaultListModel<>();
    private final JTable statisticsTable = new JTable(statisticsModel);
    private final JList<String> eventList = new JList<>(eventModel);
    private final DurationChartPanel durationChart = new DurationChartPanel();
    private final LiveResponseChartPanel responseChart = new LiveResponseChartPanel();
    private File selectedFile;
    private volatile boolean stopRequested;
    private volatile Thread executionThread;
    private volatile Instant currentRequestStarted;
    private int totalPlannedRequests;
    private int completedRequests;
    private int livePassed;
    private int liveFailed;
    private int liveSkipped;
    private final javax.swing.Timer elapsedTimer = new javax.swing.Timer(250, event -> refreshElapsedTime());

    private AutomationDashboard() {
        super("Kata Mobile | Centro de ejecuciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setLocationByPlatform(true);
        add(buildContent());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AutomationDashboard().setVisible(true));
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.setBackground(new Color(238, 242, 247));

        JLabel title = new JLabel("Centro de ejecuciones");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(25, 39, 52));
        JLabel subtitle = new JLabel("Supervisa el dispositivo y entiende el comportamiento de cada solicitud en tiempo real.");
        subtitle.setForeground(new Color(85, 95, 105));

        JPanel heading = new JPanel(new BorderLayout(0, 4));
        heading.setOpaque(false);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.CENTER);
        root.add(heading, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Ejecutar", buildExecutionPanel());
        tabs.addTab("Estadísticas", buildStatisticsPanel());
        root.add(tabs, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildExecutionPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 4, 4, 4));

        JPanel configuration = new JPanel(new GridBagLayout());
        configuration.setBorder(BorderFactory.createTitledBorder("Configuración"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JButton browseButton = new JButton("Seleccionar Excel");
        browseButton.addActionListener(event -> chooseExcelFile());
        constraints.gridx = 0;
        constraints.gridy = 0;
        configuration.add(browseButton, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        configuration.add(selectedFileLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0;
        configuration.add(new JLabel("Cantidad de corridas:"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        configuration.add(repetitionsSpinner, constraints);

        runButton.setBackground(new Color(24, 105, 145));
        runButton.setForeground(Color.WHITE);
        runButton.addActionListener(event -> startExecution());
        stopButton.setBackground(new Color(180, 65, 58));
        stopButton.setForeground(Color.WHITE);
        stopButton.setEnabled(false);
        stopButton.addActionListener(event -> requestStop());
        constraints.gridx = 0;
        constraints.gridy = 2;
        configuration.add(runButton, constraints);
        constraints.gridx = 1;
        configuration.add(stopButton, constraints);
        JPanel liveHeader = new JPanel(new BorderLayout(0, 10));
        liveHeader.setOpaque(false);
        liveHeader.add(configuration, BorderLayout.NORTH);
        liveHeader.add(buildLiveMetricsPanel(), BorderLayout.CENTER);
        panel.add(liveHeader, BorderLayout.NORTH);

        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Actividad reciente"));
        logScroll.setPreferredSize(new Dimension(0, 145));
        panel.add(responseChart, BorderLayout.CENTER);
        panel.add(logScroll, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLiveMetricsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 8));
        panel.setOpaque(false);

        JPanel statusLine = new JPanel(new BorderLayout(8, 0));
        statusLine.setOpaque(false);
        executionStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        executionStatusLabel.setForeground(new Color(24, 105, 145));
        currentRequestLabel.setForeground(new Color(85, 95, 105));
        statusLine.add(executionStatusLabel, BorderLayout.WEST);
        statusLine.add(currentRequestLabel, BorderLayout.CENTER);
        statusLine.add(liveTimerLabel, BorderLayout.EAST);
        panel.add(statusLine, BorderLayout.NORTH);

        JPanel cards = new JPanel(new java.awt.GridLayout(1, 5, 8, 0));
        cards.setOpaque(false);
        cards.add(createMetricCard("Completadas", completedLabel, new Color(24, 105, 145)));
        cards.add(createMetricCard("Exitosas", passedLabel, new Color(36, 145, 106)));
        cards.add(createMetricCard("Fallidas", failedLabel, new Color(212, 91, 72)));
        cards.add(createMetricCard("Omitidas", skippedLabel, new Color(190, 125, 35)));
        cards.add(createMetricCard("Tiempo actual", elapsedLabel, new Color(104, 77, 160)));
        panel.add(cards, BorderLayout.CENTER);

        progressBar.setStringPainted(true);
        progressBar.setString("Esperando ejecución");
        progressBar.setForeground(new Color(24, 105, 145));
        panel.add(progressBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 3));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 225, 232)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        card.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel(title.toUpperCase(Locale.ROOT));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        titleLabel.setForeground(new Color(100, 110, 120));
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        valueLabel.setForeground(accent);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 4, 4, 4));
        statisticsTable.setFillsViewportHeight(true);
        panel.add(statisticsTable.getTableHeader(), BorderLayout.NORTH);
        panel.add(statisticsTable, BorderLayout.CENTER);
        panel.add(durationChart, BorderLayout.SOUTH);

        eventList.setBorder(BorderFactory.createTitledBorder("Eventos de la ejecución"));
        eventList.setPreferredSize(new Dimension(300, 0));
        panel.add(eventList, BorderLayout.EAST);
        return panel;
    }

    private void chooseExcelFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar archivo de solicitudes");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            selectedFileLabel.setText(selectedFile.getAbsolutePath());
            appendLog("Archivo seleccionado: " + selectedFile.getName());
        }
    }

    private void startExecution() {
        if (selectedFile == null) {
            appendLog("Selecciona un archivo Excel antes de ejecutar.");
            return;
        }

        int repetitions = (Integer) repetitionsSpinner.getValue();
        resetLiveMetrics(repetitions);
        stopRequested = false;
        runButton.setEnabled(false);
        stopButton.setEnabled(true);
        appendLog("Iniciando " + repetitions + " corrida(s)...");

        executionThread = new Thread(() -> executeRuns(repetitions), "kata-mobile-executor");
        executionThread.start();
    }

    private void resetLiveMetrics(int repetitions) {
        Object[][] data = ExcelUtils.getTestData(selectedFile.getAbsolutePath(), "Hoja1");
        int requestsPerRun = data == null ? 0 : data.length;
        totalPlannedRequests = requestsPerRun * repetitions;
        completedRequests = 0;
        livePassed = 0;
        liveFailed = 0;
        liveSkipped = 0;
        currentRequestStarted = null;
        elapsedTimer.stop();
        progressBar.setMaximum(Math.max(1, totalPlannedRequests));
        progressBar.setValue(0);
        progressBar.setString(totalPlannedRequests == 0 ? "Sin solicitudes" : "0 / " + totalPlannedRequests);
        executionStatusLabel.setText("Preparando ejecución");
        currentRequestLabel.setText("Leyendo solicitudes del Excel");
        liveTimerLabel.setText("00:00");
        elapsedLabel.setText("00:00");
        completedLabel.setText("0 / " + totalPlannedRequests);
        passedLabel.setText("0");
        failedLabel.setText("0");
        skippedLabel.setText("0");
        responseChart.clear();
    }

    private void requestStop() {
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas que deseas detener la ejecución?\nLa operación Appium actual terminará de forma segura.",
                "Confirmar detención",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        stopRequested = true;
        Thread worker = executionThread;
        if (worker != null) {
            worker.interrupt();
        }
        Thread driverCloser = new Thread(DriverManager::quitDriver, "kata-mobile-stop-driver");
        driverCloser.setDaemon(true);
        driverCloser.start();
        stopButton.setEnabled(false);
        appendLog("Detención solicitada. Cerrando la sesión Appium para permitir una nueva ejecución.");
    }

    private void executeRuns(int repetitions) {
        for (int runNumber = 1; runNumber <= repetitions && !stopRequested; runNumber++) {
            ExecutionListener listener = new ExecutionListener(runNumber);
            Instant started = Instant.now();
            try {
                System.setProperty("kata.excel.path", selectedFile.getAbsolutePath());
                TestNG testng = new TestNG();
                testng.setTestClasses(new Class<?>[] {FlujoCompletoTest.class});
                testng.addListener(listener);
                testng.run();
                Duration duration = Duration.between(started, Instant.now());
                String status = stopRequested
                        ? "DETENIDA"
                        : (testng.getStatus() == 0 ? "OK" : "CON ERROR");
                addStatistics(runNumber, listener, duration, status);
            } catch (Exception exception) {
                Duration duration = Duration.between(started, Instant.now());
                addStatistics(runNumber, listener, duration, stopRequested ? "DETENIDA" : "ERROR DE LANZAMIENTO");
                appendLog("Corrida " + runNumber + " no pudo iniciar: " + exception.getMessage());
            }
        }
        executionThread = null;
        SwingUtilities.invokeLater(() -> {
            runButton.setEnabled(true);
            stopButton.setEnabled(false);
            elapsedTimer.stop();
            currentRequestStarted = null;
            executionStatusLabel.setText(stopRequested ? "Ejecución detenida" : "Ejecución finalizada");
            currentRequestLabel.setText("Sin solicitud activa");
            liveTimerLabel.setText("00:00");
        });
        appendLog(stopRequested ? "Ejecución detenida por el usuario." : "Ejecución finalizada.");
    }

    private void addStatistics(int runNumber, ExecutionListener listener, Duration duration, String status) {
        SwingUtilities.invokeLater(() -> {
            statisticsModel.addRow(new Object[] {
                runNumber, status, formatDuration(duration), listener.passed, listener.failed, listener.skipped
            });
            durationChart.addDuration(duration.toMillis(), status);
        });
    }

    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + System.lineSeparator()));
    }

    private void refreshElapsedTime() {
        Instant started = currentRequestStarted;
        if (started == null) {
            return;
        }
        String elapsed = formatDuration(Duration.between(started, Instant.now()));
        elapsedLabel.setText(elapsed);
        liveTimerLabel.setText(elapsed);
    }

    private void requestStarted(ITestResult result, int runNumber) {
        currentRequestStarted = Instant.now();
        SwingUtilities.invokeLater(() -> {
            executionStatusLabel.setText("Ejecutando solicitud");
            currentRequestLabel.setText("Corrida " + runNumber + " | " + result.getName());
            elapsedLabel.setText("00:00");
            liveTimerLabel.setText("00:00");
            elapsedTimer.start();
        });
    }

    private void requestFinished(ITestResult result, String status) {
        Instant started = currentRequestStarted;
        long durationMillis = started == null ? 0 : Duration.between(started, Instant.now()).toMillis();
        SwingUtilities.invokeLater(() -> {
            elapsedTimer.stop();
            completedRequests++;
            if ("OK".equals(status)) {
                livePassed++;
            } else if ("FALLÓ".equals(status)) {
                liveFailed++;
            } else {
                liveSkipped++;
            }
            completedLabel.setText(completedRequests + " / " + totalPlannedRequests);
            passedLabel.setText(String.valueOf(livePassed));
            failedLabel.setText(String.valueOf(liveFailed));
            skippedLabel.setText(String.valueOf(liveSkipped));
            progressBar.setValue(Math.min(completedRequests, progressBar.getMaximum()));
            progressBar.setString(totalPlannedRequests == 0
                    ? String.valueOf(completedRequests)
                    : completedRequests + " / " + totalPlannedRequests);
            responseChart.addPoint(durationMillis, status);
            elapsedLabel.setText(formatDuration(Duration.ofMillis(durationMillis)));
            liveTimerLabel.setText(formatDuration(Duration.ofMillis(durationMillis)));
            executionStatusLabel.setText("Solicitud " + status.toLowerCase(Locale.ROOT));
            currentRequestLabel.setText("Última respuesta: " + formatDuration(Duration.ofMillis(durationMillis)));
        });
    }

    private static String formatDuration(Duration duration) {
        return String.format("%02dm %02ds", duration.toMinutes(), duration.toSecondsPart());
    }

    private final class ExecutionListener implements ITestListener {
        private final int runNumber;
        private int passed;
        private int failed;
        private int skipped;

        private ExecutionListener(int runNumber) {
            this.runNumber = runNumber;
        }

        @Override
        public void onTestSuccess(ITestResult result) {
            passed++;
            requestFinished(result, "OK");
            recordEvent("Corrida " + runNumber + ": solicitud procesada");
        }

        @Override
        public void onTestFailure(ITestResult result) {
            failed++;
            requestFinished(result, "FALLÓ");
            recordEvent("Corrida " + runNumber + ": fallo en " + result.getName());
        }

        @Override
        public void onTestSkipped(ITestResult result) {
            skipped++;
            requestFinished(result, "OMITIDA");
            recordEvent("Corrida " + runNumber + ": solicitud omitida");
        }

        private void recordEvent(String event) {
            SwingUtilities.invokeLater(() -> eventModel.addElement(event));
        }

        @Override public void onStart(ITestContext context) { }
        @Override public void onFinish(ITestContext context) { }
        @Override public void onTestStart(ITestResult result) { requestStarted(result, runNumber); }
        @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) { }
    }

    private static final class LiveResponseChartPanel extends JPanel {
        private final List<Long> responseTimes = new ArrayList<>();
        private final List<String> statuses = new ArrayList<>();

        private LiveResponseChartPanel() {
            setBorder(BorderFactory.createTitledBorder("Tiempo de respuesta por solicitud · actualización en vivo"));
            setPreferredSize(new Dimension(0, 240));
            setBackground(Color.WHITE);
        }

        private void addPoint(long responseTime, String status) {
            responseTimes.add(responseTime);
            statuses.add(status);
            repaint();
        }

        private void clear() {
            responseTimes.clear();
            statuses.clear();
            repaint();
        }

        @Override
        protected void paintComponent(java.awt.Graphics graphics) {
            super.paintComponent(graphics);
            if (responseTimes.isEmpty()) {
                graphics.setColor(new Color(100, 110, 120));
                graphics.drawString("Esperando la primera respuesta del dispositivo...", 20, 58);
                return;
            }

            java.awt.Graphics2D chart = (java.awt.Graphics2D) graphics.create();
            chart.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            int left = 48;
            int top = 26;
            int right = getWidth() - 22;
            int bottom = getHeight() - 34;
            int width = Math.max(1, right - left);
            int height = Math.max(1, bottom - top);
            long maximum = responseTimes.stream().mapToLong(Long::longValue).max().orElse(1L);

            chart.setColor(new Color(226, 232, 238));
            chart.drawLine(left, bottom, right, bottom);
            chart.drawLine(left, top, left, bottom);
            chart.setColor(new Color(100, 110, 120));
            chart.drawString(formatAxisTime(maximum), 8, top + 5);
            chart.drawString("0s", 20, bottom + 4);

            int count = responseTimes.size();
            int previousX = -1;
            int previousY = -1;
            for (int index = 0; index < count; index++) {
                int x = count == 1 ? left + width / 2 : left + (width * index / (count - 1));
                int y = bottom - (int) (height * (responseTimes.get(index) / (double) maximum));
                if (previousX >= 0) {
                    chart.setColor(new Color(24, 105, 145));
                    chart.drawLine(previousX, previousY, x, y);
                }
                chart.setColor("OK".equals(statuses.get(index))
                        ? new Color(36, 145, 106)
                        : new Color(212, 91, 72));
                chart.fillOval(x - 4, y - 4, 8, 8);
                previousX = x;
                previousY = y;
            }
            chart.dispose();
        }

        private String formatAxisTime(long millis) {
            return String.format(Locale.ROOT, "%.1fs", millis / 1000.0);
        }
    }

    private static final class DurationChartPanel extends JPanel {
        private final List<Long> durations = new ArrayList<>();
        private final List<String> statuses = new ArrayList<>();

        private DurationChartPanel() {
            setBorder(BorderFactory.createTitledBorder("Duración por corrida"));
            setPreferredSize(new Dimension(0, 155));
            setBackground(Color.WHITE);
        }

        private void addDuration(long durationMillis, String status) {
            durations.add(durationMillis);
            statuses.add(status);
            repaint();
        }

        @Override
        protected void paintComponent(java.awt.Graphics graphics) {
            super.paintComponent(graphics);
            if (durations.isEmpty()) {
                graphics.setColor(new Color(100, 110, 120));
                graphics.drawString("Las duraciones aparecerán aquí al finalizar una corrida.", 16, 55);
                return;
            }

            long maximum = durations.stream().mapToLong(Long::longValue).max().orElse(1L);
            int chartWidth = Math.max(1, getWidth() - 48);
            int chartHeight = Math.max(1, getHeight() - 58);
            int barWidth = Math.max(16, chartWidth / Math.max(1, durations.size()) - 12);
            int gap = Math.max(8, (chartWidth - barWidth * durations.size()) / (durations.size() + 1));

            for (int index = 0; index < durations.size(); index++) {
                int barHeight = (int) (chartHeight * (durations.get(index) / (double) maximum));
                int x = 24 + gap + index * (barWidth + gap);
                int y = 28 + chartHeight - barHeight;
                graphics.setColor(statuses.get(index).equals("OK")
                        ? new Color(36, 145, 106) : new Color(212, 91, 72));
                graphics.fillRect(x, y, barWidth, barHeight);
                graphics.setColor(new Color(70, 80, 90));
                graphics.drawString("#" + (index + 1), x, 45 + chartHeight);
            }
        }
    }
}