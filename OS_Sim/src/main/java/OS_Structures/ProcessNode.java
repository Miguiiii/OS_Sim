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
public class ProcessNode implements HeapNode<OS_Process>{

    private OS_Process element;
    public static Schedule schedule = Schedule.PRIORITY;
    
    //private long cicleCounter=561132421; //PLACEHOLDER FOR THE GLOBAL CICLECOUNTER FOR TESTS PURPOSES
    
    public ProcessNode(OS_Process element) {
        this.element = element;
    }

    @Override
    public OS_Process getElement() {
        return element;
    }

    @Override
    public long getPriority() {
        return switch (getPriorityType()) {
            case Schedule.FIFO -> getElement().getTimeInSystem(OperatingSystem.cycleCounter);
            case Schedule.SHORTEST_NEXT -> getElement().getMaxRunTime();
            case Schedule.SHORTEST_REMAINING_TIME -> getElement().getMaxRunTime()-getElement().getProgram_counter();
            case Schedule.HIGHEST_RESPONSE_RATIO -> (getElement().getTimeInSystem(OperatingSystem.cycleCounter)/getElement().getMaxRunTime())-1;
            case Schedule.FEEDBACK -> getElement().getProgram_counter()/getElement().getMaxRunTime();
            case Schedule.ROUND_ROBIN -> 0;
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
