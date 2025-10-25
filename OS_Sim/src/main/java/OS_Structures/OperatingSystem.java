/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;
import Structures.*;
import java.time.Duration;
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
    public static long cycleCounter = 0;
    public volatile long programCounter = 0;
    private Schedule schedule = Schedule.PRIORITY;
    private long quantum = 1;
    private Schedule changedSched = Schedule.PRIORITY;
    private long changedQuant = 1;
    private boolean isInKernel = true;
    private long memorySpace;
    private long memoryFree;
    private ReadyList readyProcesses;
    private ReadyList readySuspendedProcesses;
    private HashMap<Integer, OS_Process> blockedProcesses;
    private HashMap<Integer, OS_Process> blockedSuspendedProcesses;
    private List<OS_Process> newProcesses;
    private OS_Process runningProcess;
    private List<OS_Process> exitProcesses;
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
                // Modificado: Enviar a la GUI
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
            if (p==null){
                isInMemory=false;
                blockedSuspendedProcesses.deleteEntry(this.process.getId());
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
                return;
            }
            process.setState(Status.READY_SUSPENDED);
            readySuspendedProcesses.insert(process);
        }
        
    }
    
    // Campo para mantener una referencia al hilo
    private cvs_manager_configuracion configManager;
    private Thread counterThread;
    
    public OperatingSystem() {
        this.isCycleInSeconds = true;
        this.cycleDuration = 1000; 
        this.ventana = new GUI();
        this.readyProcesses = new ReadyList();
        this.readySuspendedProcesses = new ReadyList();
        this.blockedProcesses = new HashMap(20);
        this.blockedSuspendedProcesses = new HashMap(20);
        this.newProcesses = new List();
        this.runningProcess = null;
        this.exitProcesses = new List();
        this.readySem = new Semaphore(1);
        this.blockedSem = new Semaphore(1);
        this.newSem = new Semaphore(1);
        this.ventana.setOperatingSystem(this); 
        this.configManager = new cvs_manager_configuracion();
        this.memorySpace = this.memoryFree = 1000000;
    }

    public void boot() {
        Inicio inicioDialog = new Inicio(null, true);
        inicioDialog.setLocationRelativeTo(null);
        inicioDialog.setVisible(true); 

        if (inicioDialog.isStarted()) {
            setMemorySpace(inicioDialog.getMemorySpace());
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
                    }
                } catch (InterruptedException ex) {
                    System.getLogger(OperatingSystem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
                
            });
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
                    if (!this.isInKernel && programCounter>0) {
                        programCounter--;
                        runningProcess.runInstruction(); //Esto regresa el objeto del proceso, pasar esto a la función de actualización de la GUI
                    }

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
        ventana.setInitialSchedule(this.schedule.toString());
    }

    public long getMemorySpace() {
        return memorySpace;
    }

    public void setMemorySpace(long memorySpace) {
        this.memorySpace = memorySpace;
    }
    
    public void setQuantum(long q) {
        this.quantum = q;
    }
    
    public long getQuantum() {
        return quantum;
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
    }
    
    public Schedule getScheduleType() {
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

        configManager.guardarConfiguracion(fileName, getScheduleType().toString(), getMemorySpace(), durationValue, unit);
    }
    
    public void createNewProcess(String name, long maxRunTime, long pile, int priority) {

            long birthTime = getCounter();

            // --- REQUERIMIENTO 2: Cambiar nombre ---
            // El nombre ahora incluye el tiempo de creación (ciclo)
            String finalName = name + " (T" + birthTime + ")";

            // --- REQUERIMIENTO 1: ID no negativo ---
            int id = (finalName + birthTime).hashCode(); // Usar finalName para el hash
            id = Math.abs(id); // Asegurar que el ID sea positivo

            // --- CORRECCIÓN 4 (previa) ---
            // Se agregó el argumento pileIO (como 0) para coincidir con el constructor de 7 argumentos
            OS_Process newProcess = new OS_Process(finalName, id, priority, birthTime, maxRunTime, pile, 0); // Usar finalName

            this.newProcesses.insertFinal(newProcess); 

            if (ventana != null) {
                // --- CORRECCIÓN DE ESTA SOLICITUD ---
                // Se cambió "Mem: ... KB" por "Pila: ... inst." para reflejar que 'pile' son instrucciones.
                ventana.addLogMessage("PROCESO CREADO: " + newProcess.getName() + 
                                      " [ID: " + newProcess.getId() + 
                                      ", Prio: " + newProcess.getPriority() + 
                                      ", T.Max: " + newProcess.getMaxRunTime() + "ms" +
                                      ", Pila: " + newProcess.getPile() + " inst.]" +
                                      " en Ciclo " + newProcess.getBirthTime());

                ventana.addNewProcessToView(newProcess);
            }
        }

    private void runPreemptive() {
        OS_Process top = this.readyProcesses.peekRoot();
        while (programCounter!=0 && this.readyProcesses.peekRoot()!=top) {}
    }
    
    private void runNonPreemptive() {
        while (programCounter!=0) {}
    }

    private void runProcess(OS_Process process) {
        runningProcess = process;
        long runTime = process.getPile()-process.getMAR();
        if (process.isIOBound()) {
            runTime = Math.min(runTime, process.getCyclesToCallException()-process.getMAR()%process.getCyclesToCallException());
        }
        long quantum = switch (getScheduleType()) {
            case Schedule.FEEDBACK -> Math.powExact(2, process.getTimesPreempted());
            default -> this.quantum;
        };
        switch (getScheduleType()) {
            case Schedule.ROUND_ROBIN, Schedule.FEEDBACK -> runTime = Math.min(runTime, quantum);
            default -> {}
        }
        this.programCounter = runTime;
        isInKernel = false;
        switch (getScheduleType()) {
            case Schedule.SHORTEST_REMAINING_TIME -> runPreemptive();
            default -> runNonPreemptive();
        }
        isInKernel = true;
        runningProcess = null;
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
        
//        long currentDuration = getCycleDuration();
//        int lastSizeReady = this.readyProcesses.getSize();
//        OS_Process p = process;
//        long maxRun =  p.getMaxRunTime();
//        long runTime = p.getPile()-p.getProgram_counter();
//        long runTime = Math.min(maxRun, runTime);
//        String currentSchedule = getScheduleType();
//        switch (currentSchedule) {
//            case "RR", "FeedBack" -> runTime = Math.min(runTime, this.quantum);
//            default -> {
//            }
//        }
//        //No quitar esto, que el thread no sirve si la variable no es final
//        final long runTime = runTime;
//        Thread running = new Thread(()->{
//            try {
//                Thread.sleep(runTime*this.cycleDuration);
//            } catch (InterruptedException e) {
//                // Modificado: Enviar a la GUI
//                ventana.addLogMessage("---> Proceso interrumpido");
//            }
//        });
//        running.setDaemon(true);
//        this.isInKernel = false;
//        long startCycle = getCounter();
//        running.start();
//        while (running.isAlive()) {
//            if (currentDuration == getCycleDuration() && currentSchedule.equals(getScheduleType()) && !("SRT".equals(currentSchedule) && lastSizeReady != this.readyProcesses.getSize())) {
//                continue;
//            }
//            //Poner aquí Logs dependiendo de la razon de interrupcion
//            
//            /*
//            DUDA: Al interrumpirse un proceso por cambio de duracion de ciclo o de planificacion
//            se deja que el proceso se interrumpe por completo y que se regrese a Listos
//            o que en esos dos casos se resuma su ejecucion pero ajustado a la nueva duracion del ciclo?????
//            */
//            long endCycle = getCounter();
//            running.interrupt();
//            long newRunTime = endCycle-startCycle;
//            if (newRunTime > runTime) {
//                break;
//            }
//            runTime = newRunTime;
//        }
//        
//        p.setProgram_counter(p.getProgram_counter()+runTime);
//        this.isInKernel = true;
//        return p;
    }
    
    private void endProcess(OS_Process process) {
        process.setState(Status.EXIT);
        process.setTotalTime(OperatingSystem.cycleCounter);
        exitProcesses.insertFinal(process);
        this.memoryFree+=process.getPile();        
    }
    
    private void startProcessIO(OS_Process process) {
        Thread IOThread = new Thread(new ExceptionRun(process));
        IOThread.setDaemon(true);
        IOThread.start();
    }
    
    //Manegar excepción con un Try-Catch al momento de llamar el thread con esta funcion
    private void manageSchedule() throws InterruptedException {
        if (!readyProcesses.isEmpty()) {
            this.readySem.acquire();
            OS_Process process = readyProcesses.extractRoot();
            this.readySem.release();
            process.setState(Status.RUNNING);
            runProcess(process);
        }
        this.readySem.acquire();
        this.readyProcesses.switchSchedule(changedSched);
        this.readySuspendedProcesses.switchSchedule(changedSched);
        this.quantum = this.changedQuant;
        this.newSem.acquire();
        for (OS_Process p:this.newProcesses) {
            p.setState(Status.READY);
            this.readySuspendedProcesses.insert(p);
        }
        this.newProcesses = new List();
        this.newSem.release();
        this.blockedSem.acquire();
        while (!this.readySuspendedProcesses.isEmpty()) {
            if (this.readySuspendedProcesses.peekRoot().getPile()>this.memoryFree) {
                break;
            }
            ProcessNode Pnode = this.readySuspendedProcesses.extractRootNode();
            memoryFree-=Pnode.getElement().getPile();
            Pnode.getElement().setState(Status.READY);
            this.readyProcesses.insertNode(Pnode);
        }
        List<Integer> keys = this.blockedProcesses.getKeys();
        while (true) {
            if (this.readySuspendedProcesses.isEmpty() || this.blockedProcesses.isEmpty()) {
                break;
            }
            
        }
    }

}
