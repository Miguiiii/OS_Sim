package main;

import OS_Structures.cvs_manager_configuracion;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author vince
 */
public class Inicio extends javax.swing.JDialog {

    private boolean started = false;

    public Inicio(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent); 
        scheduleComboBoxActionPerformed(null);
    }

    public boolean isStarted() {
        return started;
    }

    public long getMemorySpace() {
        return Long.parseLong(memoryField.getText());
    }

    public long getCycleDuration() {
        return Long.parseLong(durationField.getText());
    }

    public String getUnit() {
        return (String) unitComboBox.getSelectedItem();
    }
    
    public String getSchedule() {
        return (String) scheduleComboBox.getSelectedItem();
    }
    
    public long getQuantum() {
        try {
            long quantum = Long.parseLong(quantumField.getText());
            return quantum > 0 ? quantum : 100; 
        } catch (NumberFormatException e) {
            return 100;
        }
    }

    private String getScheduleDisplayNameFromEnum(String enumName) {
        if (enumName == null) return "Priority"; // Fallback seguro
        switch (enumName.toUpperCase()) {
            case "ROUND_ROBIN": return "RR";
            case "SHORTEST_NEXT": return "SN";
            case "SHORTEST_REMAINING_TIME": return "SRT";
            case "HIGHEST_RESPONSE_RATIO": return "HRR";
            case "FEEDBACK": return "FeedBack";
            case "PRIORITY": return "Priority";
            case "FIFO": return "FIFO";
            default: return "Priority"; 
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        memoryLabel = new javax.swing.JLabel();
        memoryField = new javax.swing.JTextField();
        durationLabel = new javax.swing.JLabel();
        durationField = new javax.swing.JTextField();
        unitComboBox = new javax.swing.JComboBox<>();
        scheduleLabel = new javax.swing.JLabel();
        scheduleComboBox = new javax.swing.JComboBox<>();
        quantumLabel = new javax.swing.JLabel();
        quantumField = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        loadConfigButton = new javax.swing.JButton();
        startButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configuración Inicial del Simulador");

        memoryLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        memoryLabel.setText("Espacio de Memoria (KB):");

        memoryField.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        memoryField.setText("10240");

        durationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        durationLabel.setText("Duración del Ciclo:");

        durationField.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        durationField.setText("1");

        unitComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Segundos", "Milisegundos" }));

        scheduleLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        scheduleLabel.setText("Algoritmo de Planificación Inicial:");

        scheduleComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Priority", "FIFO", "RR", "SN", "SRT", "HRR", "FeedBack" }));
        scheduleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleComboBoxActionPerformed(evt);
            }
        });

        quantumLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        quantumLabel.setText("Quantum (para RR / FeedBack):");

        quantumField.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        quantumField.setText("100");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(quantumField)
                    .addComponent(quantumLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scheduleComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scheduleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(durationField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(unitComboBox, 0, 163, Short.MAX_VALUE))
                    .addComponent(durationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(memoryField)
                    .addComponent(memoryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(memoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(memoryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(durationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(durationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(scheduleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(quantumLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(quantumField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        loadConfigButton.setText("Cargar Configuración");
        loadConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadConfigButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(loadConfigButton);

        startButton.setText("Iniciar Simulación");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(startButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>                        

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            
        try {
            long mem = Long.parseLong(memoryField.getText());
            long dur = Long.parseLong(durationField.getText());
            if (mem <= 0 || dur <= 0) {
                JOptionPane.showMessageDialog(this, "Los valores de memoria y duración deben ser positivos.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ("RR".equals(getSchedule()) || "FeedBack".equals(getSchedule())) {
                 long quant = Long.parseLong(quantumField.getText());
                 if (quant <= 0) {
                     JOptionPane.showMessageDialog(this, "El Quantum debe ser un número positivo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                     return;
                 }
            }
            this.started = true;
            this.dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }                                           

    private void scheduleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        String selection = (String) scheduleComboBox.getSelectedItem();
        boolean isQuantumVisible = "RR".equals(selection) || "FeedBack".equals(selection);
        quantumLabel.setVisible(isQuantumVisible);
        quantumField.setVisible(isQuantumVisible);
        this.pack();
    }                                                

    private void loadConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setDialogTitle("Seleccionar archivo de configuración CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            cvs_manager_configuracion configManager = new cvs_manager_configuracion();
            String[] configData = configManager.cargarConfiguracion(fileToLoad.getAbsolutePath());

            if (configData != null && configData.length >= 5) {
                try {
                    String scheduleEnumName = configData[0];
                    String memory = configData[1];
                    String duration = configData[2];
                    String unit = configData[3];
                    String quantum = configData[4];
                    String scheduleDisplayName = getScheduleDisplayNameFromEnum(scheduleEnumName);
                    scheduleComboBox.setSelectedItem(scheduleDisplayName);
                    memoryField.setText(memory);
                    durationField.setText(duration);
                    unitComboBox.setSelectedItem(unit);
                    quantumField.setText(quantum);
                    scheduleComboBoxActionPerformed(null);

                    JOptionPane.showMessageDialog(this, "Configuración cargada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "El archivo de configuración parece estar dañado o malformado.\nError: " + e.getMessage(), "Error de Lectura", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cargar la configuración del archivo. Verifique que el formato sea correcto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }                                                
             
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JTextField durationField;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JButton loadConfigButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField memoryField;
    private javax.swing.JLabel memoryLabel;
    private javax.swing.JTextField quantumField;
    private javax.swing.JLabel quantumLabel;
    private javax.swing.JComboBox<String> scheduleComboBox;
    private javax.swing.JLabel scheduleLabel;
    private javax.swing.JButton startButton;
    private javax.swing.JComboBox<String> unitComboBox;         
}