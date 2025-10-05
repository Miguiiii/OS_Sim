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
    public static String priorityType = "Priority";
    
    private long cicleCounter=561132421; //PLACEHOLDER FOR THE GLOBAL CICLECOUNTER FOR TESTS PURPOSES
    
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
            case "FIFO" -> getElement().getTimeInSystem(cicleCounter);
            case "SN" -> getElement().getMaxRunTime();
            case "SRT" -> getElement().getMaxRunTime()-getElement().getProgram_counter();
            case "HRR" -> (getElement().getTimeInSystem(cicleCounter)/getElement().getMaxRunTime())-1;
            case "FeedBack" -> getElement().getProgram_counter()/getElement().getMaxRunTime();
            default -> getElement().getPriority();
        };
    }
    
    public String getPriorityType() {
        return ProcessNode.priorityType;
    }
    
    @Override
    public String toString() {
        return this.element.toString();
    }
    
}
