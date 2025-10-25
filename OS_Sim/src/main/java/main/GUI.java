package main;

import OS_Structures.OS_Process;
import OS_Structures.OperatingSystem;
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

/**
 *
 * @author vince
 */
public class GUI extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GUI.class.getName());
    private OperatingSystem os;

    public GUI() {
        initComponents();

        // --- INICIO DE MODIFICACIONES MANUALES ---
        // Este código se ejecuta DESPUÉS de que el diseñador (initComponents) crea los componentes.
        
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

        // Ocultar Quantum por defecto
        quantumLabel.setVisible(false);
        quantumField.setVisible(false);
        
        // Configurar los listeners de "paneo" (arrastrar) para los JScrollPanes
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
        
        // --- FIN DE MODIFICACIONES MANUALES ---
    }

    public void setOperatingSystem(OperatingSystem os) {
        this.os = os;
    }

    public void updateCycleCount(long count) {
        SwingUtilities.invokeLater(() -> {
            cycleLabel.setText("Ciclos hasta el momento: " + count);
        });
    }

    public void addLogMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            logTextArea.append(message + "\n");
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

    public void setInitialSchedule(String schedule) {
        SwingUtilities.invokeLater(() -> {
            scheduleComboBox.setSelectedItem(schedule);

            currentScheduleValueLabel.setText(schedule);

            scheduleComboBoxActionPerformed(null);
        });
    }

    public void addNewProcessToView(OS_Process process) {
        SwingUtilities.invokeLater(() -> {
            createProcessCard(process, newProcessListPanel);
            newProcessListPanel.revalidate();
            newProcessListPanel.repaint();
        });
    }

    private void createProcessCard(OS_Process process, JPanel parentPanel) {
        JPanel processCard = new JPanel();
        processCard.setLayout(new BoxLayout(processCard, BoxLayout.Y_AXIS));
        processCard.setBackground(Color.WHITE);
        processCard.setBorder(BorderFactory.createEtchedBorder());

        processCard.add(Box.createRigidArea(new Dimension(0, 5)));
        processCard.add(new JLabel(" Nombre: " + process.getName()));
        processCard.add(new JLabel(" ID: " + process.getId()));
        processCard.add(new JLabel(" Prioridad: " + process.getPriority()));
        processCard.add(new JLabel(" T. Máx: " + process.getMaxRunTime() + "ms"));
        processCard.add(new JLabel(" Memoria: " + process.getPile() + "KB"));
        processCard.add(new JLabel(" Ciclo Nac.: " + process.getBirthTime()));
        processCard.add(Box.createRigidArea(new Dimension(0, 5)));

        int cardHeight = 110;
        processCard.setMinimumSize(new Dimension(100, cardHeight));
        processCard.setMaximumSize(new Dimension(Short.MAX_VALUE, cardHeight));

        parentPanel.add(processCard);

        if (parentPanel != finishedListPanel) {
            parentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }

    public void updateReadyList(Iterable<OS_Process> processes) {
        SwingUtilities.invokeLater(() -> {
            readyListPanel.removeAll();
            if (processes != null) {
                for (OS_Process process : processes) {
                    createProcessCard(process, readyListPanel);
                }
            }
            readyListPanel.revalidate();
            readyListPanel.repaint();
        });
    }

    public void updateBlockedList(Iterable<OS_Process> processes) {
        SwingUtilities.invokeLater(() -> {
            blockedListPanel.removeAll();
            if (processes != null) {
                for (OS_Process process : processes) {
                    createProcessCard(process, blockedListPanel);
                }
            }
            blockedListPanel.revalidate();
            blockedListPanel.repaint();
        });
    }

    public void updateReadySuspendedList(Iterable<OS_Process> processes) {
        SwingUtilities.invokeLater(() -> {
            readySuspendedListPanel.removeAll();
            if (processes != null) {
                for (OS_Process process : processes) {
                    createProcessCard(process, readySuspendedListPanel);
                }
            }
            readySuspendedListPanel.revalidate();
            readySuspendedListPanel.repaint();
        });
    }

    public void updateBlockedSuspendedList(Iterable<OS_Process> processes) {
        SwingUtilities.invokeLater(() -> {
            blockedSuspendedListPanel.removeAll();
            if (processes != null) {
                for (OS_Process process : processes) {
                    createProcessCard(process, blockedSuspendedListPanel);
                }
            }
            blockedSuspendedListPanel.revalidate();
            blockedSuspendedListPanel.repaint();
        });
    }

    public void addFinishedProcessToView(OS_Process process) {
        SwingUtilities.invokeLater(() -> {
            createProcessCard(process, finishedListPanel);
            finishedListPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            finishedListPanel.revalidate();
            finishedListPanel.repaint();
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        simulatorPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        priorityLabel = new javax.swing.JLabel();
        maxRunTimeLabel = new javax.swing.JLabel();
        maxRunTimeField = new javax.swing.JTextField();
        pileLabel = new javax.swing.JLabel();
        pileField = new javax.swing.JTextField();
        createProcessButton = new javax.swing.JButton();
        prioritySpinner = new javax.swing.JSpinner();
        create10ProcessButton = new javax.swing.JButton();
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
        graphicsPanel = new javax.swing.JPanel();
        logPanel = new javax.swing.JPanel();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        simulatorPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(225, 225, 225));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Creacion de procesos");

        nameLabel.setText("Nombre:");

        nameField.setPreferredSize(new java.awt.Dimension(180, 22));

        priorityLabel.setText("Prioridad:");

        maxRunTimeLabel.setText("T. Máx:");

        maxRunTimeField.setPreferredSize(new java.awt.Dimension(180, 22));

        pileLabel.setText("T. Pila:");

        pileField.setPreferredSize(new java.awt.Dimension(180, 22));

        createProcessButton.setText("Crear Proceso");
        createProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createProcessButtonActionPerformed(evt);
            }
        });

        prioritySpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 99, 1));
        prioritySpinner.setPreferredSize(new java.awt.Dimension(180, 22));

        create10ProcessButton.setText("Crear 10 Procesos");
        create10ProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                create10ProcessButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(maxRunTimeLabel)
                                    .addComponent(nameLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(priorityLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(pileLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(maxRunTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pileField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(prioritySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 12, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(createProcessButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(create10ProcessButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(priorityLabel)
                    .addComponent(prioritySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxRunTimeLabel)
                    .addComponent(maxRunTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pileLabel)
                    .addComponent(pileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(createProcessButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(create10ProcessButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        simulatorPanel.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

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

        simulatorPanel.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 600, 410, 120));

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

        simulatorPanel.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 600, -1, 120));

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

        jTabbedPane1.addTab("Simulador", simulatorPanel);

        javax.swing.GroupLayout graphicsPanelLayout = new javax.swing.GroupLayout(graphicsPanel);
        graphicsPanel.setLayout(graphicsPanelLayout);
        graphicsPanelLayout.setHorizontalGroup(
            graphicsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1634, Short.MAX_VALUE)
        );
        graphicsPanelLayout.setVerticalGroup(
            graphicsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 726, Short.MAX_VALUE)
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

        logPanel.add(logScrollPane, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Log", logPanel);

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
    }// </editor-fold>//GEN-END:initComponents

    private void create10ProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_create10ProcessButtonActionPerformed
        if (this.os == null) {
            addLogMessage("ERROR: OperatingSystem no está inicializado.");
            return;
        }

        addLogMessage("==> Creando 10 procesos batch...");
        for (int i = 1; i <= 10; i++) {
            String name = "Batch_" + i;
            long maxRunTime = 10000;
            long pile = 1024;
            int priority = 5;
            os.createNewProcess(name, maxRunTime, pile, priority);
        }
        addLogMessage("==> 10 procesos creados.");
    }//GEN-LAST:event_create10ProcessButtonActionPerformed

    private void createProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createProcessButtonActionPerformed
        if (this.os == null) {
            addLogMessage("ERROR: OperatingSystem no está inicializado.");
            return;
        }

        String name = nameField.getText().trim();
        int priority = (Integer) prioritySpinner.getValue();
        String maxRunTimeStr = maxRunTimeField.getText().trim();
        String pileStr = pileField.getText().trim();

        if (name.isEmpty() || maxRunTimeStr.isEmpty() || pileStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete los campos 'Nombre', 'T. Máx' y 'Memoria'.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            long maxRunTime = Long.parseLong(maxRunTimeStr);
            long pile = Long.parseLong(pileStr);

            if (maxRunTime <= 0 || pile <= 0) {
                JOptionPane.showMessageDialog(this, "Los valores numéricos (t. máx, memoria) deben ser positivos.", "Valores inválidos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            os.createNewProcess(name, maxRunTime, pile, priority);

            nameField.setText("");
            prioritySpinner.setValue(1);
            maxRunTimeField.setText("");
            pileField.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese números válidos para t. máx y memoria.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_createProcessButtonActionPerformed

    private void setScheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setScheduleButtonActionPerformed
        if (this.os == null) {
            addLogMessage("ERROR: OperatingSystem no está inicializado.");
            return;
        }

        String selectedSchedule = (String) scheduleComboBox.getSelectedItem();
        long quantumValue = -1;

        if ("RR".equals(selectedSchedule)) {
            try {
                quantumValue = Long.parseLong(quantumField.getText().trim());
                if (quantumValue <= 0) {
                    JOptionPane.showMessageDialog(this, "El Quantum debe ser un número positivo.", "Quantum Inválido", JOptionPane.WARNING_MESSAGE);
                    quantumField.setText("100");
                    return;
                }
                addLogMessage("--> Quantum para Round Robin establecido en: " + quantumValue + " ms");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido para el Quantum.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                quantumField.setText("100");
                return;
            }
        }

        currentScheduleValueLabel.setText(selectedSchedule + (quantumValue > 0 ? " (Q=" + quantumValue + ")" : ""));
    }//GEN-LAST:event_setScheduleButtonActionPerformed

    private void scheduleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleComboBoxActionPerformed
        String selectedSchedule = (String) scheduleComboBox.getSelectedItem();
        boolean isRR = "RR".equals(selectedSchedule);

        quantumLabel.setVisible(isRR);
        quantumField.setVisible(isRR);
    }//GEN-LAST:event_scheduleComboBoxActionPerformed

    private void setDurationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setDurationButtonActionPerformed
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
                addLogMessage("ERROR: El valor de duración debe ser positivo.");
                return;
            }

            this.os.setCycleDuration(value, unit);

            long durationInMs = "Segundos".equals(unit) ? value * 1000 : value;
            currentDurationValueLabel.setText(durationInMs + " ms");

        } catch (NumberFormatException e) {
            addLogMessage("ERROR: Entrada inválida. Se requiere un número entero.");
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            durationField.setText("1");
        }
    }//GEN-LAST:event_setDurationButtonActionPerformed

    private void unitComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unitComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_unitComboBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new GUI().setVisible(true));

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel blockedLabel;
    private javax.swing.JPanel blockedListPanel;
    private javax.swing.JScrollPane blockedScrollPane;
    private javax.swing.JLabel blockedSuspendedLabel;
    private javax.swing.JPanel blockedSuspendedListPanel;
    private javax.swing.JScrollPane blockedSuspendedScrollPane;
    private javax.swing.JButton create10ProcessButton;
    private javax.swing.JButton createProcessButton;
    private javax.swing.JLabel currentDurationLabel;
    private javax.swing.JLabel currentDurationValueLabel;
    private javax.swing.JLabel currentScheduleLabel;
    private javax.swing.JLabel currentScheduleValueLabel;
    private javax.swing.JLabel cycleConfigLabel;
    private javax.swing.JLabel cycleLabel;
    private javax.swing.JLabel cycleStatusLabel;
    private javax.swing.JTextField durationField;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JLabel finishedLabel;
    private javax.swing.JPanel finishedListPanel;
    private javax.swing.JScrollPane finishedScrollPane;
    private javax.swing.JPanel graphicsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel logPanel;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JLabel mainMemoryLabel;
    private javax.swing.JPanel mainMemoryPanel;
    private javax.swing.JTextField maxRunTimeField;
    private javax.swing.JLabel maxRunTimeLabel;
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
    private javax.swing.JLabel readySuspendedLabel;
    private javax.swing.JPanel readySuspendedListPanel;
    private javax.swing.JScrollPane readySuspendedScrollPane;
    private javax.swing.JComboBox<String> scheduleComboBox;
    private javax.swing.JLabel scheduleLabel;
    private javax.swing.JLabel secondaryMemoryLabel;
    private javax.swing.JPanel secondaryMemoryPanel;
    private javax.swing.JButton setDurationButton;
    private javax.swing.JButton setScheduleButton;
    private javax.swing.JPanel simulatorPanel;
    private javax.swing.JComboBox<String> unitComboBox;
    // End of variables declaration//GEN-END:variables

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