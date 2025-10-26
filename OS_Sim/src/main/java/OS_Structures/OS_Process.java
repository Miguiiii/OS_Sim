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
    private volatile long PC = 1;
    private volatile long MAR = 0;
    private volatile Status state = Status.NEW;
    private long totalTimeInOS = 0;
    private int timesPreempted = 0;
    
    public OS_Process(String name, int id, int priority, long pile, long birthTime, long cyclesToCallException, long cyclesToCompleteException) {
        this.name = name;
        this.id = id;
        this.priority = priority;
        this.pile = pile;
        this.birthTime = birthTime;
        this.cyclesToCallException = cyclesToCallException;
        this.cyclesToCompleteException = cyclesToCompleteException;
    }
    
    public OS_Process(String name, int id, int priority, long pile, long birthTime) {
        this(name, id, priority, pile, birthTime, 0, 0);
    }


    public String getName() {
        return name;
    }

 
    public int getId() {
        return id;
    }


    public int getPriority() {
        return priority;
    }

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
        this.state = Status.READY;
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
