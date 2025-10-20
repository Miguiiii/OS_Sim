/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;
import Structures.*;
import main.GUI;
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
    private ArrayList mainMemory;
    private ArrayList permMemory;
    private ReadyList readyProcesses;
    private ReadyList readySuspendedProcesses;
    private ArrayList blockedProcesses;
    private ArrayList blockedSuspendedProcesses;
    private ArrayList newProcesses;
    private ProcessNode runningProcess;
    private GUI ventana;
    
    public OperatingSystem() {
        this.isCycleInSeconds = true;
        this.cycleDuration = 1000; 
        this.ventana = new GUI();
        this.ventana.setOperatingSystem(this); 
    }
    
    public long getCounter() {
        return cycleCounter;
    }
    
    public void setCycleModeToSeconds() {
        this.isCycleInSeconds = true;
        this.cycleDuration = 1000;
        logger.info("Duración del ciclo establecida a 1 segundo");
    }
    
    public void setCycleModeToMilliseconds() {
        this.isCycleInSeconds = false;
        this.cycleDuration = 1;
        logger.info("Duración del ciclo establecida a 1 milisegundo");
    }
    
    public void startSystem() {
        Thread counterThread = new Thread(() -> {
            while (true) {
                cycleCounter++;               
                ventana.updateCycleCount(cycleCounter);

                try {
                    Thread.sleep(this.cycleDuration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Hilo del contador interrumpido");
                    break;
                }
            }
        });
        counterThread.setDaemon(true); 
        counterThread.start();
        
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
    }
}