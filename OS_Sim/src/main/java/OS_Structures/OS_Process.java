/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;

/**
 *
 * @author Miguel
 */
public class OS_Process {

    private String name;
    private final int id;
    private int priority;
    private final long pile;
    private final long birthTime;
    private long cyclesToCallException = 0;
    private long cyclesToCompleteException = 0;
    private long PC = 1;
    private long MAR = 0;
    private long totalTimeInOS = 0;
    private Status state = Status.NEW;
    private int timesPreempted = 0;
    
    /**
     * Constructor for an IO Bound Process
     * @param name Name of the Process
     * @param id Id of the Process
     * @param priority Priority  of the Process
     * @param pile Number of Instructios of the Process
     * @param birthTime Cycle number in which the Process was created
     * @param cyclesToCallException Number of Cycles for the Process to call for an IO operation
     * @param cyclesToCompleteException Number of Cycles for an IO operation of this process to be completed
     */
    public OS_Process(String name, int id, int priority, long pile, long birthTime, long cyclesToCallException, long cyclesToCompleteException) {
        this.name = name;
        this.id = id;
        this.priority = priority;
        this.pile = pile;
        this.birthTime = birthTime;
        this.cyclesToCallException = cyclesToCallException;
        this.cyclesToCompleteException = cyclesToCompleteException;
    }
    
    /**
     * Constructor for a CPU Bound Process
     * cyclesToCallException and cyclesToCompleteException are set to 0
     * @param name Name of the Process
     * @param id Id of the Process
     * @param priority Priority  of the Process
     * @param pile Number of Instructios of the Process
     * @param birthTime Cycle number in which the Process was created
     */
    public OS_Process(String name, int id, int priority, long pile, long birthTime) {
        this(name, id, priority, pile, birthTime, 0, 0);
    }

    /**
     * Returns the Name of the process given to it upon creation
     * @return {@code String} name of the process
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Id of the process given to it upon creation
     * @return {@code int} Id of the process
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the Priority of the process given to it upon creation
     * @return {@code int} prioriy of the process
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Returns the Cycle of the OS simulation in which this process was created
     * @return {@code long} cycle of birth of the process
     */
    public long getBirthTime() {
        return birthTime;
    }
    
    public long getTimeInSystem(long cicle) {
        return cicle-getBirthTime()-getPC();
    }
    
    public long getTimeInSystem() {
        return getTotalTime()-getPC();
    }
    
    public long getPC() {
        return PC;
    }

    public long getMAR() {
        return MAR;
    }

    public long getCyclesToCallException() {
        return cyclesToCallException;
    }

    public long getCyclesToCompleteException() {
        return cyclesToCompleteException;
    }   
    
    public int getTimesPreempted() {
        return timesPreempted;
    }
    
    public void preempted() {
        timesPreempted++;
    }
    
    public OS_Process runInstruction() {
        MAR++;
        PC++;
        return this;
    }
    
    public long getTotalTime() {
        return totalTimeInOS;
    }
    
    public long getPile(){
        return pile;
    }
    
    public boolean isCPUBound() {
        return this.cyclesToCallException==0;
    }
    
    public boolean isIOBound() {
        return !isCPUBound();
    }
    
    public Status getState(){
        return state;
    }
    
    public void setState(Status state) {
        this.state = state;
    }
    
    public void setTotalTime(long finalCycle) {
        if (totalTimeInOS!=0) {
            System.out.println("This process's total runtime has already been set");
            return;
        }
        this.totalTimeInOS = finalCycle-getBirthTime();
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
}
