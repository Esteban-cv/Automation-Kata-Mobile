package com.kata.automation.gui;

import com.kata.automation.tests.FlujoCompletoTest;
import com.kata.automation.utils.DriverManager;
import com.kata.automation.utils.ExcelUtils;
import com.kata.automation.utils.ExecutionHistoryStore;
import com.kata.automation.utils.ExecutionLog;
import com.kata.automation.utils.ExecutionTelemetry;
import com.kata.automation.utils.EvidenceStorage;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNG;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Centro de control para lanzar y supervisar ejecuciones del flujo TestNG.
 *
 * <p>Vocabulario de la interfaz: una <b>ejecución</b> procesa el archivo Excel completo;
 * cada fila del Excel es una <b>solicitud</b>; cada solicitud atraviesa varias <b>etapas</b>.
 */
public final class AutomationDashboard extends JFrame {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String[] STAGE_LABELS = {"Consulta", "Radicación", "Análisis", "Producto", "Decisión"};
    private static final String EXCEL_SHEET = "Hoja1";
    private static final int MAX_RUNS_IN_CHART = 30;
    private static final int MAX_ACTIONS_IN_CHART = 8;

    // ---------------------------------------------------------------- Configuración
    private final JLabel fileNameLabel = new JLabel("Ningún archivo seleccionado");
    private final JLabel fileHintLabel = new JLabel("Arrastra un .xlsx aquí o usa el botón");
    private final JLabel loadedRowsLabel = new JLabel("Sin datos cargados");
    private final JLabel planSummaryLabel = new JLabel(" ");
    private final JSpinner executionsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final ModernButton browseButton =
            new ModernButton("Seleccionar archivo Excel…", ModernButton.Kind.SECONDARY);
    private final ModernButton runButton = new ModernButton("Iniciar ejecución", ModernButton.Kind.PRIMARY);
    private final ModernButton stopButton = new ModernButton("Detener ejecución", ModernButton.Kind.DANGER);

    // ---------------------------------------------------------------- Estado en vivo
    private final PillLabel statusPill = new PillLabel("LISTO");
    private final JLabel executionStatusLabel = new JLabel("Listo para ejecutar");
    private final JLabel currentRequestLabel = new JLabel("Selecciona un archivo Excel para comenzar");
    private final JLabel stageStatusLabel = new JLabel("Sin etapa activa");
    private final JLabel totalTimerLabel = new JLabel("00:00:00");
    private final JLabel stageTimeLabel = new JLabel("00:00:00");
    private final JLabel completedLabel = new JLabel("0 / 0");
    private final JLabel passedLabel = new JLabel("0");
    private final JLabel failedLabel = new JLabel("0");
    private final JLabel blockedLabel = new JLabel("0");
    private final JLabel skippedLabel = new JLabel("0");
    private final JProgressBar progressBar = new JProgressBar();
    private final JTextArea logArea = new JTextArea();
    private final LiveStagePanel stageChart = new LiveStagePanel();

    // ---------------------------------------------------------------- Historial
    private final DefaultTableModel statisticsModel = readOnlyModel(
            "Ejecución", "Estado", "Duración", "Exitosas", "Fallidas", "Bloqueadas", "Omitidas");
    private final DefaultTableModel orderStatisticsModel = readOnlyModel(
            "Ejecución", "Cédula", "Estado", "Duración");
    private final DefaultListModel<String> eventModel = new DefaultListModel<>();
    private final JTable statisticsTable = new JTable(statisticsModel);
    private final JTable orderStatisticsTable = new JTable(orderStatisticsModel);
    private final JList<String> eventList = new JList<>(eventModel);
    private final BarChartPanel durationChart = new BarChartPanel(
            "Duración por ejecución (últimas " + MAX_RUNS_IN_CHART + ")",
            "Las duraciones aparecerán al finalizar una ejecución.");
    private final BarChartPanel stageAverageChart = new BarChartPanel(
            "Promedio histórico por etapa", "Aún no hay etapas registradas.");
    private final BarChartPanel actionAverageChart = new BarChartPanel(
            "Acciones más lentas (promedio)", "Las acciones instrumentadas aparecerán aquí.");

    private final List<Long> runDurations = new ArrayList<>();
    private final List<String> runStatuses = new ArrayList<>();
    private final long[] stageTotals = new long[ExecutionTelemetry.TOTAL_STAGES];
    private final int[] stageCounts = new int[ExecutionTelemetry.TOTAL_STAGES];
    private final Map<String, Long> actionTotals = new LinkedHashMap<>();
    private final Map<String, Integer> actionCounts = new LinkedHashMap<>();

    // ---------------------------------------------------------------- Control de ejecución
    private File selectedFile;
    private int loadedRows;
    private volatile boolean stopRequested;
    private volatile Thread executionThread;
    private volatile Instant currentRequestStarted;
    private int totalPlannedStages;
    private int completedStages;
    private int livePassedStages;
    private int liveFailedStages;
    private int liveBlockedStages;
    private int liveSkippedStages;
    private Instant runStarted;
    private Instant currentStageStarted;
    private final Timer elapsedTimer = new Timer(250, event -> refreshElapsedTime());

    private AutomationDashboard() {
        super("Kata Mobile | Centro de control de ejecuciones");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                shutdownApplication();
            }
        });
        setIconImage(createAppIcon(64));
        setMinimumSize(new Dimension(1100, 720));
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
        executionsSpinner.addChangeListener(event -> updatePlanSummary());
        loadHistory();
        applyIdleState();
    }

    public static void main(String[] args) {
        EvidenceStorage.ensureEvidenceDirectory();
        installLookAndFeel();
        SwingUtilities.invokeLater(() -> new AutomationDashboard().setVisible(true));
    }

    /**
     * Usa FlatLaf si está en el classpath (recomendado, ver README del cambio);
     * de lo contrario recurre a Nimbus, que ya viene con el JDK.
     */
    private static void installLookAndFeel() {
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ProgressBar.arc", 999);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 12);
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            return;
        } catch (Exception ignored) {
            // FlatLaf no disponible: se intenta Nimbus.
        }
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception ignored) {
            // Se mantiene el aspecto por defecto.
        }
    }

    // ================================================================ Tema visual

    private static final class Theme {
        static final Color BACKGROUND = new Color(0xF2F4F8);
        static final Color SURFACE = Color.WHITE;
        static final Color BORDER = new Color(0xDCE2E9);
        static final Color GRID = new Color(0xE6EBF0);
        static final Color TEXT = new Color(0x1B2733);
        static final Color MUTED = new Color(0x667380);
        static final Color NAVY = new Color(0x0D2B4E);
        static final Color PRIMARY = new Color(0x1565C0);
        static final Color RUNNING = new Color(0x2F8CFF);
        static final Color SUCCESS = new Color(0x1E9E6A);
        static final Color WARNING = new Color(0xD98E04);
        static final Color DANGER = new Color(0xD64545);
        static final Color NEUTRAL = new Color(0x7B8794);
        static final Color PENDING = new Color(0xC9D2DC);
        static final Color VIOLET = new Color(0x6D4AC4);
        static final String FONT_FAMILY = resolveFontFamily();

        private Theme() { }

        static Font font(int style, int size) {
            return new Font(FONT_FAMILY, style, size);
        }

        private static String resolveFontFamily() {
            Set<String> available = new HashSet<>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            for (String candidate : new String[] {"Segoe UI", "Inter", "Helvetica Neue", "Ubuntu", "Roboto"}) {
                if (available.contains(candidate)) {
                    return candidate;
                }
            }
            return Font.SANS_SERIF;
        }
    }

    private static Graphics2D quality(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    private static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    private static JLabel caption(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.font(Font.BOLD, 12));
        label.setForeground(Theme.MUTED);
        return label;
    }

    private static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.font(Font.BOLD, 15));
        label.setForeground(Theme.TEXT);
        return label;
    }

    private static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // ================================================================ Construcción de la interfaz

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BACKGROUND);
        root.add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.font(Font.BOLD, 13));
        tabs.addTab("Ejecución en vivo", buildExecutionPanel());
        tabs.addTab("Historial y estadísticas", buildStatisticsPanel());

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(14, 18, 18, 18));
        body.add(tabs, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBackground(Theme.NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JLabel title = new JLabel("Kata Mobile");
        title.setFont(Theme.font(Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Centro de control de ejecuciones");
        subtitle.setFont(Theme.font(Font.PLAIN, 13));
        subtitle.setForeground(new Color(0xB9C7DA));

        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 2));
        texts.setOpaque(false);
        texts.add(title);
        texts.add(subtitle);

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        brand.add(new JLabel(new ImageIcon(createAppIcon(44))), BorderLayout.WEST);
        brand.add(texts, BorderLayout.CENTER);

        JPanel pillHolder = new JPanel(new GridBagLayout());
        pillHolder.setOpaque(false);
        pillHolder.add(statusPill);

        header.add(brand, BorderLayout.WEST);
        header.add(pillHolder, BorderLayout.EAST);
        return header;
    }

    private static Image createAppIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = quality(image.getGraphics());
        g2.setPaint(new GradientPaint(0, 0, Theme.RUNNING, size, size, Theme.PRIMARY));
        g2.fillRoundRect(0, 0, size, size, size / 3, size / 3);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, (int) (size * 0.58)));
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString("K", (size - metrics.stringWidth("K")) / 2,
                (size - metrics.getHeight()) / 2 + metrics.getAscent());
        g2.dispose();
        return image;
    }

    // ---------------------------------------------------------------- Pestaña: ejecución en vivo

    private JPanel buildExecutionPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(Theme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        panel.add(buildSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setOpaque(false);
        main.add(buildLiveMetricsPanel(), BorderLayout.NORTH);
        main.add(stageChart, BorderLayout.CENTER);
        main.add(buildLogCard(), BorderLayout.SOUTH);
        panel.add(main, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSidebar() {
        RoundedPanel card = new RoundedPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(330, 0));
        card.setTransferHandler(new ExcelDropHandler());

        fileNameLabel.setFont(Theme.font(Font.BOLD, 13));
        fileNameLabel.setForeground(Theme.TEXT);
        fileHintLabel.setFont(Theme.font(Font.PLAIN, 12));
        fileHintLabel.setForeground(Theme.MUTED);
        JPanel fileBox = new JPanel(new GridLayout(2, 1, 0, 2));
        fileBox.setBackground(new Color(0xF8FAFC));
        fileBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        fileBox.add(fileNameLabel);
        fileBox.add(fileHintLabel);

        browseButton.addActionListener(event -> chooseExcelFile());
        runButton.addActionListener(event -> startExecution());
        stopButton.addActionListener(event -> requestStop());
        stopButton.setEnabled(false);

        executionsSpinner.setFont(Theme.font(Font.PLAIN, 14));
        executionsSpinner.setPreferredSize(new Dimension(100, 34));
        executionsSpinner.setToolTipText("Cuántas veces se procesará el archivo completo");

        loadedRowsLabel.setFont(Theme.font(Font.BOLD, 13));
        loadedRowsLabel.setForeground(Theme.MUTED);
        planSummaryLabel.setFont(Theme.font(Font.PLAIN, 12));
        planSummaryLabel.setForeground(Theme.MUTED);
        JLabel spinnerHint = new JLabel("Veces que se procesará el archivo completo");
        spinnerHint.setFont(Theme.font(Font.PLAIN, 12));
        spinnerHint.setForeground(Theme.MUTED);

        int row = 0;
        addRow(card, sectionTitle("Configuración"), row++, 0);
        addRow(card, caption("Archivo de solicitudes"), row++, 18);
        addRow(card, fileBox, row++, 6);
        addRow(card, browseButton, row++, 8);
        addRow(card, caption("Número de ejecuciones"), row++, 18);
        addRow(card, executionsSpinner, row++, 6);
        addRow(card, spinnerHint, row++, 4);
        addRow(card, caption("Datos disponibles"), row++, 18);
        addRow(card, loadedRowsLabel, row++, 6);
        addRow(card, planSummaryLabel, row++, 2);
        addRow(card, runButton, row++, 24);
        addRow(card, stopButton, row++, 8);

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = row;
        filler.weighty = 1;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        card.add(spacer, filler);
        return card;
    }

    private static void addRow(JPanel panel, Component component, int row, int topGap) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(topGap, 0, 0, 0);
        panel.add(component, constraints);
    }

    private JPanel buildLiveMetricsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        executionStatusLabel.setFont(Theme.font(Font.BOLD, 17));
        executionStatusLabel.setForeground(Theme.TEXT);
        currentRequestLabel.setFont(Theme.font(Font.PLAIN, 13));
        currentRequestLabel.setForeground(Theme.MUTED);
        stageStatusLabel.setFont(Theme.font(Font.PLAIN, 12));
        stageStatusLabel.setForeground(Theme.MUTED);
        JPanel texts = new JPanel(new GridLayout(3, 1, 0, 3));
        texts.setOpaque(false);
        texts.add(executionStatusLabel);
        texts.add(currentRequestLabel);
        texts.add(stageStatusLabel);

        JLabel timerCaption = caption("Tiempo total");
        timerCaption.setHorizontalAlignment(SwingConstants.RIGHT);
        totalTimerLabel.setFont(Theme.font(Font.BOLD, 26));
        totalTimerLabel.setForeground(Theme.PRIMARY);
        totalTimerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel timerBox = new JPanel(new GridLayout(2, 1));
        timerBox.setOpaque(false);
        timerBox.add(timerCaption);
        timerBox.add(totalTimerLabel);

        RoundedPanel activity = new RoundedPanel(new BorderLayout(16, 0));
        activity.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        activity.add(texts, BorderLayout.CENTER);
        activity.add(timerBox, BorderLayout.EAST);
        panel.add(activity, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 6, 10, 0));
        cards.setOpaque(false);
        cards.add(createMetricCard("Etapas completadas", completedLabel, Theme.PRIMARY));
        cards.add(createMetricCard("Etapas superadas", passedLabel, Theme.SUCCESS));
        cards.add(createMetricCard("Fallos técnicos", failedLabel, Theme.DANGER));
        cards.add(createMetricCard("Órdenes bloqueadas", blockedLabel, Theme.WARNING));
        cards.add(createMetricCard("Etapas omitidas", skippedLabel, Theme.NEUTRAL));
        cards.add(createMetricCard("Duración de etapa", stageTimeLabel, Theme.VIOLET));
        panel.add(cards, BorderLayout.CENTER);

        progressBar.setStringPainted(true);
        progressBar.setString("Esperando ejecución");
        progressBar.setForeground(Theme.PRIMARY);
        progressBar.setFont(Theme.font(Font.BOLD, 11));
        progressBar.setPreferredSize(new Dimension(0, 20));
        panel.add(progressBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accent) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 4));
        card.setAccent(accent);
        card.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 10));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.font(Font.PLAIN, 12));
        titleLabel.setForeground(Theme.MUTED);
        valueLabel.setFont(Theme.font(Font.BOLD, 20));
        valueLabel.setForeground(Theme.TEXT);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildLogCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 14, 16));
        card.setPreferredSize(new Dimension(0, 190));

        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setBackground(new Color(0x101B29));
        logArea.setForeground(new Color(0xD2DCE6));
        logArea.setCaretColor(new Color(0xD2DCE6));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        card.add(sectionTitle("Actividad reciente"), BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ---------------------------------------------------------------- Pestaña: historial

    private JPanel buildStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(Theme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        styleTable(statisticsTable);
        styleTable(orderStatisticsTable);
        JTabbedPane summaryTabs = new JTabbedPane();
        summaryTabs.setFont(Theme.font(Font.BOLD, 12));
        summaryTabs.addTab("Resumen por ejecución", tableScroll(statisticsTable));
        summaryTabs.addTab("Detalle por solicitud", tableScroll(orderStatisticsTable));
        RoundedPanel tables = new RoundedPanel(new BorderLayout());
        tables.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tables.add(summaryTabs, BorderLayout.CENTER);
        panel.add(tables, BorderLayout.CENTER);

        JPanel charts = new JPanel(new GridLayout(1, 3, 12, 0));
        charts.setOpaque(false);
        charts.setPreferredSize(new Dimension(0, 270));
        charts.add(durationChart);
        charts.add(stageAverageChart);
        charts.add(actionAverageChart);
        panel.add(charts, BorderLayout.SOUTH);

        eventList.setFont(Theme.font(Font.PLAIN, 12));
        eventList.setForeground(Theme.TEXT);
        eventList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, false);
                label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return label;
            }
        });
        JScrollPane eventScroll = new JScrollPane(eventList);
        eventScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        RoundedPanel events = new RoundedPanel(new BorderLayout(0, 8));
        events.setBorder(BorderFactory.createEmptyBorder(12, 14, 14, 14));
        events.setPreferredSize(new Dimension(300, 0));
        events.add(sectionTitle("Eventos de la sesión"), BorderLayout.NORTH);
        events.add(eventScroll, BorderLayout.CENTER);
        panel.add(events, BorderLayout.EAST);
        return panel;
    }

    private static JScrollPane tableScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xEBEFF3));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFont(Theme.font(Font.PLAIN, 13));
        table.setSelectionBackground(new Color(0xDCEBFA));
        table.setSelectionForeground(Theme.TEXT);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new CellRenderer());
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 36));
        header.setDefaultRenderer(new HeaderRenderer());
    }

    // ================================================================ Selección de archivo

    private void chooseExcelFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar archivo de solicitudes");
        chooser.setFileFilter(new FileNameExtensionFilter("Libro de Excel (*.xlsx)", "xlsx"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadExcel(chooser.getSelectedFile());
        }
    }

    private void loadExcel(File file) {
        selectedFile = file;
        fileNameLabel.setText(file.getName());
        File parent = file.getParentFile();
        fileHintLabel.setText(parent == null ? file.getAbsolutePath() : parent.getAbsolutePath());
        fileHintLabel.setToolTipText(file.getAbsolutePath());
        fileNameLabel.setToolTipText(file.getAbsolutePath());
        updateLoadedRowCount();
        appendLog("Archivo seleccionado: " + file.getName());
        if (loadedRows > 0) {
            currentRequestLabel.setText("Archivo listo. Pulsa «Iniciar ejecución» para comenzar.");
        }
    }

    private void updateLoadedRowCount() {
        try {
            Object[][] data = ExcelUtils.getTestData(selectedFile.getAbsolutePath(), EXCEL_SHEET);
            loadedRows = data == null ? 0 : data.length;
            if (loadedRows > 0) {
                loadedRowsLabel.setText(loadedRows + (loadedRows == 1 ? " solicitud" : " solicitudes")
                        + " en la hoja «" + EXCEL_SHEET + "»");
                loadedRowsLabel.setForeground(Theme.SUCCESS);
            } else {
                loadedRowsLabel.setText("La hoja «" + EXCEL_SHEET + "» no tiene solicitudes");
                loadedRowsLabel.setForeground(Theme.DANGER);
            }
            loadedRowsLabel.setToolTipText(null);
        } catch (RuntimeException exception) {
            loadedRows = 0;
            loadedRowsLabel.setText("No se pudo leer el archivo");
            loadedRowsLabel.setForeground(Theme.DANGER);
            loadedRowsLabel.setToolTipText(exception.getMessage());
            appendLog("No se pudo leer el archivo: " + exception.getMessage());
        }
        updatePlanSummary();
    }

    private void updatePlanSummary() {
        if (loadedRows <= 0) {
            planSummaryLabel.setText(" ");
            return;
        }
        int executions = (Integer) executionsSpinner.getValue();
        planSummaryLabel.setText(loadedRows + (loadedRows == 1 ? " solicitud" : " solicitudes") + " × "
                + executions + (executions == 1 ? " ejecución" : " ejecuciones")
                + " = " + (loadedRows * executions) + " en total");
    }

    private final class ExcelDropHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return runButton.isEnabled() && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            try {
                List<?> files = (List<?>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                if (files.isEmpty()) {
                    return false;
                }
                File file = (File) files.get(0);
                if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
                    showWarning("Formato no compatible", "Solo se admiten libros de Excel (.xlsx).");
                    return false;
                }
                loadExcel(file);
                return true;
            } catch (UnsupportedFlavorException | IOException exception) {
                return false;
            }
        }
    }

    private void showWarning(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    // ================================================================ Ciclo de vida de la ejecución

    private void startExecution() {
        if (selectedFile == null) {
            showWarning("Archivo requerido", "Selecciona un archivo Excel antes de iniciar la ejecución.");
            return;
        }
        if (loadedRows <= 0) {
            showWarning("Sin solicitudes",
                    "El archivo seleccionado no tiene solicitudes en la hoja «" + EXCEL_SHEET + "».");
            return;
        }

        int executions = (Integer) executionsSpinner.getValue();
        try {
            resetLiveMetrics(executions);
        } catch (RuntimeException exception) {
            showWarning("No se pudo leer el archivo", String.valueOf(exception.getMessage()));
            return;
        }
        stopRequested = false;
        System.setProperty("kata.stop.requested", "false");
        runStarted = Instant.now();
        elapsedTimer.start();
        setControlsEnabled(false);
        statusPill.setState("EN EJECUCIÓN", Theme.RUNNING);
        appendLog("Iniciando " + executions + (executions == 1 ? " ejecución…" : " ejecuciones…"));

        executionThread = new Thread(() -> executeRuns(executions), "kata-mobile-executor");
        executionThread.start();
    }

    private void setControlsEnabled(boolean idle) {
        browseButton.setEnabled(idle);
        executionsSpinner.setEnabled(idle);
        runButton.setEnabled(idle);
        stopButton.setEnabled(!idle);
    }

    private void applyIdleState() {
        statusPill.setState("LISTO", new Color(0x5B6B7D));
        executionStatusLabel.setText("Listo para ejecutar");
        currentRequestLabel.setText("Selecciona un archivo Excel para comenzar");
        stageStatusLabel.setText("Sin etapa activa");
        totalTimerLabel.setText("00:00:00");
        stageTimeLabel.setText("00:00:00");
        progressBar.setValue(0);
        progressBar.setString("Esperando ejecución");
    }

    private void resetLiveMetrics(int executions) {
        Object[][] data = ExcelUtils.getTestData(selectedFile.getAbsolutePath(), EXCEL_SHEET);
        int requestsPerRun = data == null ? 0 : data.length;
        totalPlannedStages = requestsPerRun * executions * ExecutionTelemetry.TOTAL_STAGES;
        completedStages = 0;
        livePassedStages = 0;
        liveFailedStages = 0;
        liveBlockedStages = 0;
        liveSkippedStages = 0;
        currentRequestStarted = null;
        currentStageStarted = null;
        elapsedTimer.stop();
        progressBar.setMaximum(Math.max(1, totalPlannedStages));
        progressBar.setValue(0);
        progressBar.setString(totalPlannedStages == 0 ? "Sin etapas" : "0 / " + totalPlannedStages + " etapas");
        executionStatusLabel.setText("Preparando ejecución");
        currentRequestLabel.setText("Leyendo solicitudes del Excel");
        stageStatusLabel.setText("Etapa 0/" + ExecutionTelemetry.TOTAL_STAGES);
        totalTimerLabel.setText("00:00:00");
        stageTimeLabel.setText("00:00:00");
        completedLabel.setText("0 / " + totalPlannedStages);
        passedLabel.setText("0");
        failedLabel.setText("0");
        blockedLabel.setText("0");
        skippedLabel.setText("0");
        stageChart.clear();
    }

    private void requestStop() {
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas que deseas detener la ejecución?\nLa operación Appium en curso terminará de forma segura.",
                "Confirmar detención",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        stopRequested = true;
        System.setProperty("kata.stop.requested", "true");
        Thread worker = executionThread;
        if (worker != null) {
            worker.interrupt();
        }
        Thread driverCloser = new Thread(DriverManager::quitDriver, "kata-mobile-stop-driver");
        driverCloser.setDaemon(true);
        driverCloser.start();
        stopButton.setEnabled(false);
        statusPill.setState("DETENIENDO", Theme.WARNING);
        appendLog("Detención solicitada. Cerrando la sesión Appium para permitir una nueva ejecución.");
    }

    private void shutdownApplication() {
        Thread worker = executionThread;
        if (worker != null && worker.isAlive()) {
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Hay una ejecución activa. ¿Deseas cerrar el dashboard y detenerla?",
                    "Confirmar cierre",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }
            stopRequested = true;
            System.setProperty("kata.stop.requested", "true");
            worker.interrupt();
        }

        elapsedTimer.stop();
        try {
            DriverManager.quitDriver();
        } catch (RuntimeException exception) {
            ExecutionLog.error("No se pudo cerrar Appium limpiamente", exception);
        } finally {
            dispose();
            System.exit(0);
        }
    }

    private void executeRuns(int executions) {
        for (int runNumber = 1; runNumber <= executions && !stopRequested; runNumber++) {
            ExecutionListener listener = new ExecutionListener(runNumber);
            Instant started = Instant.now();
            ExecutionLog.start(runNumber);
            ExecutionLog.info("Archivo Excel: " + selectedFile.getAbsolutePath());
            try {
                System.setProperty("kata.excel.path", selectedFile.getAbsolutePath());
                TestNG testng = new TestNG();
                testng.setTestClasses(new Class<?>[] {FlujoCompletoTest.class});
                testng.addListener(listener);
                ExecutionTelemetry.setListener(listener);
                try {
                    testng.run();
                } finally {
                    ExecutionTelemetry.clearListener(listener);
                }
                Duration duration = Duration.between(started, Instant.now());
                String status = stopRequested
                        ? "DETENIDA"
                        : (testng.getStatus() == 0 ? "OK" : "CON ERROR");
                if (!stopRequested && listener.blockedOrders > 0 && "OK".equals(status)) {
                    status = "OK + BLOQUEADAS";
                }
                addStatistics(runNumber, listener, duration, status);
                ExecutionLog.finish(status);
            } catch (Exception exception) {
                Duration duration = Duration.between(started, Instant.now());
                String status = stopRequested ? "DETENIDA" : "ERROR DE LANZAMIENTO";
                addStatistics(runNumber, listener, duration, status);
                appendLog("La ejecución " + runNumber + " no pudo iniciar: " + exception.getMessage());
                ExecutionLog.error("La ejecución no pudo iniciar", exception);
                ExecutionLog.finish(status);
            }
        }
        executionThread = null;
        boolean stopped = stopRequested;
        SwingUtilities.invokeLater(() -> {
            setControlsEnabled(true);
            elapsedTimer.stop();
            currentRequestStarted = null;
            currentStageStarted = null;
            if (stopped) {
                statusPill.setState("DETENIDA", Theme.WARNING);
                executionStatusLabel.setText("Ejecución detenida");
            } else {
                statusPill.setState("FINALIZADA", Theme.SUCCESS);
                executionStatusLabel.setText("Ejecución finalizada");
            }
            currentRequestLabel.setText("Revisa el detalle en la pestaña «Historial y estadísticas».");
            stageStatusLabel.setText("Sin etapa activa");
        });
        appendLog(stopped ? "Ejecución detenida por el usuario." : "Ejecución finalizada.");
    }

    // ================================================================ Registro de resultados

    private void addStatistics(int runNumber, ExecutionListener listener, Duration duration, String status) {
        SwingUtilities.invokeLater(() -> {
            statisticsModel.addRow(new Object[] {
                runNumber, status, formatDuration(duration), listener.passed, listener.failed,
                listener.blockedOrders, listener.skipped
            });
            runDurations.add(duration.toMillis());
            runStatuses.add(status);
            refreshRunChart();
            ExecutionHistoryStore.save("RUN", String.valueOf(runNumber), duration.toMillis(), status);
        });
    }

    private void appendLog(String message) {
        String line = "[" + LocalTime.now().format(TIME_FORMAT) + "]  " + message;
        ExecutionLog.info(message);
        SwingUtilities.invokeLater(() -> {
            logArea.append(line + System.lineSeparator());
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void refreshElapsedTime() {
        if (runStarted != null) {
            totalTimerLabel.setText(formatDuration(Duration.between(runStarted, Instant.now())));
        }
        if (currentStageStarted != null) {
            stageTimeLabel.setText(formatDuration(Duration.between(currentStageStarted, Instant.now())));
        }
    }

    private void requestStarted(ITestResult result, int runNumber) {
        currentRequestStarted = Instant.now();
        SwingUtilities.invokeLater(() -> {
            executionStatusLabel.setText("Procesando solicitud");
            currentRequestLabel.setText("Ejecución " + runNumber + " · " + result.getName());
            stageStatusLabel.setText("Etapa 0/" + ExecutionTelemetry.TOTAL_STAGES + " · preparando");
            stageTimeLabel.setText("00:00:00");
            stageChart.clear();
            elapsedTimer.start();
        });
    }

    private void requestFinished(ITestResult result, String status, int runNumber) {
        Instant started = currentRequestStarted;
        long durationMillis = started == null ? 0 : Duration.between(started, Instant.now()).toMillis();
        String orderId = readOrderId(result);
        SwingUtilities.invokeLater(() -> {
            String duration = formatDuration(Duration.ofMillis(durationMillis));
            currentRequestLabel.setText("Última solicitud: " + duration);
            stageTimeLabel.setText(duration);
            executionStatusLabel.setText("Solicitud " + displayStatus(status).toLowerCase(Locale.ROOT));
            orderStatisticsModel.addRow(new Object[] {runNumber, orderId, status, duration});
            ExecutionHistoryStore.save("ORDER", orderId, durationMillis, status);
        });
    }

    private String readOrderId(ITestResult result) {
        Object[] parameters = result.getParameters();
        return parameters.length > 3 && parameters[3] != null
                ? String.valueOf(parameters[3]) : "Desconocida";
    }

    private void stageStarted(String stageName, int stageNumber, int totalStages) {
        currentStageStarted = Instant.now();
        appendLog("Etapa " + stageNumber + "/" + totalStages + " iniciada: " + stageName);
        SwingUtilities.invokeLater(() -> {
            executionStatusLabel.setText("Ejecutando etapa");
            stageStatusLabel.setText("Etapa " + stageNumber + "/" + totalStages + " · " + stageName);
            currentRequestLabel.setText("En curso: " + stageName);
            stageTimeLabel.setText("00:00:00");
            stageChart.startStage(stageNumber);
        });
    }

    private void stageFinished(String stageName, int stageNumber, ExecutionTelemetry.StageResult result,
            String detail, long durationMillis) {
        SwingUtilities.invokeLater(() -> {
            if (result == ExecutionTelemetry.StageResult.BLOCKED) {
                totalPlannedStages = Math.max(completedStages + 1,
                        totalPlannedStages - (ExecutionTelemetry.TOTAL_STAGES - stageNumber));
                progressBar.setMaximum(Math.max(1, totalPlannedStages));
            }
            completedStages++;
            switch (result) {
                case PASSED -> livePassedStages++;
                case BLOCKED -> liveBlockedStages++;
                case FAILED -> liveFailedStages++;
                default -> liveSkippedStages++;
            }
            completedLabel.setText(completedStages + " / " + totalPlannedStages);
            passedLabel.setText(String.valueOf(livePassedStages));
            failedLabel.setText(String.valueOf(liveFailedStages));
            blockedLabel.setText(String.valueOf(liveBlockedStages));
            skippedLabel.setText(String.valueOf(liveSkippedStages));
            progressBar.setValue(Math.min(completedStages, progressBar.getMaximum()));
            progressBar.setString(totalPlannedStages == 0
                    ? String.valueOf(completedStages)
                    : completedStages + " / " + totalPlannedStages + " etapas");
            stageChart.finishStage(stageNumber, durationMillis, result);

            ExecutionHistoryStore.save("STAGE", stageName, durationMillis, result.name());
            int index = stageNumber - 1;
            if (index >= 0 && index < stageTotals.length) {
                stageTotals[index] += durationMillis;
                stageCounts[index]++;
                refreshStageChart();
            }

            String duration = formatDuration(Duration.ofMillis(durationMillis));
            stageStatusLabel.setText("Etapa " + stageNumber + "/" + ExecutionTelemetry.TOTAL_STAGES
                    + " " + stageResultLabel(result) + " · " + stageName);
            currentRequestLabel.setText(detail == null || detail.isBlank() ? stageName : detail);
            stageTimeLabel.setText(duration);
            appendLog("Etapa " + stageNumber + "/" + ExecutionTelemetry.TOTAL_STAGES + " "
                    + stageResultLabel(result) + ": " + stageName + " (" + duration + ")");
        });
    }

    private static String stageResultLabel(ExecutionTelemetry.StageResult result) {
        return switch (result) {
            case PASSED -> "superada";
            case BLOCKED -> "bloqueada";
            case FAILED -> "fallida";
            default -> "omitida";
        };
    }

    // ================================================================ Historial persistente

    private void loadHistory() {
        try {
            for (ExecutionHistoryStore.Entry entry : ExecutionHistoryStore.readAll()) {
                switch (entry.type()) {
                    case "RUN" -> {
                        runDurations.add(entry.durationMillis());
                        runStatuses.add(entry.status());
                    }
                    case "STAGE" -> {
                        int stageNumber = stageNumberFromName(entry.key());
                        if (stageNumber > 0) {
                            stageTotals[stageNumber - 1] += entry.durationMillis();
                            stageCounts[stageNumber - 1]++;
                        }
                    }
                    case "ACTION" -> recordAction(entry.key(), entry.durationMillis());
                    default -> { }
                }
            }
        } catch (RuntimeException exception) {
            appendLog("No se pudo cargar el historial: " + exception.getMessage());
        }
        refreshRunChart();
        refreshStageChart();
        refreshActionChart();
    }

    private static int stageNumberFromName(String stageName) {
        if (stageName == null) return 0;
        if (stageName.startsWith("Consulta")) return 1;
        if (stageName.startsWith("Radicación")) return 2;
        if (stageName.startsWith("Análisis")) return 3;
        if (stageName.startsWith("Registro") || stageName.startsWith("Producto")) return 4;
        if (stageName.startsWith("Decisión")) return 5;
        return 0;
    }

    private void recordAction(String actionName, long durationMillis) {
        actionTotals.merge(actionName, durationMillis, Long::sum);
        actionCounts.merge(actionName, 1, Integer::sum);
    }

    private void refreshRunChart() {
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        List<Color> colors = new ArrayList<>();
        for (int i = Math.max(0, runDurations.size() - MAX_RUNS_IN_CHART); i < runDurations.size(); i++) {
            labels.add("#" + (i + 1));
            values.add(runDurations.get(i));
            colors.add(statusColor(runStatuses.get(i)));
        }
        durationChart.setData(labels, values, colors);
    }

    private void refreshStageChart() {
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        List<Color> colors = new ArrayList<>();
        boolean hasData = false;
        for (int i = 0; i < stageTotals.length; i++) {
            labels.add(stageLabel(i));
            long average = stageCounts[i] == 0 ? 0 : stageTotals[i] / stageCounts[i];
            hasData |= stageCounts[i] > 0;
            values.add(average);
            colors.add(Theme.VIOLET);
        }
        if (!hasData) {
            labels.clear();
            values.clear();
            colors.clear();
        }
        stageAverageChart.setData(labels, values, colors);
    }

    private void refreshActionChart() {
        List<Map.Entry<String, Long>> averages = new ArrayList<>();
        for (Map.Entry<String, Long> entry : actionTotals.entrySet()) {
            averages.add(Map.entry(entry.getKey(), entry.getValue() / actionCounts.get(entry.getKey())));
        }
        averages.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < Math.min(MAX_ACTIONS_IN_CHART, averages.size()); i++) {
            labels.add(averages.get(i).getKey());
            values.add(averages.get(i).getValue());
            colors.add(Theme.PRIMARY);
        }
        actionAverageChart.setData(labels, values, colors);
    }

    // ================================================================ Utilidades de formato

    private static String stageLabel(int index) {
        return index >= 0 && index < STAGE_LABELS.length ? STAGE_LABELS[index] : "Etapa " + (index + 1);
    }

    private static String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
    }

    private static String formatShort(long millis) {
        if (millis < 60_000) {
            return String.format(Locale.ROOT, "%.1fs", millis / 1000.0);
        }
        long seconds = millis / 1000;
        if (seconds < 3600) {
            return String.format(Locale.ROOT, "%dm %02ds", seconds / 60, seconds % 60);
        }
        return String.format(Locale.ROOT, "%dh %02dm", seconds / 3600, (seconds % 3600) / 60);
    }

    /** Traduce los códigos internos (que se conservan en el historial) a texto para el operador. */
    private static String displayStatus(String status) {
        if (status == null) {
            return "—";
        }
        return switch (status) {
            case "OK" -> "Exitosa";
            case "OK + BLOQUEADAS" -> "Exitosa con bloqueos";
            case "CON ERROR" -> "Con errores";
            case "ERROR DE LANZAMIENTO" -> "Error al iniciar";
            case "DETENIDA" -> "Detenida";
            case "FALLÓ" -> "Fallida";
            case "BLOQUEADA" -> "Bloqueada";
            case "OMITIDA" -> "Omitida";
            default -> status;
        };
    }

    private static Color statusColor(String status) {
        if (status == null) {
            return Theme.MUTED;
        }
        if ("OK".equals(status)) {
            return Theme.SUCCESS;
        }
        if (status.startsWith("OK") || "BLOQUEADA".equals(status)) {
            return Theme.WARNING;
        }
        if ("DETENIDA".equals(status) || "OMITIDA".equals(status)) {
            return Theme.NEUTRAL;
        }
        return Theme.DANGER;
    }

    private static String fit(FontMetrics metrics, String text, int maxWidth) {
        if (metrics.stringWidth(text) <= maxWidth) {
            return text;
        }
        int end = text.length();
        while (end > 1 && metrics.stringWidth(text.substring(0, end) + "…") > maxWidth) {
            end--;
        }
        return text.substring(0, end) + "…";
    }

    // ================================================================ Listener TestNG

    private final class ExecutionListener implements ITestListener, ExecutionTelemetry.Listener {
        private final int runNumber;
        private int passed;
        private int failed;
        private int blockedOrders;
        private int skipped;
        private boolean currentOrderBlocked;

        private ExecutionListener(int runNumber) {
            this.runNumber = runNumber;
        }

        @Override
        public void onTestSuccess(ITestResult result) {
            String status = currentOrderBlocked ? "BLOQUEADA" : "OK";
            if (currentOrderBlocked) {
                blockedOrders++;
            } else {
                passed++;
            }
            requestFinished(result, status, runNumber);
            recordEvent("Ejecución " + runNumber + ": solicitud " + displayStatus(status).toLowerCase(Locale.ROOT));
            currentOrderBlocked = false;
        }

        @Override
        public void onTestFailure(ITestResult result) {
            failed++;
            requestFinished(result, "FALLÓ", runNumber);
            ExecutionLog.error("Solicitud fallida: " + result.getName(), result.getThrowable());
            recordEvent("Ejecución " + runNumber + ": fallo en " + result.getName());
        }

        @Override
        public void onTestSkipped(ITestResult result) {
            skipped++;
            requestFinished(result, "OMITIDA", runNumber);
            recordEvent("Ejecución " + runNumber + ": solicitud omitida");
        }

        private void recordEvent(String event) {
            String line = LocalTime.now().format(TIME_FORMAT) + "  " + event;
            SwingUtilities.invokeLater(() -> eventModel.addElement(line));
        }

        @Override public void onStart(ITestContext context) { }
        @Override public void onFinish(ITestContext context) { }
        @Override public void onTestStart(ITestResult result) { requestStarted(result, runNumber); }
        @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) { }

        @Override
        public void onStageStarted(String stageName, int stageNumber, int totalStages) {
            stageStarted(stageName, stageNumber, totalStages);
        }

        @Override
        public void onStageFinished(String stageName, int stageNumber, ExecutionTelemetry.StageResult result,
                String detail, long durationMillis) {
            if (result == ExecutionTelemetry.StageResult.BLOCKED) {
                currentOrderBlocked = true;
            }
            if (result == ExecutionTelemetry.StageResult.FAILED) {
                ExecutionLog.error("Etapa fallida: " + stageName + " - " + detail);
            }
            stageFinished(stageName, stageNumber, result, detail, durationMillis);
        }

        @Override
        public void onActionFinished(String actionName, boolean successful, long durationMillis) {
            ExecutionHistoryStore.save("ACTION", actionName, durationMillis, successful ? "OK" : "ERROR");
            if (!successful) {
                ExecutionLog.error("Acción fallida: " + actionName);
            }
            SwingUtilities.invokeLater(() -> {
                recordAction(actionName, durationMillis);
                refreshActionChart();
            });
        }
    }

    // ================================================================ Componentes visuales

    /** Tarjeta blanca de esquinas redondeadas con borde sutil y barra de acento opcional. */
    private static class RoundedPanel extends JPanel {
        private Color accent;

        RoundedPanel() {
            this(new BorderLayout());
        }

        RoundedPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        void setAccent(Color accent) {
            this.accent = accent;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = quality(graphics);
            Shape shape = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 14, 14);
            g2.setColor(Theme.SURFACE);
            g2.fill(shape);
            if (accent != null) {
                g2.setClip(shape);
                g2.setColor(accent);
                g2.fillRect(0, 0, 5, getHeight());
                g2.setClip(null);
            }
            g2.setColor(Theme.BORDER);
            g2.draw(shape);
            g2.dispose();
        }
    }

    /** Indicador de estado con forma de píldora. */
    private static final class PillLabel extends JLabel {
        private Color fill = Theme.NEUTRAL;

        PillLabel(String text) {
            super(text, SwingConstants.CENTER);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(Theme.font(Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        }

        void setState(String text, Color fill) {
            setText(text);
            this.fill = fill;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = quality(graphics);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    /** Botón propio: se ve igual con FlatLaf, Nimbus o cualquier otro aspecto. */
    private static final class ModernButton extends JButton {
        enum Kind { PRIMARY, DANGER, SECONDARY }

        private final Kind kind;
        private boolean hover;
        private boolean pressed;

        ModernButton(String text, Kind kind) {
            super(text);
            this.kind = kind;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(Theme.font(Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100, 42));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent event) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent event) { hover = false; pressed = false; repaint(); }
                @Override public void mousePressed(MouseEvent event) { pressed = true; repaint(); }
                @Override public void mouseReleased(MouseEvent event) { pressed = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = quality(graphics);
            Color fill;
            Color border = null;
            Color text;
            if (!isEnabled()) {
                fill = new Color(0xE9EDF2);
                text = new Color(0x9AA5B1);
            } else if (kind == Kind.SECONDARY) {
                fill = pressed ? new Color(0xD8E8F8) : hover ? new Color(0xEAF2FB) : Color.WHITE;
                border = Theme.PRIMARY;
                text = Theme.PRIMARY;
            } else {
                Color base = kind == Kind.PRIMARY ? Theme.PRIMARY : Theme.DANGER;
                fill = pressed ? base.darker().darker() : hover ? base.darker() : base;
                text = Color.WHITE;
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            if (border != null) {
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            g2.setFont(getFont());
            FontMetrics metrics = g2.getFontMetrics();
            g2.setColor(text);
            g2.drawString(getText(), (getWidth() - metrics.stringWidth(getText())) / 2,
                    (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent());
            g2.dispose();
        }
    }

    private static final class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setOpaque(true);
            setBackground(new Color(0xEEF2F6));
            setForeground(Theme.MUTED);
            setFont(Theme.font(Font.BOLD, 12));
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)));
            return this;
        }
    }

    private static final class CellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            boolean statusColumn = "Estado".equals(table.getColumnName(column));
            String raw = String.valueOf(value);
            super.getTableCellRendererComponent(table, statusColumn ? displayStatus(raw) : value,
                    isSelected, false, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            if (!isSelected) {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF8FAFC));
            }
            setForeground(statusColumn ? statusColor(raw) : Theme.TEXT);
            setFont(Theme.font(statusColumn ? Font.BOLD : Font.PLAIN, 13));
            return this;
        }
    }

    /** Pipeline de las etapas de la solicitud actual: estado de cada etapa y su duración. */
    private static final class LiveStagePanel extends RoundedPanel {
        private enum State { PENDING, RUNNING, PASSED, BLOCKED, FAILED, SKIPPED }

        private final long[] durations = new long[ExecutionTelemetry.TOTAL_STAGES];
        private final State[] states = new State[ExecutionTelemetry.TOTAL_STAGES];
        private String caption = "";

        private LiveStagePanel() {
            setPreferredSize(new Dimension(0, 270));
            clear();
        }

        private void startStage(int stageNumber) {
            int index = stageNumber - 1;
            if (index >= 0 && index < states.length) {
                states[index] = State.RUNNING;
                durations[index] = 0;
                caption = stageLabel(index) + " en curso";
            }
            repaint();
        }

        private void finishStage(int stageNumber, long durationMillis, ExecutionTelemetry.StageResult result) {
            int index = stageNumber - 1;
            if (index >= 0 && index < states.length) {
                durations[index] = durationMillis;
                states[index] = switch (result) {
                    case PASSED -> State.PASSED;
                    case BLOCKED -> State.BLOCKED;
                    case FAILED -> State.FAILED;
                    default -> State.SKIPPED;
                };
                caption = stageLabel(index) + " " + stageResultLabel(result);
            }
            repaint();
        }

        private void clear() {
            Arrays.fill(durations, 0);
            Arrays.fill(states, State.PENDING);
            caption = "";
            repaint();
        }

        private static boolean isFinished(State state) {
            return state != State.PENDING && state != State.RUNNING;
        }

        private static Color stateColor(State state) {
            return switch (state) {
                case PASSED -> Theme.SUCCESS;
                case BLOCKED -> Theme.WARNING;
                case FAILED -> Theme.DANGER;
                case SKIPPED -> Theme.NEUTRAL;
                case RUNNING -> Theme.RUNNING;
                default -> Theme.PENDING;
            };
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = quality(graphics);

            g2.setFont(Theme.font(Font.BOLD, 14));
            g2.setColor(Theme.TEXT);
            g2.drawString("Avance por etapa de la solicitud actual", 20, 30);
            if (!caption.isEmpty()) {
                g2.setFont(Theme.font(Font.PLAIN, 12));
                g2.setColor(Theme.MUTED);
                int width = g2.getFontMetrics().stringWidth(caption);
                g2.drawString(caption, getWidth() - 20 - width, 30);
            }

            int slots = states.length;
            int left = 24;
            int right = getWidth() - 24;
            int slotWidth = Math.max(1, (right - left) / slots);
            int circle = 36;
            int circleTop = 52;
            int centerY = circleTop + circle / 2;

            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < slots - 1; i++) {
                int x1 = left + i * slotWidth + slotWidth / 2 + circle / 2 + 6;
                int x2 = left + (i + 1) * slotWidth + slotWidth / 2 - circle / 2 - 6;
                g2.setColor(isFinished(states[i]) ? stateColor(states[i]) : Theme.GRID);
                g2.drawLine(x1, centerY, x2, centerY);
            }

            for (int i = 0; i < slots; i++) {
                int centerX = left + i * slotWidth + slotWidth / 2;
                State state = states[i];
                if (state == State.RUNNING) {
                    g2.setColor(withAlpha(Theme.RUNNING, 55));
                    g2.fillOval(centerX - circle / 2 - 5, centerY - circle / 2 - 5, circle + 10, circle + 10);
                }
                g2.setColor(state == State.PENDING ? new Color(0xF1F4F8) : stateColor(state));
                g2.fillOval(centerX - circle / 2, centerY - circle / 2, circle, circle);
                if (state == State.PENDING) {
                    g2.setColor(Theme.PENDING);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(centerX - circle / 2, centerY - circle / 2, circle, circle);
                }
                drawGlyph(g2, state, centerX, centerY, i + 1);

                g2.setFont(Theme.font(Font.BOLD, 13));
                g2.setColor(state == State.PENDING ? Theme.MUTED : Theme.TEXT);
                String name = stageLabel(i);
                FontMetrics metrics = g2.getFontMetrics();
                g2.drawString(name, centerX - metrics.stringWidth(name) / 2, circleTop + circle + 22);
            }

            int barsTop = circleTop + circle + 44;
            int barsBottom = getHeight() - 22;
            if (barsBottom - barsTop > 40) {
                long maximum = 1;
                for (int i = 0; i < slots; i++) {
                    if (isFinished(states[i])) {
                        maximum = Math.max(maximum, durations[i]);
                    }
                }
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(Theme.GRID);
                g2.drawLine(left, barsBottom, right, barsBottom);
                for (int i = 0; i < slots; i++) {
                    int centerX = left + i * slotWidth + slotWidth / 2;
                    int barWidth = Math.max(20, Math.min(64, slotWidth - 40));
                    boolean finished = isFinished(states[i]);
                    int barHeight = finished
                            ? Math.max(4, (int) ((barsBottom - barsTop - 20) * (durations[i] / (double) maximum)))
                            : 4;
                    g2.setColor(finished ? stateColor(states[i])
                            : states[i] == State.RUNNING ? withAlpha(Theme.RUNNING, 120) : Theme.GRID);
                    g2.fillRoundRect(centerX - barWidth / 2, barsBottom - barHeight, barWidth, barHeight, 8, 8);

                    String text = finished ? formatShort(durations[i])
                            : states[i] == State.RUNNING ? "En curso" : "Pendiente";
                    g2.setFont(Theme.font(Font.PLAIN, 12));
                    g2.setColor(finished ? Theme.TEXT : Theme.MUTED);
                    g2.drawString(text, centerX - g2.getFontMetrics().stringWidth(text) / 2,
                            barsBottom - barHeight - 7);
                }
            }
            g2.dispose();
        }

        private void drawGlyph(Graphics2D g2, State state, int cx, int cy, int number) {
            g2.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(Color.WHITE);
            switch (state) {
                case PASSED -> g2.drawPolyline(new int[] {cx - 7, cx - 2, cx + 8}, new int[] {cy, cy + 6, cy - 6}, 3);
                case FAILED -> {
                    g2.drawLine(cx - 6, cy - 6, cx + 6, cy + 6);
                    g2.drawLine(cx - 6, cy + 6, cx + 6, cy - 6);
                }
                case BLOCKED -> {
                    g2.drawLine(cx, cy - 8, cx, cy + 2);
                    g2.fillOval(cx - 2, cy + 6, 4, 4);
                }
                case SKIPPED -> g2.drawLine(cx - 6, cy, cx + 6, cy);
                default -> {
                    g2.setColor(state == State.RUNNING ? Color.WHITE : Theme.MUTED);
                    g2.setFont(Theme.font(Font.BOLD, 14));
                    FontMetrics metrics = g2.getFontMetrics();
                    String text = String.valueOf(number);
                    g2.drawString(text, cx - metrics.stringWidth(text) / 2,
                            cy - metrics.getHeight() / 2 + metrics.getAscent());
                }
            }
        }
    }

    /** Gráfico de barras genérico para el historial: eje con referencias, etiquetas ajustadas y valores. */
    private static final class BarChartPanel extends RoundedPanel {
        private final String title;
        private final String emptyMessage;
        private List<String> labels = new ArrayList<>();
        private List<Long> values = new ArrayList<>();
        private List<Color> colors = new ArrayList<>();

        private BarChartPanel(String title, String emptyMessage) {
            this.title = title;
            this.emptyMessage = emptyMessage;
        }

        private void setData(List<String> labels, List<Long> values, List<Color> colors) {
            this.labels = new ArrayList<>(labels);
            this.values = new ArrayList<>(values);
            this.colors = new ArrayList<>(colors);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = quality(graphics);
            g2.setFont(Theme.font(Font.BOLD, 13));
            g2.setColor(Theme.TEXT);
            g2.drawString(fit(g2.getFontMetrics(), title, getWidth() - 36), 18, 28);

            if (values.isEmpty()) {
                g2.setFont(Theme.font(Font.PLAIN, 12));
                g2.setColor(Theme.MUTED);
                g2.drawString(fit(g2.getFontMetrics(), emptyMessage, getWidth() - 36), 18, 62);
                g2.dispose();
                return;
            }

            int left = 52;
            int right = getWidth() - 18;
            int top = 54;
            int bottom = getHeight() - 36;
            if (bottom - top < 30 || right - left < 30) {
                g2.dispose();
                return;
            }
            long maximum = 1;
            for (long value : values) {
                maximum = Math.max(maximum, value);
            }

            g2.setFont(Theme.font(Font.PLAIN, 11));
            FontMetrics metrics = g2.getFontMetrics();
            for (int i = 0; i <= 3; i++) {
                int y = bottom - (bottom - top) * i / 3;
                g2.setColor(Theme.GRID);
                g2.drawLine(left, y, right, y);
                String tick = formatShort(maximum * i / 3);
                g2.setColor(Theme.MUTED);
                g2.drawString(tick, left - 8 - metrics.stringWidth(tick), y + 4);
            }

            int count = values.size();
            int slot = Math.max(1, (right - left) / count);
            int barWidth = Math.max(6, Math.min(56, slot - 12));
            int labelStride = Math.max(1, (int) Math.ceil(44.0 / slot));
            for (int i = 0; i < count; i++) {
                int centerX = left + i * slot + slot / 2;
                int barHeight = Math.max(3, (int) ((bottom - top) * (values.get(i) / (double) maximum)));
                int y = bottom - barHeight;
                g2.setColor(colors.get(i));
                g2.fillRoundRect(centerX - barWidth / 2, y, barWidth, barHeight, 6, 6);

                if (slot >= 46) {
                    String valueText = values.get(i) == 0 ? "—" : formatShort(values.get(i));
                    g2.setColor(Theme.TEXT);
                    g2.drawString(valueText, centerX - metrics.stringWidth(valueText) / 2, y - 5);
                }
                if (i % labelStride == 0) {
                    String label = fit(metrics, labels.get(i), slot * labelStride - 4);
                    g2.setColor(Theme.MUTED);
                    g2.drawString(label, centerX - metrics.stringWidth(label) / 2, bottom + 18);
                }
            }
            g2.dispose();
        }
    }
}