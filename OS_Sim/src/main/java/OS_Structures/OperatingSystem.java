/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;
import Structures.*;
/**
 *
 * @author Miguel
 */
public class OperatingSystem {
    private long cycleDuration;
    private boolean isCycleInSeconds = true;
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
    private ProcessNode runningProcess;
   
    //Constructor con Argumentos
    public OperatingSystem(boolean isCycleInSeconds, long cycleDuration) {
        if (isCycleInSeconds) {
            this.cycleDuration = cycleDuration*1000;
        } else {
            this.cycleDuration = cycleDuration;
        }
        this.isCycleInSeconds=isCycleInSeconds;
    }
    //Constructor default, inicializa el OS con ciclos de 1 segundo
    public OperatingSystem() {
        this(true, 1);
    }
    
    public long getCounter() {
        return cycleCounter;
    }
    //Inicializa un hilo que aumenta el contador segun la duración del ciclo
    public void startSystem() {
        Thread counterThread = new Thread(() -> {
            long cycleCount = 0;
            while (true) {
                cycleCount++;
                //Update ventana comentado porque falta reimplementarlo correctamente
                //ventana.updateCycleCount(cycleCount);

                try {
                    //Obtener duracion de ciclo desde ventana comentado porque se busca obtenerlo de otro modo
                    //long duration = ventana.getCycleDuration();
                    Thread.sleep(cycleDuration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Hilo del contador interrumpido");
                    break;
                }
            }
        });
        counterThread.setDaemon(true); 
        counterThread.start();
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
    
    private void runProcess() {
        
    }
    
    private void manageSchedule() {
        
    }
    
    private void manageMemory() {
        
    }
    
}
