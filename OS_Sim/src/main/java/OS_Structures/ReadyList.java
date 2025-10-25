/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;
import Structures.*;
import java.util.Iterator;
/**
 *
 * @author Miguel
 */
public class ReadyList implements Iterable {

    private ArrayList<ProcessNode> heap;
    private int size;
    private int maxSize;
    private boolean isMin;
    private Schedule schedule = Schedule.PRIORITY;
    
    public ReadyList(int maxSize, boolean isMin) {
        this.maxSize = maxSize;
        this.size = 0;
        this.heap = new ArrayList(this.maxSize);
        this.isMin = isMin;
    }
    
    public ReadyList(int maxSize) {
        this(maxSize, true);
    }

    public ReadyList() {
        this(20);
    }
    
    public ArrayList<ProcessNode> getHeap() {
        return heap;
    }

    private void setHeap(ArrayList<ProcessNode> heap) {
        this.heap = heap;
    }
    
    public int getSize() {
        return size;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isMinHeap() {
        return this.isMin;
    }
    
    public ProcessNode peekRootNode() {
        return getHeap().getElmenetAtIndex(0);
    }
    
    public OS_Process peekRoot() {
        return peekRootNode().getElement();
    }
    
    public boolean isEmpty() {
        return getSize() == 0;
    }
    
    private int parent(int index) {
        return (index - 1) / 2;
    }
    
    private int leftChild(int index) {
        return 2 * index + 1;
    }
    
    private int rightChild(int index) {
        return 2 * index + 2;
    }
    
    private void swap(int pIndex, int cIndex) {
        ArrayNode prev, parent, child, prevCh;
        prev = parent = child = prevCh = null;
        int count = 0;
        Integer pointer = getHeap().getHead();
        while (count != cIndex) {
            if (pIndex != 0 && count == pIndex - 1) {prev = getHeap().getArray()[pointer];}
            if (count == cIndex-1) {prevCh = getHeap().getArray()[pointer];}
            if (count == pIndex) {parent = getHeap().getArray()[pointer];}
            pointer = getHeap().getArray()[pointer].getNext();
            count++;
        }
        child = getHeap().getArray()[pointer];
        pointer = child.getNext();
        Integer pointer2;
        if (pIndex == 0) {
            if (cIndex == 1) {
                child.setNext(getHeap().getHead());
                getHeap().setHead(parent.getNext());
            } else {
                child.setNext(parent.getNext());
                pointer2 = prevCh.getNext();
                prevCh.setNext(getHeap().getHead());
                getHeap().setHead(pointer2);
            }
        } else {
            pointer2 = prev.getNext();
            prev.setNext(prevCh.getNext());
            child.setNext(parent.getNext());
            prevCh.setNext(pointer2);
        }
        parent.setNext(pointer);
    }
    
    public void Heapify(int index) {
        if (isMinHeap()) {
            minHeapify(index);
            return;
        }
        maxHeapify(index);
    }
    
    private void maxHeapify(int index) {
        int lChild = leftChild(index);
        int rChild = rightChild(index);
        
        int smallest = index;
        
        if (lChild < getSize() && getHeap().getElmenetAtIndex(lChild).getPriority() > getHeap().getElmenetAtIndex(smallest).getPriority()) {
            smallest = lChild;
        } if (rChild < getSize() && getHeap().getElmenetAtIndex(rChild).getPriority() > getHeap().getElmenetAtIndex(smallest).getPriority()) {
            smallest = rChild;
        }
        
        if (smallest != index) {
            swap(index, smallest);
            maxHeapify(smallest);
        }
    }
    
    private void minHeapify(int index) {
        int lChild = leftChild(index);
        int rChild = rightChild(index);
        
        int smallest = index;
        
        if (lChild < getSize() && getHeap().getElmenetAtIndex(lChild).getPriority() < getHeap().getElmenetAtIndex(smallest).getPriority()) {
            smallest = lChild;
        } if (rChild < getSize() && getHeap().getElmenetAtIndex(rChild).getPriority() < getHeap().getElmenetAtIndex(smallest).getPriority()) {
            smallest = rChild;
        }
        
        if (smallest != index) {
            swap(index, smallest);
            minHeapify(smallest);
        }
    }
    
    protected void insertedMin(int current) {
        while (current != 0 && getHeap().getElmenetAtIndex(current).getPriority() < getHeap().getElmenetAtIndex(parent(current)).getPriority()) {
            swap(parent(current), current);
            current = parent(current);
        }
    }
    
    protected void insertedMax(int current) {
        while (current != 0 && getHeap().getElmenetAtIndex(current).getPriority() > getHeap().getElmenetAtIndex(parent(current)).getPriority()) {
            swap(parent(current), current);
            current = parent(current);
        }
    }
    
    protected void insertNode(ProcessNode node) {
        if (getSize() == getMaxSize()) {
            ArrayList<ProcessNode> newHeap = new ArrayList(getMaxSize() + 5);
            for (int i = 0; i < getSize(); i++) {
                newHeap.insertAtIndex(getHeap().getElmenetAtIndex(i), i);
            }
            setHeap(newHeap);
            this.maxSize = this.maxSize + 5;
        }
        int current = size;
        getHeap().insertFinal(node);
        size++;
        switch (getScheduleType()) {
            case Schedule.SHORTEST_NEXT, Schedule.SHORTEST_REMAINING_TIME, Schedule.FEEDBACK, Schedule.PRIORITY -> insertedMin(current);
            case Schedule.FIFO, Schedule.ROUND_ROBIN, Schedule.HIGHEST_RESPONSE_RATIO -> insertedMax(current);
        }
    }
    
    public OS_Process extractRoot() {
        ProcessNode root = extractRootNode();
        if (root==null) {
            return null;
        }
        return root.getElement();
    }
    
    protected ProcessNode deleteRootIfExists() {
        if (isEmpty()) {
            System.out.println("The Heap is Empty");
            return null;
        }
        ProcessNode root = getHeap().deleteBegin();
        size--;
        return root;
    }
    
    protected void fixHeap() {
        if (size != 0) {
            getHeap().insertBegin(getHeap().getElmenetAtIndex(size - 1));
            getHeap().deleteFinal();
            Heapify(0);
        }
    }
    
    public ProcessNode extractRootNode() {
        ProcessNode root = deleteRootIfExists();
        if (getScheduleType() != Schedule.ROUND_ROBIN) {fixHeap();}
        return root;
    }
    
    public void reloadHeap() {
        ArrayList<ProcessNode> heapCopy = new ArrayList(getMaxSize());
        for (int i = 0; i < getSize(); i++) {
            heapCopy.insertAtIndex(getHeap().getElmenetAtIndex(i), i);
        }
        setHeap(new ArrayList(getMaxSize()));
        size=0;
        for (ProcessNode i:heapCopy) {
            insertNode(i);
        }
    }
    
    public void inverseHeap() {
        this.isMin = !isMinHeap();
        reloadHeap();
    }
        
    public void print() {
        getHeap().print();
    }
 
    public void printInMemory() {
        getHeap().printInMemory();
    }
    
    @Override
    public Iterator iterator() {
        return new HeapIterator(getHeap());
    }
    
    protected ProcessNode createNode(OS_Process element) {
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
                case Schedule.FIFO, Schedule.ROUND_ROBIN, Schedule.HIGHEST_RESPONSE_RATIO -> this.isMin = false;
            }
            reloadHeap();
        }
    }
    
    public void insert(OS_Process process) {
        insertNode(createNode(process));
    }
    
    public long peekTopPriority() {
        return peekRootNode().getPriority();
    }
    
    public ProcessNode peekLastNode() {
        return getHeap().getElmenetAtIndex(size-1);
    }
    
    public OS_Process peekLast() {
        return peekLastNode().getElement();
    }
    
    public long peekLastPriority() {
        return peekLastNode().getPriority();
    }
}

class HeapIterator implements Iterator {
    
    ArrayNode<ProcessNode>[] array;
    Integer pointer;

    public HeapIterator(ArrayList array) {
        pointer = array.getHead();
        this.array = array.getArray();
    }
    
    @Override
    public boolean hasNext() {
        return pointer != null;
    }

    @Override
    public ProcessNode next() {
        ArrayNode<ProcessNode> current = array[pointer];
        pointer = current.getNext();
        return current.getElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}