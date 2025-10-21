/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;
import Structures.*;
import java.time.Duration;
import java.util.concurrent.Semaphore;
import main.GUI;
import java.util.logging.Logger; 

/**
 *
 * @author Miguel
 */
public class OperatingSystem {
    
    // El logger original se puede mantener para logs internos si se desea,
    // pero los logs para el usuario ahora irán a la GUI.
    private static final Logger logger = Logger.getLogger(OperatingSystem.class.getName());
    
    private volatile long cycleDuration;
    private volatile boolean isCycleInSeconds; 
    public static long cycleCounter = 0;
    private String schedule = "Priority";
    private long quantum = 1;
    private boolean isInKernel = true;
    private long memorySpace;
    private ArrayList mainMemory;
    private ArrayList permMemory;
    private ReadyList readyProcesses;
    private ReadyList readySuspendedProcesses;
    private ArrayList blockedProcesses;
    private ArrayList blockedSuspendedProcesses;
    private ArrayList newProcesses;
    private ProcessNode runningProcess;
    private GUI ventana;
    private Semaphore readySem;
    
    // Campo para mantener una referencia al hilo
    private Thread counterThread;
    
    public OperatingSystem() {
        this.isCycleInSeconds = true;
        this.cycleDuration = 1000; 
        this.ventana = new GUI();
        this.ventana.setOperatingSystem(this); 
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
            // Modificado: Enviar a la GUI
            ventana.addLogMessage("ADVERTENCIA: La duración solicitada era < 1ms. Se ha establecido a 1ms.");
        }
        
        if (newDurationInMs == this.cycleDuration) {
            // Modificado: Enviar a la GUI
            ventana.addLogMessage("---> Intento de aplicar la misma duración. No se interrumpe el ciclo.");
            return;
        }

        this.cycleDuration = newDurationInMs;
        this.isCycleInSeconds = newIsCycleInSeconds;
        
        // Modificado: Enviar a la GUI
        ventana.addLogMessage("---> Duración del ciclo establecida a: " + value + " " + unit + " (" + this.cycleDuration + "ms)");
        
        if (this.counterThread != null && this.counterThread.isAlive()) {
            this.counterThread.interrupt();
        }
    }
    
    public long getCycleDuration() {
        return cycleDuration;
    }
    
    public void startSystem() {
        this.counterThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(this.cycleDuration);
                    cycleCounter++;               
                    ventana.updateCycleCount(cycleCounter);

                } catch (InterruptedException e) {
                    // Modificado: Enviar a la GUI
                    ventana.addLogMessage("---> Ciclo interrumpido para aplicar nueva duración.");
                }
            }
        });
        this.counterThread.setDaemon(true); 
        this.counterThread.start();
        
        this.readySem = new Semaphore(1);
        
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
    }

    public long getMemorySpace() {
        return memorySpace;
    }

    private void setMemorySpace(long memorySpace) {
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
            ProcessNode.priorityType=new_schedule;
            this.readyProcesses.switchSchedule(new_schedule);
            this.readySuspendedProcesses.switchSchedule(new_schedule);
        }
    }
    
    private void setScheduleType(String type) {
        this.schedule = type;
    }
    
    public String getScheduleType() {
        return this.schedule;
    }
    
    private OS_Process runProcess(OS_Process process) {
        long currentDuration = getCycleDuration();
        int lastSizeReady = this.readyProcesses.getSize();
        OS_Process p = process;
        long maxRun =  p.getMaxRunTime();
        long instructionsLeft = p.getPile()-p.getProgram_counter();
        long runFor = Math.min(maxRun, instructionsLeft);
        String currentSchedule = getScheduleType();
        switch (currentSchedule) {
            case "RR":
            case "FeedBack":
                runFor = Math.min(runFor, this.quantum);
                break;
            default:
                break;
        }
        //No quitar esto, que el thread no sirve si la variable no es final
        final long runTime = runFor;
        Thread running = new Thread(()->{
            try {
                Thread.sleep(runTime*this.cycleDuration);
            } catch (InterruptedException e) {
                // Modificado: Enviar a la GUI
                ventana.addLogMessage("---> Proceso interrumpido");
            }
        });
        running.setDaemon(true);
        this.isInKernel = false;
        long startCycle = getCounter();
        running.start();
        while (running.isAlive()) {
            if (currentDuration == getCycleDuration() && currentSchedule == getScheduleType() && !(currentSchedule == "SRT" && lastSizeReady != this.readyProcesses.getSize())) {
                continue;
            }
            long endCycle = getCounter();
            running.interrupt();
            p.setProgram_counter(p.getProgram_counter()+endCycle-startCycle);
            this.isInKernel = true;
            return p;
        }
        
        p.setProgram_counter(p.getProgram_counter()+runTime);
        this.isInKernel = true;
        return p;
    }
    
    private void manageSchedule() {
        if (!readyProcesses.isEmpty()) {
            OS_Process process = runProcess(readyProcesses.extractRoot());
        }
    }
    
    private void manageMemory() {
        
    }
    
}
