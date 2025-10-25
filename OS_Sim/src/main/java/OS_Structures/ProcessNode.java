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
public class ProcessNode{

    private OS_Process element;
    public static Schedule schedule = Schedule.PRIORITY;
    private long cycleQueued;
    
    //private long cicleCounter=561132421; //PLACEHOLDER FOR THE GLOBAL CICLECOUNTER FOR TESTS PURPOSES
    
    public ProcessNode(OS_Process element) {
        this.element = element;
        this.cycleQueued = OperatingSystem.cycleCounter;
    }

    public OS_Process getElement() {
        return element;
    }

    public long getPriority() {
        return getPriority(getPriorityType());
    }
    
    public long getPriority(Schedule schedule) {
        return switch (schedule) {
            case Schedule.FIFO, Schedule.ROUND_ROBIN -> OperatingSystem.cycleCounter - this.cycleQueued;
            case Schedule.SHORTEST_NEXT -> getElement().getPile();
            case Schedule.SHORTEST_REMAINING_TIME -> getElement().getPile()-getElement().getMAR();
            case Schedule.HIGHEST_RESPONSE_RATIO -> ((OperatingSystem.cycleCounter - this.cycleQueued)/getElement().getPile())+1;
            case Schedule.FEEDBACK -> getElement().getTimesPreempted();
            case Schedule.PRIORITY -> getElement().getPriority();
            default -> getElement().getPriority();
        };
    }
    
    public Schedule getPriorityType() {
        return ProcessNode.schedule;
    }
    
    @Override
    public String toString() {
        return this.element.toString();
    }
    
}
