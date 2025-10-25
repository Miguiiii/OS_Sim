/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
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

    private long memorySpace;
    private long cycleDuration;
    private String unit;
    private String schedule;
    private boolean started = false;
    private cvs_manager_configuracion configLoader;

    public Inicio(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Configuración Inicial");
        setPreferredSize(new java.awt.Dimension(350, 300));
        this.configLoader = new cvs_manager_configuracion();
        pack(); 
    }
    
    public Inicio() {
        this(null, true);
    }

    public long getMemorySpace() { return memorySpace; }
    public long getCycleDuration() { return cycleDuration; }
    public String getUnit() { return unit; }
    public String getSchedule() { return schedule; }
    public boolean isStarted() { return started; }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        memoryLabel = new javax.swing.JLabel();
        memoryField = new javax.swing.JTextField();
        durationLabel = new javax.swing.JLabel();
        durationField = new javax.swing.JTextField();
        unitComboBox = new javax.swing.JComboBox<>();
        scheduleLabel = new javax.swing.JLabel();
        scheduleComboBox = new javax.swing.JComboBox<>();
        buttonPanel = new javax.swing.JPanel();
        loadConfigButton = new javax.swing.JButton();
        startButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setLayout(new java.awt.GridBagLayout());

        memoryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        memoryLabel.setText("Espacio de Memoria:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainPanel.add(memoryLabel, gridBagConstraints);

        memoryField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        memoryField.setText("1024");
        memoryField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memoryFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainPanel.add(memoryField, gridBagConstraints);

        durationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        durationLabel.setText("Duración del Ciclo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainPanel.add(durationLabel, gridBagConstraints);

        durationField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        durationField.setText("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainPanel.add(durationField, gridBagConstraints);

        unitComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Segundos", "Milisegundos" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainPanel.add(unitComboBox, gridBagConstraints);

        scheduleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        scheduleLabel.setText("Algoritmo de Planificación:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainPanel.add(scheduleLabel, gridBagConstraints);

        scheduleComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Priority", "FIFO", "RR", "SN", "SRT", "HRR", "FeedBack" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainPanel.add(scheduleComboBox, gridBagConstraints);

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
    }// </editor-fold>//GEN-END:initComponents

    private void memoryFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memoryFieldActionPerformed

    }//GEN-LAST:event_memoryFieldActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            long mem = Long.parseLong(memoryField.getText());
            long dur = Long.parseLong(durationField.getText());
            String u = (String) unitComboBox.getSelectedItem();
            String s = (String) scheduleComboBox.getSelectedItem();
            if (mem <= 0 || dur <= 0) {
                 JOptionPane.showMessageDialog(this, 
                    "Los valores deben ser positivos.", "Error de entrada", 
                    JOptionPane.WARNING_MESSAGE);
                 return;
            }

            this.memorySpace = mem;
            this.cycleDuration = dur;
            this.unit = u;
            this.schedule = s;
            this.started = true;
            this.dispose(); 

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, ingrese números válidos.", "Error de formato", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de configuración");
        fileChooser.setCurrentDirectory(new File(".")); // Directorio actual
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            String[] config = configLoader.cargarConfiguracion(fileToLoad.getAbsolutePath());

            if (config != null && config.length == 4) {
                scheduleComboBox.setSelectedItem(config[0]);
                memoryField.setText(config[1]);
                durationField.setText(config[2]);
                unitComboBox.setSelectedItem(config[3]);
                JOptionPane.showMessageDialog(this, "Configuración cargada desde '" + fileToLoad.getName() + "'", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "El archivo seleccionado no es una configuración válida.", "Error de Carga", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JTextField durationField;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JButton loadConfigButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField memoryField;
    private javax.swing.JLabel memoryLabel;
    private javax.swing.JComboBox<String> scheduleComboBox;
    private javax.swing.JLabel scheduleLabel;
    private javax.swing.JButton startButton;
    private javax.swing.JComboBox<String> unitComboBox;
    // End of variables declaration//GEN-END:variables
}