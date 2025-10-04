/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Structures;

/**
 *
 * @author Miguel
 * @param <T> Any Object
 */
public class BinaryHeap<T> {
    
    private ArrayList<BHNode<T>> heap;
    private int size;
    private int maxSize;
    private boolean isMin;

    public BinaryHeap(int maxSize, boolean isMin) {
        this.maxSize = maxSize;
        this.size = 0;
        this.heap = new ArrayList(this.maxSize);
        this.isMin = isMin;
    }
    
    public BinaryHeap(int maxSize){
        this(maxSize, true);
    }

    public ArrayList<BHNode<T>> getHeap() {
        return heap;
    }

    private void setHeap(ArrayList<BHNode<T>> heap) {
        this.heap = heap;
    }
    
    public int getSize() {
        return size;
    }

    public int getMaxSize() {
        return maxSize;
    }

//    public void setSize(int size) {
//        this.size = size;
//    }
    
    public boolean isMinHeap() {
        return this.isMin;
    }
    
    public BHNode<T> getRootNode() {
        return getHeap().getElmenetAtIndex(0);
    }
    
    public T getRoot() {
        return getRootNode().getElement();
    }
    
    public boolean isElementInHeap(T element) {
        for (int i = 0; i < getSize(); i++) {
            if (getHeap().getElmenetAtIndex(i).getElement() == element) {return true;}
        }
        return false;
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
        ArrayNode prev, parent, child;
        prev = parent = child = null;
        int count = 0;
        Integer pointer = getHeap().getHead();
        while (count != cIndex) {
            if (pIndex != 0 && count == pIndex - 1) {prev = getHeap().getArray()[pointer];}
            if (count == pIndex) {parent = getHeap().getArray()[pointer];}
            pointer = getHeap().getArray()[pointer].getNext();
            count++;
        }
        child = getHeap().getArray()[pointer];
        pointer = child.getNext();
        if (pIndex == 0) {
            child.setNext(getHeap().getHead());
            getHeap().setHead(parent.getNext());
        } else {
            child.setNext(prev.getNext());
            prev.setNext(parent.getNext());
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
    
    private void insertedMin(int current) {
        while (current != 0 && getHeap().getElmenetAtIndex(current).getPriority() < getHeap().getElmenetAtIndex(parent(current)).getPriority()) {
            swap(parent(current), current);
            current = parent(current);
        }
    }
    
    private void insertedMax(int current) {
        while (current != 0 && getHeap().getElmenetAtIndex(current).getPriority() > getHeap().getElmenetAtIndex(parent(current)).getPriority()) {
            swap(parent(current), current);
            current = parent(current);
        }
    }
    
    public void insertNode(BHNode<T> node) {
        if (getSize() == getMaxSize()) {
            ArrayList<BHNode<T>> newHeap = new ArrayList(getMaxSize() + 5);
            for (int i = 0; i < getSize(); i++) {
                newHeap.insertAtIndex(getHeap().getElmenetAtIndex(i), i);
            }
            setHeap(newHeap);
            this.maxSize = this.maxSize + 5;
        }
        int current = size;
        getHeap().insertFinal(node);
        size++;
        
        if (isMinHeap()) {
            insertedMin(current);
            return;
        }
        insertedMax(current);
    }
    
    private BHNode<T> createNode(T element, int priority) {
        return new BHNode(element, priority);
    }
    
    public void insert(T element, int priority) {
        insertNode(createNode(element, priority));
    }
    
    public T extractRoot() {
        return extractRootNode().getElement();
    }
    
    public BHNode<T> extractRootNode() {
        if (isEmpty()) {
            System.out.println("The Heap is Empty");
            return null;
        }
        BHNode<T> root = getHeap().deleteBegin();
        size--;
        if (size != 0) {
            getHeap().insertBegin(getHeap().getElmenetAtIndex(size - 1));
            getHeap().deleteFinal();
            Heapify(0);
        }
        return root;
    }
    
    public void inverseHeap() {
        this.isMin = !isMinHeap();
        ArrayList<BHNode<T>> heapCopy = new ArrayList(getMaxSize());
        for (int i = 0; i < getSize(); i++) {
            heapCopy.insertAtIndex(getHeap().getElmenetAtIndex(i), i);
        }
        setHeap(new ArrayList(getMaxSize()));
        for (BHNode<T> i:heapCopy) {
            insertNode(i);
        }
    }
    
    @Deprecated
    public T extractElement(BHNode extract) {
        if (isEmpty()) {
            System.out.println("The Heap is Empty");
            return null;
        }
        
        
        List<T> colaElementos = new List();
        List<Integer> colaPrioridades = new List();
        T eliminado = null;

        while (!getHeap().getElmenetAtIndex(0).equals(extract)) {
            colaElementos.insertFinal(getHeap().getElmenetAtIndex(0).getElement());
            colaPrioridades.insertFinal(getHeap().getElmenetAtIndex(0).getPriority());
            extractRoot();
            if (getSize() == 0) {break;}
        }
        if (getSize() != 0) {eliminado = extractRoot();}
        
        while (colaElementos.getLength() != 0) {
            T newElement = colaElementos.getElmenetAtIndex(0);
            int newPrioridad = colaPrioridades.getElmenetAtIndex(0);
            insert(newElement, newPrioridad);
            colaElementos.deleteBegin();
            colaPrioridades.deleteBegin();
        }
        if (eliminado == null) {
            System.out.println("Elemento no encontrado");
            size++;
        }
        size--;
        return eliminado;
    }
    
    public void print() {
        getHeap().print();
    }
 
    public void printInMemory() {
        getHeap().printInMemory();
    }
    
}
