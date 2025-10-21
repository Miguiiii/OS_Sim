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
    private final long birthTime;
    private final long maxRunTime;
    private long pile;
    private long pileIO;
    private long program_counter=0;
    private long totalRunTime=0;
    private String state;
    
    public OS_Process(String name, int id, long birthTime, long maxRunTime, long pile, long pileIO, int priority) {
        this.name = name;
        this.id = id;
        this.birthTime = birthTime;
        this.maxRunTime = maxRunTime;
        this.pile = pile;
        this.priority = priority;
        this.pileIO = pileIO;
    }
    
    public OS_Process(String name, int id, long birthTime, long maxRunTime, long pile) {
        this(name, id, birthTime, maxRunTime, pile, 0, 100);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public long getPriority() {
        return priority;
    }

    public long getBirthTime() {
        return birthTime;
    }

    public long getMaxRunTime() {
        return maxRunTime;
    }

    public long getTimeInSystem(long cicle) {
        return cicle-getBirthTime()-getProgram_counter();
    }
    
    public long getTimeInSystem() {
        return getTotalTime()-getProgram_counter();
    }
    
    public long getProgram_counter() {
        return program_counter;
    }

    public void setProgram_counter(long program_counter) {
        this.program_counter = program_counter;
    }
    
    public long getTotalTime() {
        return totalRunTime;
    }
    
    public long getPile(){
        return pile;
    }
    
    public long getPileIO() {
        return pileIO;
    }
    
    public String getState(){
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public void setTotalTime(long finalCycle) {
        if (totalRunTime!=0) {
            System.out.println("This process's total runtime has already been set");
            return;
        }
        this.totalRunTime = finalCycle-getBirthTime();
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
}
