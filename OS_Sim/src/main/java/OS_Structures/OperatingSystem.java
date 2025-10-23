/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;
import Structures.*;
import main.GUI;
import main.Inicio;
import java.util.logging.Logger; 

/**
 *
 * @author Miguel
 */
public class OperatingSystem {
    
    private static final Logger logger = Logger.getLogger(OperatingSystem.class.getName());
    
    private volatile long cycleDuration;
    private volatile boolean isCycleInSeconds; 
    public static long cycleCounter = 0;
    private String schedule = "Priority";
    private long memorySpace;
    private ArrayList mainMemory;
    private ArrayList permMemory;
    private ReadyList readyProcesses;
    private ReadyList readySuspendedProcesses;
    private ArrayList blockedProcesses;
    private ArrayList blockedSuspendedProcesses;
    private ArrayList newProcesses;
    private ArrayList finishedProcesses;;
    private ProcessNode runningProcess;
    private GUI ventana;
    private cvs_manager_configuracion configManager;
    private Thread counterThread;
    
    public OperatingSystem() {
        this.isCycleInSeconds = true;
        this.cycleDuration = 1000; 
        this.configManager = new cvs_manager_configuracion();
        mainMemory = new ArrayList(20);
        permMemory = new ArrayList(20);
        readyProcesses = new ReadyList(20);
        readySuspendedProcesses = new ReadyList(20);
        blockedProcesses = new ArrayList(20);
        blockedSuspendedProcesses = new ArrayList(20);
        newProcesses = new ArrayList(20);
        finishedProcesses = new ArrayList(20);
        runningProcess = null;
    }

    public void boot() {
        Inicio inicioDialog = new Inicio(null, true);
        inicioDialog.setLocationRelativeTo(null);
        inicioDialog.setVisible(true); 

        if (inicioDialog.isStarted()) {
            setMemorySpace(inicioDialog.getMemorySpace());
            setCycleDuration(inicioDialog.getCycleDuration(), inicioDialog.getUnit());
            String initialSchedule = inicioDialog.getSchedule();
            this.setScheduleType(initialSchedule); 
            this.ventana = new GUI();
            this.ventana.setOperatingSystem(this); 
            this.startSystem();
            
        } else {
            System.exit(0);
        }
    }

    public long getCounter() {
        return cycleCounter;
    }
    
    public void setCycleDuration(long value, String unit) {
        long newDurationInMs;
        boolean newIsCycleInSeconds;
        
        if ("Segundos".equalsIgnoreCase(unit)) {
            newDurationInMs = value * 1000;
            newIsCycleInSeconds = true;
        } else { 
            newDurationInMs = value;
            newIsCycleInSeconds = false;
        }
        if (newDurationInMs < 1) {
            newDurationInMs = 1;
            if (ventana != null && ventana.isVisible()) {
                ventana.addLogMessage("ADVERTENCIA: La duración solicitada era < 1ms. Se ha establecido a 1ms.");
            }
        }
        if (newDurationInMs == this.cycleDuration && ventana != null && ventana.isVisible()) {
            ventana.addLogMessage("---> Intento de aplicar la misma duración. No se interrumpe el ciclo.");
            return;
        }

        this.cycleDuration = newDurationInMs;
        this.isCycleInSeconds = newIsCycleInSeconds;
        
        if (ventana != null && ventana.isVisible()) {
            ventana.addLogMessage("---> Duración del ciclo establecida a: " + value + " " + unit + " (" + this.cycleDuration + "ms)");
        }
        
        if (this.counterThread != null && this.counterThread.isAlive()) {
            this.counterThread.interrupt();
        }
    }
    
    
    public void startSystem() {
        this.counterThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(this.cycleDuration);
                    cycleCounter++;               
                    ventana.updateCycleCount(cycleCounter);

                } catch (InterruptedException e) {
                    ventana.addLogMessage("---> Ciclo interrumpido para aplicar nueva duración.");
                }
            }
        });
        this.counterThread.setDaemon(true); 
        this.counterThread.start();
        ventana.setVisible(true);
        // ventana.setLocationRelativeTo(null); // Quitado para que setExtendedState funcione
        ventana.addLogMessage("Sistema iniciado. Espacio de memoria total: " + this.memorySpace + " KB.");
        ventana.addLogMessage("Algoritmo de planificación: " + this.schedule);
        
        long initialValue;
        String initialUnit;
        if (this.isCycleInSeconds) {
            initialValue = this.cycleDuration / 1000;
            initialUnit = "Segundos";
        } else {
            initialValue = this.cycleDuration;
            initialUnit = "Milisegundos";
        }
        ventana.setInitialDuration(initialValue, initialUnit);
        ventana.setInitialSchedule(this.schedule);
    }

    public long getMemorySpace() {
        return memorySpace;
    }

    public void setMemorySpace(long memorySpace) {
        this.memorySpace = memorySpace;
    }
    
    //CAMBIO DE PLANIFICACION
    //PROVICIONAL
    //EJEMPLO DE COMO SE MANEJA EN CLASES INTERNAS DE COLA DE LISTOS Y NODO DE PROCESO
    public void switchSchedule(String schedule) {
        String prev_sched = getScheduleType();
        String new_schedule;
        new_schedule = switch (schedule) {
            case "Priority", "FIFO", "RR", "SN", "SRT", "HRR", "FeedBack" -> schedule;
            default -> "Priority";
        };
        if (!prev_sched.equals(new_schedule)) {
            setScheduleType(new_schedule);
     
            if (ventana != null && ventana.isVisible()) {
                ventana.addLogMessage("ALGORITMO CAMBIADO: De " + prev_sched + " a " + new_schedule);
            }
        } else {
            if (ventana != null && ventana.isVisible()) {
                ventana.addLogMessage("---> Algoritmo de planificación ya es " + new_schedule + ". No se aplica cambio.");
            }
        }
    }
    
    public void setScheduleType(String type) {
        this.schedule = type;
    }
    
    public String getScheduleType() {
        return this.schedule;
    }

    public void saveCurrentConfiguration(String configName) {
        long durationValue;
        String unit;

        if (this.isCycleInSeconds) {
            durationValue = this.cycleDuration / 1000;
            unit = "Segundos";
        } else {
            durationValue = this.cycleDuration;
            unit = "Milisegundos";
        }
        String fileName = configName.endsWith(".csv") ? configName : configName + ".csv";
        
        configManager.guardarConfiguracion(fileName, getScheduleType(), getMemorySpace(), durationValue, unit);
    }
    

    public void createNewProcess(String name, long maxRunTime, long pile, int priority) {
       
        long birthTime = getCounter();
        
        int id = (name + birthTime).hashCode();
        
        OS_Process newProcess = new OS_Process(name, id, birthTime, maxRunTime, pile, priority);
        
        this.newProcesses.insertFinal(newProcess); 
        
        if (ventana != null) {
            ventana.addLogMessage("PROCESO CREADO: " + newProcess.getName() + 
                                  " [ID: " + newProcess.getId() + 
                                  ", Prio: " + newProcess.getPriority() + 
                                  ", T.Max: " + newProcess.getMaxRunTime() + "ms" +
                                  ", Mem: " + newProcess.getPile() + "KB]" +
                                  " en Ciclo " + newProcess.getBirthTime());
            
            ventana.addNewProcessToView(newProcess);
        }
    }
    
    private void runProcess() {
        
    }
    
    private void manageSchedule() {
        
    }
    
    private void manageMemory() {
        
    }
    
}
