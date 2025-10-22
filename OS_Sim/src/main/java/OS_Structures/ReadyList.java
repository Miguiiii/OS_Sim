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

    private Schedule schedule = Schedule.PRIORITY;
    
    public ReadyList(int maxSize) {
        super(maxSize);
        this.isMin = true;
    }

    public ReadyList() {
        this(20);
    }
    
    @Override
    protected HeapNode<OS_Process> createNode(OS_Process element, long priority) {
        return new ProcessNode(element);
    }
    
    public Schedule getScheduleType() {
        return this.schedule;
    }
    
    private void setScheduleType(Schedule type) {
        this.schedule = type;
    }
    
    public void switchSchedule(Schedule schedule) {
        Schedule prev_sched = getScheduleType();
        if (prev_sched != schedule) {
            setScheduleType(schedule);
            ProcessNode.schedule=schedule;
            switch (schedule) {
                case Schedule.PRIORITY, Schedule.SHORTEST_NEXT, Schedule.SHORTEST_REMAINING_TIME, Schedule.FEEDBACK -> this.isMin = true;
                case Schedule.FIFO, Schedule.HIGHEST_RESPONSE_RATIO -> this.isMin = false;
                case Schedule.ROUND_ROBIN -> {return;}
            }
            reloadHeap();
        }
    }
    
    public void insert(OS_Process process) {
        int current = insertNode(createNode(process, 0));
        switch (getScheduleType()) {
            case Schedule.ROUND_ROBIN -> {return;}
            case Schedule.SHORTEST_NEXT, Schedule.SHORTEST_REMAINING_TIME, Schedule.FEEDBACK, Schedule.PRIORITY -> insertedMin(current);
            case Schedule.FIFO, Schedule.HIGHEST_RESPONSE_RATIO -> insertedMax(current);
        }
    }
    
    @Override
    public HeapNode<OS_Process> extractRootNode() {
        HeapNode<OS_Process> root = deleteRootIfExists();
        if (getScheduleType() != Schedule.ROUND_ROBIN) {fixHeap();}
        return root;
    }
    
}
