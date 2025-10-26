package main;

import OS_Structures.OS_Process;
import OS_Structures.OperatingSystem;
import OS_Structures.Schedule;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JViewport;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTextArea;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author vince
 */
public class GUI extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GUI.class.getName());
    private OperatingSystem os;
    private JTextArea metricsTextArea;

    private Map<Schedule, XYSeries> throughputSeriesMap;
    private Map<Schedule, XYSeries> cpuUtilizationSeriesMap;
    private Map<Schedule, XYSeries> avgWaitTimeSeriesMap;
    private Map<Schedule, XYSeries> fairnessSeriesMap;

    private Map<Schedule, Long> totalBusyCyclesMap;
    private Map<Schedule, Double> totalWaitingTimeMap;
    private Map<Schedule, Long> totalFinishedProcsMap;
    private Map<Schedule, List<Double>> finishedWaitTimesMap;
    
    private int graphedFinishedProcessCount = 0;
    
    private volatile OS_Process runningProcessForChart = null;

    public GUI() {
        initComponents();
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        quantumLabel.setVisible(false);
        quantumField.setVisible(false);
        ioBoundCheckBox.setSelected(false);
        cyclesToCallField.setEnabled(false);
        cyclesToCompleteField.setEnabled(false);
        cyclesToCallLabel.setEnabled(false);
        cyclesToCompleteLabel.setEnabled(false);
        cyclesToCallField.setText("0");
        cyclesToCompleteField.setText("0");

        MousePanListener verticalPan1 = new MousePanListener(newProcessScrollPane);
        newProcessListPanel.addMouseListener(verticalPan1);
        newProcessListPanel.addMouseMotionListener(verticalPan1);
        
        MousePanListener verticalPan2 = new MousePanListener(readyScrollPane);
        readyListPanel.addMouseListener(verticalPan2);
        readyListPanel.addMouseMotionListener(verticalPan2);
        
        MousePanListener verticalPan3 = new MousePanListener(blockedScrollPane);
        blockedListPanel.addMouseListener(verticalPan3);
        blockedListPanel.addMouseMotionListener(verticalPan3);
        
        MousePanListener verticalPan4 = new MousePanListener(readySuspendedScrollPane);
        readySuspendedListPanel.addMouseListener(verticalPan4);
        readySuspendedListPanel.addMouseMotionListener(verticalPan4);
        
        MousePanListener verticalPan5 = new MousePanListener(blockedSuspendedScrollPane);
        blockedSuspendedListPanel.addMouseListener(verticalPan5);
        blockedSuspendedListPanel.addMouseMotionListener(verticalPan5);
        
        MousePanListener horizontalPan = new MousePanListener(finishedScrollPane);
        finishedListPanel.addMouseListener(horizontalPan);
        finishedListPanel.addMouseMotionListener(horizontalPan);
        
        initializeCharts();

    }
    
    private void initializeCharts() {
        graphicsPanel.setLayout(new java.awt.BorderLayout());
        JTabbedPane scheduleTabbedPane = new JTabbedPane();
        graphicsPanel.add(scheduleTabbedPane, java.awt.BorderLayout.CENTER);

        throughputSeriesMap = new HashMap<>();
        cpuUtilizationSeriesMap = new HashMap<>();
        avgWaitTimeSeriesMap = new HashMap<>();
        fairnessSeriesMap = new HashMap<>();
        finishedWaitTimesMap = new HashMap<>();

        totalBusyCyclesMap = new HashMap<>();
        totalWaitingTimeMap = new HashMap<>();
        totalFinishedProcsMap = new HashMap<>();

        for (Schedule alg : Schedule.values()) {
            
            totalBusyCyclesMap.put(alg, 0L);
            totalWaitingTimeMap.put(alg, 0.0);
            totalFinishedProcsMap.put(alg, 0L);
            finishedWaitTimesMap.put(alg, new ArrayList<>());

            JPanel algTabPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            
            XYSeries tputSeries = new XYSeries("Throughput");
            throughputSeriesMap.put(alg, tputSeries);
            XYSeriesCollection tputDataset = new XYSeriesCollection(tputSeries);
            JFreeChart tputChart = ChartFactory.createXYLineChart(
                    "Throughput (Procesos Terminados)", "Ciclo del OS", "Total Terminados",
                    tputDataset, PlotOrientation.VERTICAL, true, true, false);
            algTabPanel.add(new ChartPanel(tputChart));

            XYSeries cpuSeries = new XYSeries("Utilización CPU");
            cpuUtilizationSeriesMap.put(alg, cpuSeries);
            XYSeriesCollection cpuDataset = new XYSeriesCollection(cpuSeries);
            JFreeChart cpuChart = ChartFactory.createXYLineChart(
                    "Utilización de CPU (%)", "Ciclo del OS", "% Utilización",
                    cpuDataset, PlotOrientation.VERTICAL, true, true, false);
            algTabPanel.add(new ChartPanel(cpuChart));

            XYSeries waitSeries = new XYSeries("T. Espera Prom.");
            avgWaitTimeSeriesMap.put(alg, waitSeries);
            XYSeriesCollection waitDataset = new XYSeriesCollection(waitSeries);
            JFreeChart waitChart = ChartFactory.createXYLineChart(
                    "Tiempo de Espera Promedio", "Nro. Proceso Terminado", "Promedio (ms)",
                    waitDataset, PlotOrientation.VERTICAL, true, true, false);
            algTabPanel.add(new ChartPanel(waitChart));

            XYSeries fairnessSeries = new XYSeries("Equidad (Desv. Est.)");
            fairnessSeriesMap.put(alg, fairnessSeries);
            XYSeriesCollection fairnessDataset = new XYSeriesCollection(fairnessSeries);
            JFreeChart fairnessChart = ChartFactory.createXYLineChart(
                    "Equidad (Desv. Est. T. Espera)", "Nro. Proceso Terminado", "Desv. Estándar (ms)",
                    fairnessDataset, PlotOrientation.VERTICAL, true, true, false);
            algTabPanel.add(new ChartPanel(fairnessChart));


            scheduleTabbedPane.addTab(alg.name(), algTabPanel);
        }
    }
    
    private void limitSeriesData(XYSeries series, int maxPoints) {
        if (series != null && series.getItemCount() > maxPoints) {
            series.remove(0);
        }
    }

    public void updateChartData(long cycleCount) {
        if (os == null) return;
        
        final Schedule alg = os.getScheduleType();
        
        if (alg == null) return;

        boolean cpuBusy = (this.runningProcessForChart != null);
        long busyCycles = 0;
        
        if (cpuBusy) {
            busyCycles = totalBusyCyclesMap.compute(alg, (k, v) -> (v == null ? 0 : v) + 1);
        } else {
            busyCycles = totalBusyCyclesMap.getOrDefault(alg, 0L);
        }
        
        final double utilization = (cycleCount == 0) ? 0 : ((double)busyCycles / cycleCount) * 100.0;
        
        SwingUtilities.invokeLater(() -> {
            try {
                XYSeries cpuSeries = cpuUtilizationSeriesMap.get(alg);

                if (cpuSeries != null) {
                    cpuSeries.add(cycleCount, utilization);
                    limitSeriesData(cpuSeries, 500);
                }

            } catch (Exception e) {
                logger.log(java.util.logging.Level.WARNING, "Error al actualizar datos de gráficos", e);
            }
        });
    }

    public void setOperatingSystem(OperatingSystem os) {
        this.os = os;
    }

    public void updateCycleCount(long count) {
        updateChartData(count);
        
        SwingUtilities.invokeLater(() -> {
            cycleLabel.setText("Ciclos hasta el momento: " + count);
        });
    }
    
    public void updateCpuStatus() {
        if (os == null || cpuStatusLabel == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                boolean kernelMode = os.isInKernel();
                
                if (kernelMode) {
                    cpuStatusLabel.setText("KERNEL MODE");

                } else {
                    OS_Process p = os.getRunningProcess();
                    String pid = (p != null) ? " (PID: " + p.getId() + ")" : "";
                    cpuStatusLabel.setText("USER MODE" + pid);
  
        
                }
            } catch (Exception e) {
                logger.log(java.util.logging.Level.WARNING, "Error al actualizar estado de CPU", e);
            }
        });
    }

    public void addLogMessage(String message) {
        long currentCycle = OperatingSystem.cycleCounter;
        String formattedMessage = "[Ciclo " + currentCycle + "] " + message + "\n";
        
        SwingUtilities.invokeLater(() -> {
            logTextArea.append(formattedMessage);
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }


    public void setInitialDuration(long duration, String unit) {
        SwingUtilities.invokeLater(() -> {
            durationField.setText(String.valueOf(duration));
            unitComboBox.setSelectedItem(unit);

            long durationInMs = "Segundos".equals(unit) ? duration * 1000 : duration;
            currentDurationValueLabel.setText(durationInMs + " ms");
        });
    }
    

    public void setInitialQuantum(long quantum) {
        SwingUtilities.invokeLater(() -> {
            quantumField.setText(String.valueOf(quantum));
        });
    }

    public void setInitialSchedule(String schedule) {
        SwingUtilities.invokeLater(() -> {
            scheduleComboBox.setSelectedItem(schedule);
            scheduleComboBoxActionPerformed(null); 
            
            String labelText = schedule;
            if ("RR".equalsIgnoreCase(schedule) || "FeedBack".equalsIgnoreCase(schedule)) {
                labelText += " (Q=" + quantumField.getText() + ")";
            }
            currentScheduleValueLabel.setText(labelText); 
        });
    }

    public void addNewProcessToView(OS_Process process) {
        SwingUtilities.invokeLater(() -> {
            createProcessCard(process, newProcessListPanel);
            newProcessListPanel.revalidate();
            newProcessListPanel.repaint();
        });
    }

    private void createProcessCard(OS_Structures.OS_Process process, JPanel parentPanel) {
        JPanel processCard = new JPanel();
        processCard.setLayout(new BoxLayout(processCard, BoxLayout.Y_AXIS));
        processCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        if (process == null) return;
        
        OS_Structures.Status status = process.getState();
        if (status != null) {
            switch (status) {
                case RUNNING:
                    processCard.setBackground(new Color(173, 216, 230));
                    break;
                case BLOCKED:
                    processCard.setBackground(new Color(255, 182, 193));
                    break;
                case READY:
                    processCard.setBackground(new Color(144, 238, 144));
                    break;
                default:
                    processCard.setBackground(Color.WHITE);
                    break;
            }
        } else {
             processCard.setBackground(Color.GRAY);
        }

        processCard.add(new JLabel(" ID: " + process.getId() + " - " + process.getName()));
        processCard.add(new JLabel(" Prio: " + process.getPriority() + " | Pila: " + process.getPile()));
        processCard.add(new JLabel(" PC: " + process.getMAR() + " / " + process.getPile()));
        processCard.add(new JLabel(" Tipo: " + (process.isIOBound() ? "I/O Bound" : "CPU Bound")));
        parentPanel.add(processCard);
        parentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    public void updateRunningProcess(OS_Structures.OS_Process process) {
        this.runningProcessForChart = process;
        
        SwingUtilities.invokeLater(() -> {
            runningProcessPanel.removeAll();

            if (process != null) {
                createProcessCard(process, runningProcessPanel);
            }

            runningProcessPanel.revalidate();
            runningProcessPanel.repaint();
        });
    }
    
    public void refreshAllQueues() {
        SwingUtilities.invokeLater(() -> {
            try {
                refreshNewList();
                refreshReadyList();
                refreshReadySuspendedList();
                refreshBlockedList();
                refreshBlockedSuspendedList();
                refreshFinishedList();
                updateMetricsDisplay();
                
                if (os != null) {
                    memoryFreeLabel.setText(os.getMemoryFree() + " KB");
                }

            } catch (Exception e) {
                logger.log(java.util.logging.Level.SEVERE, "Error al refrescar las colas de la GUI", e);
            }
        });
    }
    
    private void updateMetricsDisplay() {
        if (os == null || metricsTextArea == null) return;

        Schedule alg = os.getScheduleType();
        long cycleCount = OperatingSystem.cycleCounter;
        long finishedCount = totalFinishedProcsMap.getOrDefault(alg, 0L);
        long busyCycles = totalBusyCyclesMap.getOrDefault(alg, 0L);
        double totalWait = totalWaitingTimeMap.getOrDefault(alg, 0.0);
        
        double utilization = (cycleCount == 0) ? 0 : ((double)busyCycles / cycleCount) * 100.0;
        double avgWait = (finishedCount == 0) ? 0 : (totalWait / finishedCount);
        double throughput = (cycleCount == 0) ? 0 : ((double)finishedCount / cycleCount);

        List<Double> waitTimes = finishedWaitTimesMap.get(alg);
        double stdDev = (waitTimes != null) ? calculateStandardDeviation(waitTimes) : 0.0;
        
        StringBuilder sb = new StringBuilder();
        sb.append("--- MÉTRICAS DE RENDIMIENTO ---\n\n");
        sb.append(String.format("Algoritmo Actual: %s\n", alg.toString()));
        sb.append(String.format("Ciclo del Sistema: %d\n", cycleCount));
        sb.append("--------------------------------\n");
        sb.append(String.format("Procesos Terminados: %d\n", finishedCount));
        sb.append(String.format("Throughput: %.4f proc/ciclo\n", throughput));
        sb.append("--------------------------------\n");
        sb.append(String.format("Ciclos CPU Ocupada: %d\n", busyCycles));
        sb.append(String.format("Utilización de CPU: %.2f %%\n", utilization));
        sb.append("--------------------------------\n");
        sb.append(String.format("T. Espera Total: %.2f ciclos\n", totalWait));
        sb.append(String.format("T. Espera Promedio: %.2f ciclos\n", avgWait));
        sb.append(String.format("Equidad (Desv. Est.): %.2f\n", stdDev));
        
        metricsTextArea.setText(sb.toString());
    }

    private void refreshNewList() throws InterruptedException {
        if (os == null || os.getNewSem() == null) return;
        
        os.getNewSem().acquire();
        try {
            newProcessListPanel.removeAll();
            
            for (OS_Structures.OS_Process p : os.getNewProcesses()) {
                 if (p != null) createProcessCard(p, newProcessListPanel);
            }
            
            newProcessListPanel.revalidate();
            newProcessListPanel.repaint();
            
        } finally {
            os.getNewSem().release();
        }
    }

    private void refreshReadyList() throws InterruptedException {
        if (os == null || os.getReadySem() == null) return;
        
        os.getReadySem().acquire();
        try {
            readyListPanel.removeAll();
            
            for (Object nodeObj : os.getReadyProcesses()) {
                 if (nodeObj == null) continue;
                OS_Structures.ProcessNode node = (OS_Structures.ProcessNode) nodeObj;
                 if (node.getElement() != null) createProcessCard(node.getElement(), readyListPanel);
            }
            
            readyListPanel.revalidate();
            readyListPanel.repaint();
            
        } finally {
            os.getReadySem().release();
        }
    }

    private void refreshReadySuspendedList() throws InterruptedException {
        if (os == null || os.getReadySem() == null) return;
        
        os.getReadySem().acquire();
        try {
            readySuspendedListPanel.removeAll();
            
            for (Object nodeObj : os.getReadySuspendedProcesses()) {
                 if (nodeObj == null) continue;
                OS_Structures.ProcessNode node = (OS_Structures.ProcessNode) nodeObj;
                 if (node.getElement() != null) createProcessCard(node.getElement(), readySuspendedListPanel);
            }
            
            readySuspendedListPanel.revalidate();
            readySuspendedListPanel.repaint();
            
        } finally {
            os.getReadySem().release();
        }
    }

    private void refreshBlockedList() throws InterruptedException {
        if (os == null || os.getBlockedSem() == null) return;
        
        os.getBlockedSem().acquire();
        try {
            blockedListPanel.removeAll();
            
            Structures.List<Integer> keys = os.getBlockedProcesses().getKeys();
             if (keys == null) return;
            for (Integer key : keys) {
                 if (key == null) continue;
                OS_Structures.OS_Process p = os.getBlockedProcesses().getValueOfKey(key);
                 if (p != null) createProcessCard(p, blockedListPanel);
            }
            
            blockedListPanel.revalidate();
            blockedListPanel.repaint();
            
        } finally {
            os.getBlockedSem().release();
        }
    }

    private void refreshBlockedSuspendedList() throws InterruptedException {
        if (os == null || os.getBlockedSem() == null) return;
        
        os.getBlockedSem().acquire();
        try {
            blockedSuspendedListPanel.removeAll();
            
            Structures.List<Integer> keys = os.getBlockedSuspendedProcesses().getKeys();
            if (keys == null) return;
            for (Integer key : keys) {
                 if (key == null) continue;
                OS_Structures.OS_Process p = os.getBlockedSuspendedProcesses().getValueOfKey(key);
                 if (p != null) createProcessCard(p, blockedSuspendedListPanel);
            }
            
            blockedSuspendedListPanel.revalidate();
            blockedSuspendedListPanel.repaint();
            
        } finally {
            os.getBlockedSem().release();
        }
    }

    private void refreshFinishedList() {
        if (os == null) return;
        
        final Schedule alg = os.getScheduleType();
        if (alg == null) return;
        
        finishedListPanel.removeAll();
        int currentProcessIndex = 0;

        Iterable<OS_Structures.OS_Process> finishedProcesses = os.getExitProcesses();
         if (finishedProcesses == null) return;

        for (OS_Structures.OS_Process p : finishedProcesses) {
             if (p == null) continue;
            
            createProcessCard(p, finishedListPanel);
            
            if (currentProcessIndex >= graphedFinishedProcessCount) {
                final double waitTime = (p.getTotalTime() - p.getMAR());
                final long cycle = OperatingSystem.cycleCounter;
                final long finishedCount = totalFinishedProcsMap.compute(alg, (k, v) -> (v == null ? 0 : v) + 1);
                final double totalWait = totalWaitingTimeMap.compute(alg, (k, v) -> (v == null ? 0 : v) + waitTime);
                final double avgWait = (finishedCount == 0) ? 0 : (totalWait / finishedCount);
                
                List<Double> waitTimesList = finishedWaitTimesMap.get(alg);
                if (waitTimesList != null) {
                    waitTimesList.add(waitTime);
                    final double stdDevWaitTime = calculateStandardDeviation(waitTimesList);
                    
                    XYSeries fairnessSeries = fairnessSeriesMap.get(alg);
                    if (fairnessSeries != null) {
                        fairnessSeries.add(finishedCount, stdDevWaitTime);
                        limitSeriesData(fairnessSeries, 500);
                    }
                }
                
                XYSeries waitSeries = avgWaitTimeSeriesMap.get(alg);
                XYSeries tputSeries = throughputSeriesMap.get(alg);
                
                if (waitSeries != null) {
                    waitSeries.add(finishedCount, avgWait);
                    limitSeriesData(waitSeries, 500);
                }
                if (tputSeries != null) {
                    tputSeries.add(cycle, finishedCount);
                    limitSeriesData(tputSeries, 500);
                }
            }
            
            currentProcessIndex++;
        }
        
        graphedFinishedProcessCount = currentProcessIndex;
        
        finishedListPanel.revalidate();
        finishedListPanel.repaint();
    }
    
    private double calculateStandardDeviation(List<Double> data) {
        int n = data.size();
        if (n < 1) {
            return 0.0;
        }

        double sum = 0.0;
        for (double val : data) {
            sum += val;
        }
        double mean = sum / n;

        double sumSquaredDiffs = 0.0;
        for (double val : data) {
            sumSquaredDiffs += Math.pow(val - mean, 2);
        }

        double variance = sumSquaredDiffs / n;

        return Math.sqrt(variance);
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        simulatorPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        priorityLabel = new javax.swing.JLabel();
        pileLabel = new javax.swing.JLabel();
        pileField = new javax.swing.JTextField();
        createProcessButton = new javax.swing.JButton();
        prioritySpinner = new javax.swing.JSpinner();
        create10ProcessButton = new javax.swing.JButton();
        cyclesToCompleteField = new javax.swing.JTextField();
        cyclesToCallField = new javax.swing.JTextField();
        cyclesToCallLabel = new javax.swing.JLabel();
        cyclesToCompleteLabel = new javax.swing.JLabel();
        ioBoundCheckBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        cycleConfigLabel = new javax.swing.JLabel();
        durationLabel = new javax.swing.JLabel();
        durationField = new javax.swing.JTextField();
        unitComboBox = new javax.swing.JComboBox<>();
        setDurationButton = new javax.swing.JButton();
        scheduleLabel = new javax.swing.JLabel();
        scheduleComboBox = new javax.swing.JComboBox<>();
        setScheduleButton = new javax.swing.JButton();
        quantumLabel = new javax.swing.JLabel();
        quantumField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        cycleStatusLabel = new javax.swing.JLabel();
        currentDurationLabel = new javax.swing.JLabel();
        currentDurationValueLabel = new javax.swing.JLabel();
        currentScheduleLabel = new javax.swing.JLabel();
        currentScheduleValueLabel = new javax.swing.JLabel();
        cycleLabel = new javax.swing.JLabel();
        finishedLabel = new javax.swing.JLabel();
        finishedScrollPane = new javax.swing.JScrollPane();
        finishedListPanel = new javax.swing.JPanel();
        mainMemoryPanel = new javax.swing.JPanel();
        mainMemoryLabel = new javax.swing.JLabel();
        readyLabel = new javax.swing.JLabel();
        readyScrollPane = new javax.swing.JScrollPane();
        readyListPanel = new javax.swing.JPanel();
        blockedLabel = new javax.swing.JLabel();
        blockedScrollPane = new javax.swing.JScrollPane();
        blockedListPanel = new javax.swing.JPanel();
        secondaryMemoryPanel = new javax.swing.JPanel();
        secondaryMemoryLabel = new javax.swing.JLabel();
        readySuspendedLabel = new javax.swing.JLabel();
        readySuspendedScrollPane = new javax.swing.JScrollPane();
        readySuspendedListPanel = new javax.swing.JPanel();
        blockedSuspendedLabel = new javax.swing.JLabel();
        blockedSuspendedScrollPane = new javax.swing.JScrollPane();
        blockedSuspendedListPanel = new javax.swing.JPanel();
        newProcessLabel = new javax.swing.JLabel();
        newProcessScrollPane = new javax.swing.JScrollPane();
        newProcessListPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        readyScrollPane2 = new javax.swing.JScrollPane();
        runningProcessPanel = new javax.swing.JPanel();
        mainMemoryLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        memoryFreeLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cpuStatusLabel = new javax.swing.JLabel();
        graphicsPanel = new javax.swing.JPanel();
        logPanel = new javax.swing.JPanel();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.addHierarchyListener(new java.awt.event.HierarchyListener() {
            public void hierarchyChanged(java.awt.event.HierarchyEvent evt) {
                jTabbedPane1HierarchyChanged(evt);
            }
        });

        simulatorPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(225, 225, 225));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Creacion de procesos");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        nameLabel.setText("Nombre:");
        jPanel1.add(nameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 39, -1, -1));

        nameField.setPreferredSize(new java.awt.Dimension(180, 22));
        jPanel1.add(nameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 36, -1, -1));

        priorityLabel.setText("Prioridad:");
        jPanel1.add(priorityLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 67, -1, -1));

        pileLabel.setText("T. Pila:");
        jPanel1.add(pileLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 95, -1, -1));

        pileField.setPreferredSize(new java.awt.Dimension(180, 22));
        jPanel1.add(pileField, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 92, -1, -1));

        createProcessButton.setText("Crear Proceso");
        createProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createProcessButtonActionPerformed(evt);
            }
        });
        jPanel1.add(createProcessButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 124, -1));

        prioritySpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 99, 1));
        prioritySpinner.setPreferredSize(new java.awt.Dimension(180, 22));
        jPanel1.add(prioritySpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 64, -1, -1));

        create10ProcessButton.setText("Crear 10 Procesos");
        create10ProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                create10ProcessButtonActionPerformed(evt);
            }
        });
        jPanel1.add(create10ProcessButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 220, -1, -1));

        cyclesToCompleteField.setText("0");
        cyclesToCompleteField.setPreferredSize(new java.awt.Dimension(180, 22));
        jPanel1.add(cyclesToCompleteField, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 180, 85, -1));

        cyclesToCallField.setText("0");
        cyclesToCallField.setPreferredSize(new java.awt.Dimension(180, 22));
        jPanel1.add(cyclesToCallField, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 100, -1));

        cyclesToCallLabel.setText("Ciclos Excepción:");
        jPanel1.add(cyclesToCallLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 105, 20));

        cyclesToCompleteLabel.setText("Duración Excepción:");
        jPanel1.add(cyclesToCompleteLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, -1, 20));

        ioBoundCheckBox.setText("Proceso I/O Bound");
        ioBoundCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ioBoundCheckBoxActionPerformed(evt);
            }
        });
        jPanel1.add(ioBoundCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 120, -1, -1));

        simulatorPanel.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 300, 260));

        jPanel2.setBackground(new java.awt.Color(225, 225, 225));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cycleConfigLabel.setText("Configurar Simulación");

        durationLabel.setText("Duración del Ciclo:");

        durationField.setText("1");

        unitComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Segundos", "Milisegundos" }));
        unitComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unitComboBoxActionPerformed(evt);
            }
        });

        setDurationButton.setText("Aplicar");
        setDurationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDurationButtonActionPerformed(evt);
            }
        });

        scheduleLabel.setText("Algoritmo Planificación:");

        scheduleComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Priority", "FIFO", "RR", "SN", "SRT", "HRR", "FeedBack" }));
        scheduleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleComboBoxActionPerformed(evt);
            }
        });

        setScheduleButton.setText("Aplicar");
        setScheduleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setScheduleButtonActionPerformed(evt);
            }
        });

        quantumLabel.setText("Quantum (ms):");

        quantumField.setText("100");
        quantumField.setPreferredSize(new java.awt.Dimension(60, 22));
        quantumField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantumFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(durationLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(durationField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(unitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(setDurationButton))
                    .addComponent(cycleConfigLabel)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(scheduleLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scheduleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(quantumLabel)
                                .addGap(18, 18, 18)
                                .addComponent(quantumField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(setScheduleButton)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cycleConfigLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(durationLabel)
                    .addComponent(durationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setDurationButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scheduleLabel)
                    .addComponent(scheduleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setScheduleButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(quantumLabel)
                    .addComponent(quantumField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        simulatorPanel.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 600, 410, 140));

        jPanel3.setBackground(new java.awt.Color(225, 225, 225));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cycleStatusLabel.setText("Estado de la Simulación");

        currentDurationLabel.setText("Duración actual (ms):");

        currentDurationValueLabel.setText("N/A");

        currentScheduleLabel.setText("Algoritmo actual:");

        currentScheduleValueLabel.setText("N/A");

        cycleLabel.setText("Ciclos hasta el momento: 0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cycleLabel)
                    .addComponent(cycleStatusLabel)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(currentDurationLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentDurationValueLabel))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(currentScheduleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentScheduleValueLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cycleStatusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentDurationLabel)
                    .addComponent(currentDurationValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentScheduleLabel)
                    .addComponent(currentScheduleValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cycleLabel)
                .addContainerGap())
        );

        simulatorPanel.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 600, -1, 120));

        finishedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        finishedLabel.setText("Procesos Terminados");
        simulatorPanel.add(finishedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 550, 660, 30));

        finishedScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        finishedScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        finishedListPanel.setBackground(new java.awt.Color(240, 240, 240));
        finishedListPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        finishedListPanel.setLayout(new javax.swing.BoxLayout(finishedListPanel, javax.swing.BoxLayout.X_AXIS));

        finishedListPanel.setLayout(new BoxLayout(finishedListPanel, BoxLayout.X_AXIS));

        finishedScrollPane.setViewportView(finishedListPanel);

        simulatorPanel.add(finishedScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 580, 660, 140));

        mainMemoryPanel.setBackground(new java.awt.Color(225, 225, 225));
        mainMemoryPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        mainMemoryLabel.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        mainMemoryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainMemoryLabel.setText("Memoria principal");

        readyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        readyLabel.setText("Ready");

        readyScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        readyScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        readyListPanel.setBackground(new java.awt.Color(240, 240, 240));
        readyListPanel.setLayout(new javax.swing.BoxLayout(readyListPanel, javax.swing.BoxLayout.Y_AXIS));

        readyListPanel.setLayout(new BoxLayout(readyListPanel, BoxLayout.Y_AXIS));

        readyScrollPane.setViewportView(readyListPanel);

        blockedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        blockedLabel.setText("Blocked");

        blockedScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        blockedScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        blockedListPanel.setBackground(new java.awt.Color(240, 240, 240));
        blockedListPanel.setLayout(new javax.swing.BoxLayout(blockedListPanel, javax.swing.BoxLayout.Y_AXIS));

        blockedListPanel.setLayout(new BoxLayout(blockedListPanel, BoxLayout.Y_AXIS));

        blockedScrollPane.setViewportView(blockedListPanel);

        javax.swing.GroupLayout mainMemoryPanelLayout = new javax.swing.GroupLayout(mainMemoryPanel);
        mainMemoryPanel.setLayout(mainMemoryPanelLayout);
        mainMemoryPanelLayout.setHorizontalGroup(
            mainMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainMemoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainMemoryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainMemoryPanelLayout.createSequentialGroup()
                        .addGroup(mainMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(readyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(readyScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(mainMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(blockedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(blockedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        mainMemoryPanelLayout.setVerticalGroup(
            mainMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainMemoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainMemoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainMemoryPanelLayout.createSequentialGroup()
                        .addComponent(readyLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(readyScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainMemoryPanelLayout.createSequentialGroup()
                        .addComponent(blockedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(blockedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        simulatorPanel.add(mainMemoryPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 30, -1, -1));

        secondaryMemoryPanel.setBackground(new java.awt.Color(225, 225, 225));
        secondaryMemoryPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        secondaryMemoryLabel.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        secondaryMemoryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        secondaryMemoryLabel.setText("Memoria secundaria");

        readySuspendedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        readySuspendedLabel.setText("Ready/Suspended");

        readySuspendedScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        readySuspendedScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        readySuspendedListPanel.setBackground(new java.awt.Color(240, 240, 240));
        readySuspendedListPanel.setLayout(new javax.swing.BoxLayout(readySuspendedListPanel, javax.swing.BoxLayout.Y_AXIS));

        readySuspendedListPanel.setLayout(new BoxLayout(readySuspendedListPanel, BoxLayout.Y_AXIS));

        readySuspendedScrollPane.setViewportView(readySuspendedListPanel);

        blockedSuspendedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        blockedSuspendedLabel.setText("Blocked/Suspended");

        blockedSuspendedScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        blockedSuspendedScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        blockedSuspendedListPanel.setBackground(new java.awt.Color(240, 240, 240));
        blockedSuspendedListPanel.setLayout(new javax.swing.BoxLayout(blockedSuspendedListPanel, javax.swing.BoxLayout.Y_AXIS));

        blockedSuspendedListPanel.setLayout(new BoxLayout(blockedSuspendedListPanel, BoxLayout.Y_AXIS));

        blockedSuspendedScrollPane.setViewportView(blockedSuspendedListPanel);

        newProcessLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        newProcessLabel.setText("Cola de Nuevos Procesos");

        newProcessScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        newProcessScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        newProcessListPanel.setBackground(new java.awt.Color(225, 225, 225));
        newProcessListPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        newProcessListPanel.setLayout(new javax.swing.BoxLayout(newProcessListPanel, javax.swing.BoxLayout.Y_AXIS));

        newProcessListPanel.setLayout(new BoxLayout(newProcessListPanel, BoxLayout.Y_AXIS));

        newProcessScrollPane.setViewportView(newProcessListPanel);

        javax.swing.GroupLayout secondaryMemoryPanelLayout = new javax.swing.GroupLayout(secondaryMemoryPanel);
        secondaryMemoryPanel.setLayout(secondaryMemoryPanelLayout);
        secondaryMemoryPanelLayout.setHorizontalGroup(
            secondaryMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secondaryMemoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(secondaryMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(secondaryMemoryPanelLayout.createSequentialGroup()
                        .addGroup(secondaryMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(readySuspendedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(readySuspendedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(secondaryMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(blockedSuspendedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(blockedSuspendedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(secondaryMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(newProcessLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newProcessScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(secondaryMemoryPanelLayout.createSequentialGroup()
                        .addComponent(secondaryMemoryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(40, 40, 40))))
        );
        secondaryMemoryPanelLayout.setVerticalGroup(
            secondaryMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secondaryMemoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(secondaryMemoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(secondaryMemoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(secondaryMemoryPanelLayout.createSequentialGroup()
                        .addComponent(readySuspendedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(readySuspendedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(secondaryMemoryPanelLayout.createSequentialGroup()
                        .addComponent(blockedSuspendedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(blockedSuspendedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(secondaryMemoryPanelLayout.createSequentialGroup()
                        .addComponent(newProcessLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newProcessScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        simulatorPanel.add(secondaryMemoryPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 30, -1, -1));

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        readyScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        readyScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        runningProcessPanel.setBackground(new java.awt.Color(240, 240, 240));
        runningProcessPanel.setLayout(new javax.swing.BoxLayout(runningProcessPanel, javax.swing.BoxLayout.Y_AXIS));

        readyListPanel.setLayout(new BoxLayout(readyListPanel, BoxLayout.Y_AXIS));

        readyScrollPane2.setViewportView(runningProcessPanel);

        mainMemoryLabel1.setBackground(new java.awt.Color(0, 0, 0));
        mainMemoryLabel1.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        mainMemoryLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainMemoryLabel1.setText("CPU");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(readyScrollPane2)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainMemoryLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainMemoryLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(readyScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        simulatorPanel.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 310, 190, 220));

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));

        memoryFreeLabel.setBackground(new java.awt.Color(0, 0, 0));
        memoryFreeLabel.setText("0");

        jLabel2.setText("Memoria disponible");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                .addComponent(memoryFreeLabel)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(memoryFreeLabel))
                .addContainerGap())
        );

        simulatorPanel.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 520, 290, 30));

        cpuStatusLabel.setBackground(new java.awt.Color(0, 0, 0));
        cpuStatusLabel.setText("KERNEL MODE");
        simulatorPanel.add(cpuStatusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 530, 140, 30));

        jTabbedPane1.addTab("Simulador", simulatorPanel);

        javax.swing.GroupLayout graphicsPanelLayout = new javax.swing.GroupLayout(graphicsPanel);
        graphicsPanel.setLayout(graphicsPanelLayout);
        graphicsPanelLayout.setHorizontalGroup(
            graphicsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1527, Short.MAX_VALUE)
        );
        graphicsPanelLayout.setVerticalGroup(
            graphicsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 830, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Graficos", graphicsPanel);

        logPanel.setLayout(new java.awt.BorderLayout());

        logTextArea.setEditable(false);
        logTextArea.setBackground(new java.awt.Color(0, 0, 0));
        logTextArea.setColumns(20);
        logTextArea.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        logTextArea.setForeground(new java.awt.Color(255, 255, 255));
        logTextArea.setRows(5);
        logScrollPane.setViewportView(logTextArea);

        javax.swing.JSplitPane infoSplitPane = new javax.swing.JSplitPane();
        infoSplitPane.setOrientation(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
        infoSplitPane.setResizeWeight(0.6);
        infoSplitPane.setLeftComponent(logScrollPane);
        
        javax.swing.JPanel metricsPanel = new javax.swing.JPanel();
        metricsPanel.setLayout(new java.awt.BorderLayout());
        
        metricsTextArea = new JTextArea("Las métricas de rendimiento aparecerán aquí...");
        metricsTextArea.setEditable(false);
        metricsTextArea.setFont(new java.awt.Font("Monospaced", 0, 14));
        metricsTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        metricsPanel.add(new JScrollPane(metricsTextArea), java.awt.BorderLayout.CENTER);
        
        javax.swing.JPanel saveConfigPanel = new javax.swing.JPanel();
        saveConfigPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        
        javax.swing.JButton saveConfigButton = new javax.swing.JButton();
        saveConfigButton.setText("Guardar Configuración");
        saveConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConfigButtonActionPerformed(evt);
            }
        });
        
        saveConfigPanel.add(saveConfigButton);
        metricsPanel.add(saveConfigPanel, java.awt.BorderLayout.SOUTH);
        
        infoSplitPane.setRightComponent(metricsPanel);
        logPanel.add(infoSplitPane, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Info", logPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>                        

    private void createProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        try {
            String name = nameField.getText();
            int priority = (int) prioritySpinner.getValue();
            long pile = Long.parseLong(pileField.getText());

            long cyclesToCall = 0;
            long cyclesToComplete = 0;
            boolean isIOBound = ioBoundCheckBox.isSelected();

            if (name.trim().isEmpty() || pile <= 0) {
                JOptionPane.showMessageDialog(this, "Nombre inválido o T. Pila debe ser positivo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (os != null && pile > os.getMemorySpace()) {
                JOptionPane.showMessageDialog(this, 
                    "El T. Pila (" + pile + " KB) no puede exceder la memoria total del sistema (" + os.getMemorySpace() + " KB).", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isIOBound) {
                cyclesToCall = Long.parseLong(cyclesToCallField.getText());
                cyclesToComplete = Long.parseLong(cyclesToCompleteField.getText());

                if (cyclesToCall <= 0 || cyclesToComplete <= 0) {
                    JOptionPane.showMessageDialog(this, "Para procesos I/O Bound, ambos ciclos de excepción deben ser mayores a 0.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
             if (os != null) {
                 os.createNewProcess(name, priority, pile, cyclesToCall, cyclesToComplete);
                 addLogMessage("INFO: Proceso '" + name + "' creado manualmente.");
             } else {
                 addLogMessage("ERROR: Sistema Operativo no inicializado al intentar crear proceso.");
             }


            nameField.setText("");
            pileField.setText("");
            prioritySpinner.setValue(1);
            cyclesToCallField.setText("0");
            cyclesToCompleteField.setText("0");
            ioBoundCheckBox.setSelected(false);
            ioBoundCheckBoxActionPerformed(null);


        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos para T. Pila y Ciclos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
             addLogMessage("ERROR: Formato numérico inválido al crear proceso.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creando proceso: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, "Error en createProcessButtonActionPerformed", e);
             addLogMessage("ERROR: Creando proceso - " + e.getMessage());
        }
    }                                                 

    private void create10ProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                    
         if (os == null) {
             addLogMessage("ERROR: Sistema Operativo no inicializado al intentar crear 10 procesos.");
             return;
         }
        try {
            java.util.Random rand = new java.util.Random();
            addLogMessage("INFO: Iniciando creación de 10 procesos aleatorios...");
            for (int i = 0; i < 10; i++) {
                String name = "Proc-" + (os.getProcessIdCounter());
                int priority = rand.nextInt(5) + 1; 
                long maxPile = os.getMemorySpace() > 1 ? os.getMemorySpace() / 4 : 1;
                 if (maxPile <= 0) maxPile = 1;
                long pile = rand.nextLong(maxPile/2) + 1;
                long cyclesToCall = 0;
                long cyclesToComplete = 0;
                boolean isIO = rand.nextBoolean();

                if (isIO) { 
                    cyclesToCall = rand.nextInt(100) + 10;
                    cyclesToComplete = rand.nextInt(50) + 5;
                }
                os.createNewProcess(name, priority, pile, cyclesToCall, cyclesToComplete);
            }
             addLogMessage("INFO: Finalizada creación de 10 procesos aleatorios.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creando 10 procesos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, "Error en create10ProcessButtonActionPerformed", e);
             addLogMessage("ERROR: Creando 10 procesos - " + e.getMessage());
        }
    }                                                   

    private void setScheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        if (this.os == null) {
            addLogMessage("ERROR: Sistema Operativo no está inicializado. No se puede cambiar el scheduler.");
            JOptionPane.showMessageDialog(this, "El simulador no está inicializado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedScheduleStr = (String) scheduleComboBox.getSelectedItem();
        Schedule selectedSchedule = null;
        long quantumValue = -1;

         try {
              switch (selectedScheduleStr) {
                  case "FIFO": selectedSchedule = Schedule.FIFO; break;
                  case "RR": selectedSchedule = Schedule.ROUND_ROBIN; break;
                  case "Priority": selectedSchedule = Schedule.PRIORITY; break;
                  case "SN": selectedSchedule = Schedule.SHORTEST_NEXT; break;
                  case "SRT": selectedSchedule = Schedule.SHORTEST_REMAINING_TIME; break;
                  case "HRR": selectedSchedule = Schedule.HIGHEST_RESPONSE_RATIO; break;
                  case "FeedBack": selectedSchedule = Schedule.FEEDBACK; break;
                  default: throw new IllegalArgumentException("Algoritmo desconocido: " + selectedScheduleStr);
              }
         } catch (IllegalArgumentException e) {
             addLogMessage("ERROR: Algoritmo de planificación seleccionado inválido: " + selectedScheduleStr);
             JOptionPane.showMessageDialog(this, "Algoritmo seleccionado inválido: " + selectedScheduleStr, "Error", JOptionPane.ERROR_MESSAGE);
             return;
         }


        if (selectedSchedule == Schedule.ROUND_ROBIN || selectedSchedule == Schedule.FEEDBACK) {
            try {
                quantumValue = Long.parseLong(quantumField.getText().trim());
                if (quantumValue <= 0) {
                    JOptionPane.showMessageDialog(this, "El Quantum debe ser un número positivo.", "Quantum Inválido", JOptionPane.WARNING_MESSAGE);
                    return; 
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido para el Quantum.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        os.setSchedule(selectedSchedule);
        if (quantumValue > 0) {
            os.setQuantum(quantumValue);
        }
        currentScheduleValueLabel.setText(selectedScheduleStr + (quantumValue > 0 ? " (Q=" + quantumValue + ")" : ""));
        String logMsg = "--> Algoritmo de planificación cambiado a: " + selectedScheduleStr;
        if (quantumValue > 0) {
            logMsg += " con Quantum=" + quantumValue;
        }
        addLogMessage(logMsg);

    }                                                 

    private void scheduleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        String selection = (String) scheduleComboBox.getSelectedItem();
        
        boolean isQuantumVisible = "RR".equals(selection) || "FeedBack".equals(selection); 
        
        quantumLabel.setVisible(isQuantumVisible);
        quantumField.setVisible(isQuantumVisible);
        
        jPanel2.revalidate();
        jPanel2.repaint();
    }                                                
    
    
    private void setDurationButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        if (this.os == null) {
            addLogMessage("ERROR: OperatingSystem no está inicializado.");
            return;
        }

        try {
            long value = Long.parseLong(durationField.getText());
            String unit = (String) unitComboBox.getSelectedItem();

            if (value <= 0) {
                JOptionPane.showMessageDialog(this, "El valor debe ser positivo.", "Error de entrada", JOptionPane.WARNING_MESSAGE);
                durationField.setText("1");
                return;
            }

            this.os.setCycleDuration(value, unit);

            long durationInMs = "Segundos".equals(unit) ? value * 1000 : value;
            currentDurationValueLabel.setText(durationInMs + " ms");
            addLogMessage("--> Duración del ciclo establecida en: " + value + " " + unit + " (" + durationInMs + " ms).");


        } catch (NumberFormatException e) {
            addLogMessage("ERROR: Entrada inválida para duración. Se requiere un número entero.");
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            durationField.setText("1");
        }
    }                                                 

    private void unitComboBoxActionPerformed(java.awt.event.ActionEvent evt) {                                             
    }                                            

    private void jTabbedPane1HierarchyChanged(java.awt.event.HierarchyEvent evt) {                                              
    }                                             

    private void ioBoundCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {                                                
        boolean isIOBound = ioBoundCheckBox.isSelected();

            cyclesToCallField.setEnabled(isIOBound);
            cyclesToCompleteField.setEnabled(isIOBound);
            cyclesToCallLabel.setEnabled(isIOBound);
            cyclesToCompleteLabel.setEnabled(isIOBound);

            if (!isIOBound) {
                cyclesToCallField.setText("0");
                cyclesToCompleteField.setText("0");
            }
    }                                               

    private void quantumFieldActionPerformed(java.awt.event.ActionEvent evt) {                                             
         setScheduleButtonActionPerformed(null);
    }                                            
    
     private void resetChartDataAndMetrics() {
         SwingUtilities.invokeLater(() -> {
             for (XYSeries series : throughputSeriesMap.values()) {
                 series.clear();
             }
             for (XYSeries series : cpuUtilizationSeriesMap.values()) {
                 series.clear();
             }
             for (XYSeries series : avgWaitTimeSeriesMap.values()) {
                 series.clear();
             }
             for (XYSeries series : fairnessSeriesMap.values()) {
                 series.clear();
             }

             for (Schedule alg : Schedule.values()) {
                 totalBusyCyclesMap.put(alg, 0L);
                 totalWaitingTimeMap.put(alg, 0.0);
                 totalFinishedProcsMap.put(alg, 0L);
                 List<Double> waitTimes = finishedWaitTimesMap.get(alg);
                 if (waitTimes != null) {
                     waitTimes.clear();
                 }
             }

             graphedFinishedProcessCount = 0;
             
             addLogMessage("INFO: Datos de gráficos y métricas reiniciados.");
         });
     }
   
    private void saveConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        if (os == null) {
            addLogMessage("ERROR: Sistema Operativo no inicializado.");
            JOptionPane.showMessageDialog(this, "El simulador no está inicializado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String configName = JOptionPane.showInputDialog(this,
                "Ingrese un nombre para la configuración (se guardará como .csv):",
                "Guardar Configuración",
                JOptionPane.PLAIN_MESSAGE);

        if (configName != null && !configName.trim().isEmpty()) {
            try {
                os.saveCurrentConfiguration(configName);
                addLogMessage("INFO: Configuración '" + configName + "' guardada exitosamente.");
                JOptionPane.showMessageDialog(this,
                        "Configuración guardada como '" + (configName.endsWith(".csv") ? configName : configName + ".csv"),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                addLogMessage("ERROR: No se pudo guardar la configuración. " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                        "Error al guardar la configuración: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (configName != null) {
            JOptionPane.showMessageDialog(this,
                    "El nombre no puede estar vacío.",
                    "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
     
     
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new GUI().setVisible(true));

    }

    // Variables declaration - do not modify                     
    private javax.swing.JLabel blockedLabel;
    private javax.swing.JPanel blockedListPanel;
    private javax.swing.JScrollPane blockedScrollPane;
    private javax.swing.JLabel blockedSuspendedLabel;
    private javax.swing.JPanel blockedSuspendedListPanel;
    private javax.swing.JScrollPane blockedSuspendedScrollPane;
    private javax.swing.JLabel cpuStatusLabel;
    private javax.swing.JButton create10ProcessButton;
    private javax.swing.JButton createProcessButton;
    private javax.swing.JLabel currentDurationLabel;
    private javax.swing.JLabel currentDurationValueLabel;
    private javax.swing.JLabel currentScheduleLabel;
    private javax.swing.JLabel currentScheduleValueLabel;
    private javax.swing.JLabel cycleConfigLabel;
    private javax.swing.JLabel cycleLabel;
    private javax.swing.JLabel cycleStatusLabel;
    private javax.swing.JTextField cyclesToCallField;
    private javax.swing.JLabel cyclesToCallLabel;
    private javax.swing.JTextField cyclesToCompleteField;
    private javax.swing.JLabel cyclesToCompleteLabel;
    private javax.swing.JTextField durationField;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JLabel finishedLabel;
    private javax.swing.JPanel finishedListPanel;
    private javax.swing.JScrollPane finishedScrollPane;
    private javax.swing.JPanel graphicsPanel;
    private javax.swing.JCheckBox ioBoundCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel logPanel;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JLabel mainMemoryLabel;
    private javax.swing.JLabel mainMemoryLabel1;
    private javax.swing.JPanel mainMemoryPanel;
    private javax.swing.JLabel memoryFreeLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel newProcessLabel;
    private javax.swing.JPanel newProcessListPanel;
    private javax.swing.JScrollPane newProcessScrollPane;
    private javax.swing.JTextField pileField;
    private javax.swing.JLabel pileLabel;
    private javax.swing.JLabel priorityLabel;
    private javax.swing.JSpinner prioritySpinner;
    private javax.swing.JTextField quantumField;
    private javax.swing.JLabel quantumLabel;
    private javax.swing.JLabel readyLabel;
    private javax.swing.JPanel readyListPanel;
    private javax.swing.JScrollPane readyScrollPane;
    private javax.swing.JScrollPane readyScrollPane2;
    private javax.swing.JLabel readySuspendedLabel;
    private javax.swing.JPanel readySuspendedListPanel;
    private javax.swing.JScrollPane readySuspendedScrollPane;
    private javax.swing.JPanel runningProcessPanel;
    private javax.swing.JComboBox<String> scheduleComboBox;
    private javax.swing.JLabel scheduleLabel;
    private javax.swing.JLabel secondaryMemoryLabel;
    private javax.swing.JPanel secondaryMemoryPanel;
    private javax.swing.JButton setDurationButton;
    private javax.swing.JButton setScheduleButton;
    private javax.swing.JPanel simulatorPanel;
    private javax.swing.JComboBox<String> unitComboBox;
    // End of variables declaration                   

    class MousePanListener extends MouseAdapter {

        private final Point dragStartPoint = new Point();
        private final JScrollPane scrollPane;
        private final JViewport viewport;
        private final Cursor defaultCursor;
        private final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        public MousePanListener(JScrollPane scrollPane) {
            this.scrollPane = scrollPane;
            this.viewport = scrollPane.getViewport();
            this.defaultCursor = scrollPane.getCursor();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            dragStartPoint.setLocation(e.getPoint());
            viewport.setCursor(handCursor);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            viewport.setCursor(defaultCursor);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point currentPoint = e.getPoint();
            Point viewPosition = viewport.getViewPosition();

            int deltaX = dragStartPoint.x - currentPoint.x;
            int deltaY = dragStartPoint.y - currentPoint.y;

            int sensitivityFactor = 2;
            int newX = viewPosition.x + (deltaX * sensitivityFactor);
            int newY = viewPosition.y + (deltaY * sensitivityFactor);

            int maxX = viewport.getView().getWidth() - viewport.getWidth();
            int maxY = viewport.getView().getHeight() - viewport.getHeight();

            if (newX < 0) {
                newX = 0;
            }
            if (newX > maxX) {
                newX = maxX;
            }
            if (newY < 0) {
                newY = 0;
            }
            if (newY > maxY) {
                newY = maxY;
            }

            viewport.setViewPosition(new Point(newX, newY));
        }
    }

}