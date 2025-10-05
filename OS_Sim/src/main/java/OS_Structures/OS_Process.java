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
    private long program_counter=0;
    
    public OS_Process(String name, int id, long birthTime, long maxRunTime, long pile, int priority) {
        this.name = name;
        this.id = id;
        this.birthTime = birthTime;
        this.maxRunTime = maxRunTime;
        this.pile = pile;
        this.priority = priority;
    }
    
    public OS_Process(String name, int id, long birthTime, long maxRunTime, long pile) {
        this(name, id, birthTime, maxRunTime, pile, 100);
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
    
    public long getProgram_counter() {
        return program_counter;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
}
