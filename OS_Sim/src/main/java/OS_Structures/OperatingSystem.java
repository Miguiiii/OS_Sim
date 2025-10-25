/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;
import Structures.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
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
    public static volatile long cycleCounter = 0;
    public volatile long programCounter = 0;
    
    private volatile Schedule schedule = Schedule.PRIORITY;
    private volatile long quantum = 1;
    private volatile Schedule changedSched = Schedule.PRIORITY;
    private volatile long changedQuant = 1;
    private volatile int processIdCounter = 0; 
    
    private volatile boolean isInKernel = true;
    private long memorySpace;
    private volatile long memoryFree; 
    
    private ReadyList readyProcesses;
    private ReadyList readySuspendedProcesses;
    private HashMap<Integer, OS_Process> blockedProcesses;
    private HashMap<Integer, OS_Process> blockedSuspendedProcesses;
    private List<OS_Process> newProcesses;
    private List<OS_Process> exitProcesses;
    
    private volatile OS_Process runningProcess;
    private GUI ventana;
    private Semaphore readySem;
    private Semaphore blockedSem;
    private Semaphore newSem;
    
    private class ExceptionRun implements Runnable {

        private OS_Process process;
        private long pileIO;
        
        public ExceptionRun(OS_Process process) {
            this.process = process;
            this.process.setState(Status.BLOCKED);
            this.pileIO = process.getCyclesToCompleteException();
        }
        
        @Override
        public void run() {
            try {
                blockedSem.acquire();
            } catch (InterruptedException ex) {
                System.getLogger(OperatingSystem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
 
            blockedProcesses.put(this.process.getId(), this.process);
            blockedSem.release();
            
            Thread IO = new Thread(()->{
            try {
                for (; this.pileIO>0; this.pileIO--) {
                    Thread.sleep(getCycleDuration());
                }
            } catch (InterruptedException e) {
                ventana.addLogMessage("---> Proceso interrumpido");
            }
        });
            IO.setDaemon(true);
            IO.start();
            try {
                IO.join();
            } catch (InterruptedException ex) {
                System.getLogger(OperatingSystem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            this.process.runInstruction();
            try {
                blockedSem.acquire();
            } catch (InterruptedException ex) {
                System.getLogger(OperatingSystem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            
            boolean isInMemory = true;
            OS_Process p = blockedProcesses.deleteEntry(this.process.getId());
            
            if (p == null){ 
                isInMemory = false;
                OS_Process p2 = blockedSuspendedProcesses.deleteEntry(this.process.getId());
            }
            
            blockedSem.release();
            try {
                readySem.acquire();
            } catch (InterruptedException ex) {
                System.getLogger(OperatingSystem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            if (isInMemory) {
                process.setState(Status.READY);
                readyProcesses.insert(process);
                readySem.release();
                return;
            }
            process.setState(Status.READY_SUSPENDED);
            readySuspendedProcesses.insert(process);
            
            readySem.release(); 
        }
        
    }
    
    private cvs_manager_configuracion configManager;
    private Thread counterThread;
    
    public OperatingSystem() {
        this.isCycleInSeconds = true;
        this.cycleDuration = 1000; 
        this.readyProcesses = new ReadyList();
        this.readySuspendedProcesses = new ReadyList();
        this.blockedProcesses = new HashMap(20);
        this.blockedSuspendedProcesses = new HashMap(20);
        this.newProcesses = new List();
        this.exitProcesses = new List();
        this.runningProcess = null;
        this.readySem = new Semaphore(1);
        this.blockedSem = new Semaphore(1);
        this.newSem = new Semaphore(1);
        this.configManager = new cvs_manager_configuracion();
        this.memorySpace = this.memoryFree = 1000000;
    }

    public void boot() {
        Inicio inicioDialog = new Inicio(null, true);
        inicioDialog.setLocationRelativeTo(null);
        inicioDialog.setVisible(true); 

        if (inicioDialog.isStarted()) {
            setMemorySpace(inicioDialog.getMemorySpace());
            this.memoryFree = this.memorySpace; 
            setCycleDuration(inicioDialog.getCycleDuration(), inicioDialog.getUnit());
            
            String initialScheduleStr = inicioDialog.getSchedule(); 
            Schedule initialScheduleEnum; 
            
            if ("Priority".equalsIgnoreCase(initialScheduleStr)) {
                initialScheduleEnum = Schedule.PRIORITY;
            } else if ("Round Robin".equalsIgnoreCase(initialScheduleStr)) {
                initialScheduleEnum = Schedule.ROUND_ROBIN;
            } else if ("SRT".equalsIgnoreCase(initialScheduleStr)) {
                initialScheduleEnum = Schedule.SHORTEST_REMAINING_TIME;
            } else if ("Feedback".equalsIgnoreCase(initialScheduleStr)) {
                initialScheduleEnum = Schedule.FEEDBACK;
            } else {
                initialScheduleEnum = Schedule.PRIORITY; 
            }
            this.setScheduleType(initialScheduleEnum); 
    
            Thread mainThread = new Thread(() -> {
                try {
                    while (true) {
                        manageSchedule();
                        Thread.sleep(10); 
                    }
                } catch (InterruptedException ex) {
                    System.getLogger(OperatingSystem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            });
            mainThread.setDaemon(true);
            
            this.ventana = new GUI();
            this.ventana.setOperatingSystem(this); 
            
            this.startSystem();
            mainThread.start();
            
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
                    
                    if (!this.isInKernel && programCounter > 0) {
                        programCounter--;                    
                        OS_Process tempProcess = runningProcess; 
                        if (tempProcess != null) {
                            tempProcess.runInstruction(); 
                        }
                    }

                } catch (InterruptedException e) {
                    ventana.addLogMessage("---> Ciclo interrumpido para aplicar nueva duración.");
                }
            }
        });
        this.counterThread.setDaemon(true); 
        this.counterThread.start();
        ventana.setVisible(true);
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
        ventana.setInitialSchedule(this.schedule.toString());
    }

    public long getMemorySpace() {
        return memorySpace;
    }
    
    public long getMemoryFree() {
        return memoryFree;
    }

    public void setMemorySpace(long memorySpace) {
        this.memorySpace = memorySpace;
    }
    
    public void setSchedule(Schedule newSchedule) {
        this.changedSched = newSchedule;
    }
    
    public void setQuantum(long newQuantum) {
        this.changedQuant = newQuantum;
    }

    public void switchSchedule(Schedule schedule, long quantum) {
        changedSched = schedule;
        changedQuant = quantum;
    }
    
    public void switchSchedule(Schedule schedule) {
        switchSchedule(schedule, getQuantum());
    }
    
    private void setScheduleType(Schedule schedule) {
        this.schedule = schedule;
        this.changedSched = schedule;
    }
    
    public Schedule getScheduleType() {
        return this.schedule;
    }
    
    public long getQuantum() {
        return this.quantum;
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

        configManager.guardarConfiguracion(fileName, getScheduleType().toString(), getMemorySpace(), durationValue, unit);
    }
    
    
    public int getProcessIdCounter() {
        return this.processIdCounter;
    }

    public void createNewProcess(String name, int priority, long pile, long cyclesToCallException, long cyclesToCompleteException) {
        try {
            int id = this.processIdCounter++;
            long birthTime = getCounter();

            OS_Process newProcess = new OS_Process(
                    name, 
                    id, 
                    priority, 
                    pile, 
                    birthTime, 
                    cyclesToCallException, 
                    cyclesToCompleteException
            );

            this.newSem.acquire();
            this.newProcesses.insertFinal(newProcess);
            this.newSem.release();

             if (ventana != null) {
                ventana.addLogMessage("PROCESO CREADO: " + newProcess.getName() + 
                                      " [ID: " + newProcess.getId() + 
                                      ", Prio: " + newProcess.getPriority() + 
                                      ", Pila: " + newProcess.getPile() + " inst." +
                                      ", I/O: " + (newProcess.isIOBound() ? "Si" : "No") + 
                                      "] en Ciclo " + newProcess.getBirthTime());
            }

        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Error al adquirir semáforo en createNewProcess", e);
            Thread.currentThread().interrupt(); 
        }
    }


    /**
     * Espera mientras se ejecuta el proceso y permite preemption.
     * @param currentlyRunning El proceso que está en la CPU.
     */
    private void runPreemptive(OS_Process currentlyRunning) {
        while (programCounter > 0) {
            OS_Process bestInReady = null;
            try {
 
                readySem.acquire();
                if (!readyProcesses.isEmpty()) {
                    bestInReady = readyProcesses.peekRoot();
                }
                readySem.release();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            if (bestInReady == null) {
                try { Thread.sleep(1); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                continue;
            }

            if (bestInReady.getId() == currentlyRunning.getId()) {
                 try { Thread.sleep(1); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                continue; 
            }

            boolean preempt = false;
            
            switch (this.schedule) { 
                case PRIORITY:
                case FEEDBACK: 
                    if (bestInReady.getPriority() < currentlyRunning.getPriority()) {
                        preempt = true;
                    }
                    break;
                    
                case SHORTEST_REMAINING_TIME:
                    long remainingTimeReady = bestInReady.getPile() - bestInReady.getMAR();
                    long remainingTimeRunning = currentlyRunning.getPile() - currentlyRunning.getMAR();
                    
                    if (remainingTimeReady < remainingTimeRunning) {
                        preempt = true;
                    }

                    break;
                    
                case ROUND_ROBIN:

                    preempt = false;
                    break;
                    
                default:

                    preempt = false;
                    break;
            }

            if (preempt) {
                if (ventana != null) {
                    ventana.addLogMessage("--> Proceso " + currentlyRunning.getId() + " PREEMPTED por Proceso " + bestInReady.getId() + " (Mejor prioridad/SRT)");
                }
                break; 
            }
 
            try {
                Thread.sleep(1); 
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    
    private void runNonPreemptive() {
        while (programCounter > 0) {
            try {
                Thread.sleep(1); 
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void runProcess(OS_Process process) {
        runningProcess = process;
        if (ventana != null) {ventana.updateRunningProcess(process);}
        
        long runTime = process.getPile()-process.getMAR();
        if (process.isIOBound()) {
            runTime = Math.min(runTime, process.getCyclesToCallException()-process.getMAR()%process.getCyclesToCallException());
        }
        
        Schedule currentSchedule = this.schedule; 
        long currentQuantum = this.quantum;
        
        long quantumToUse = switch (currentSchedule) {
            case Schedule.FEEDBACK -> (long) Math.pow(2, process.getTimesPreempted()); 
            default -> currentQuantum;
        };
        
        switch (currentSchedule) {
            case Schedule.ROUND_ROBIN, Schedule.FEEDBACK -> runTime = Math.min(runTime, quantumToUse);
            default -> {}
        }
        
        this.programCounter = runTime;
        isInKernel = false;
        
        boolean isPreemptive = false;
        switch (currentSchedule) { 
            case SHORTEST_REMAINING_TIME:
            case ROUND_ROBIN:
            case FEEDBACK:
            case PRIORITY: 
                isPreemptive = true;
                break;
            case FIFO:
            case SHORTEST_NEXT:
            case HIGHEST_RESPONSE_RATIO:
                isPreemptive = false;
                break;
        }

        if (isPreemptive) {
            runPreemptive(process); 
        } else {
            runNonPreemptive();
        }
        

        isInKernel = true;
        runningProcess = null; 
        
        if (ventana != null) {
            ventana.updateRunningProcess(null);
        }
        
        programCounter = 0;
        if (process.getMAR() == process.getPile()) {
            endProcess(process);
            return;
        }
        if (process.isIOBound()) {
            if (process.getMAR()%process.getCyclesToCallException()==0 && process.getMAR()!=0) {
                startProcessIO(process);
                return;
            }
        }
        process.preempted();
        try {
            this.readySem.acquire();
        } catch (InterruptedException ex) {
            System.getLogger(OperatingSystem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        this.readyProcesses.insert(process);
        this.readySem.release();
    }
    
    private void endProcess(OS_Process process) {
        process.setState(Status.EXIT);
        process.setTotalTime(OperatingSystem.cycleCounter);
        
        exitProcesses.insertFinal(process);
        
        this.memoryFree += process.getPile();
        if (ventana != null) {
            ventana.addLogMessage("--> Proceso " + process.getId() + " finalizado. Memoria liberada: " + process.getPile() + " KB. Total libre: " + this.memoryFree + " KB.");
        }
    }
    
    private void startProcessIO(OS_Process process) {
        Thread IOThread = new Thread(new ExceptionRun(process));
        IOThread.setDaemon(true);
        IOThread.start();
    }
    
    private void manageSchedule() throws InterruptedException {
        
        // 1. Aplicar cambios de Planificación
        if (this.schedule != this.changedSched) {
            this.schedule = this.changedSched;
            this.readyProcesses.switchSchedule(this.schedule);
            this.readySuspendedProcesses.switchSchedule(this.schedule);
            ventana.addLogMessage("--> Planificador cambiado a: " + this.schedule);
        }
        
        if (this.quantum != this.changedQuant) {
            this.quantum = this.changedQuant;
             ventana.addLogMessage("--> Quantum cambiado a: " + this.quantum);
        }
        
        
        // 2. Ejecutar un proceso si es posible
        if (!readyProcesses.isEmpty()) {
            this.readySem.acquire();
            OS_Process process = readyProcesses.extractRoot();
            this.readySem.release();
            process.setState(Status.RUNNING);
            runProcess(process);
        }
        
        // 3. Admitir nuevos procesos (New -> Ready-Suspended)
        this.readySem.acquire(); 
        this.newSem.acquire();
        for (OS_Process p:this.newProcesses) {
            p.setState(Status.READY_SUSPENDED); 
            this.readySuspendedProcesses.insert(p);
        }
        this.newProcesses = new List();
        this.newSem.release();
        
        // 4. Admitir procesos en memoria (Ready-Suspended -> Ready)
        while (!this.readySuspendedProcesses.isEmpty()) {
            if (this.readySuspendedProcesses.peekRoot().getPile() > this.memoryFree) {
                break;
            }
            ProcessNode Pnode = this.readySuspendedProcesses.extractRootNode();
            memoryFree -= Pnode.getElement().getPile();
            Pnode.getElement().setState(Status.READY);
            this.readyProcesses.insertNode(Pnode);
            ventana.addLogMessage("--> Proceso " + Pnode.getElement().getId() + " admitido en memoria. Memoria libre: " + memoryFree + " KB.");
        }        
        // 5. Lógica de Swapping (Blocked -> Blocked-Suspended)
        this.blockedSem.acquire();
        
        List<Integer> keys = this.blockedProcesses.getKeys();
        for (Integer k:keys) {
            if (this.readySuspendedProcesses.isEmpty() || this.blockedProcesses.isEmpty()) {
                break;
            }
            OS_Process p = this.blockedProcesses.deleteEntry(k);
            p.setState(Status.BLOCKED_SUSPENDED);
            this.blockedSuspendedProcesses.put(k, p);
            memoryFree += p.getPile();
            if (this.readySuspendedProcesses.peekRoot().getPile() <= this.memoryFree) {
                ProcessNode Pnode = this.readySuspendedProcesses.extractRootNode();
                memoryFree -= Pnode.getElement().getPile();
                Pnode.getElement().setState(Status.READY);
                this.readyProcesses.insertNode(Pnode);
                ventana.addLogMessage("--> Proceso " + Pnode.getElement().getId() + " admitido en memoria. Memoria libre: " + memoryFree + " KB.");
            }
        }

        this.blockedSem.release();
        this.readySem.release();
        
        // 6. Refrescar la GUI
        if (ventana != null) {
            ventana.refreshAllQueues();
        }
    }


    
    public ReadyList getReadyProcesses() { return this.readyProcesses; }
    public ReadyList getReadySuspendedProcesses() { return this.readySuspendedProcesses; }
    public HashMap<Integer, OS_Process> getBlockedProcesses() { return this.blockedProcesses; }
    public HashMap<Integer, OS_Process> getBlockedSuspendedProcesses() { return this.blockedSuspendedProcesses; }
    public List<OS_Process> getNewProcesses() { return this.newProcesses; }
    public List<OS_Process> getExitProcesses() { return this.exitProcesses; }
    public Semaphore getReadySem() { return this.readySem; }
    public Semaphore getBlockedSem() { return this.blockedSem; }
    public Semaphore getNewSem() { return this.newSem; }
    
}
