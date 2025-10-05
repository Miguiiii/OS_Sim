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
public class ReadyList extends Heap<OS_Process>{

    private String schedule = "Priority";
    
    public ReadyList(int maxSize) {
        super(maxSize);
        this.isMin = true;
    }

    @Override
    protected HeapNode<OS_Process> createNode(OS_Process element, long priority) {
        return new ProcessNode(element);
    }
    
    public String getScheduleType() {
        return this.schedule;
    }
    
    private void setScheduleType(String type) {
        this.schedule = type;
    }
    
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
            reloadHeap();
            switch (new_schedule) {
                case "Priority", "SN", "SRT", "FeedBack" -> this.isMin = true;
                case "FIFO", "HRR" -> this.isMin = false;
            }
        }
    }
    
    public void insert(OS_Process process) {
        int current = insertNode(createNode(process, 0));
        switch (getScheduleType()) {
            case "RR" -> {
            }
            case "SN", "SRT", "FeedBack", "Priority" -> insertedMin(current);
            case "FIFO", "HRR" -> insertedMax(current);
        }
    }
    
    @Override
    public HeapNode<OS_Process> extractRootNode() {
        HeapNode<OS_Process> root = deleteRootIfExists();
        switch (getScheduleType()) {
            case "Priority", "FIFO", "SN", "SRT", "HRR", "FeedBack" -> {
                fixHeap();
            }
            case "RR" -> {
            }
        }
        return root;
    }
    
}
