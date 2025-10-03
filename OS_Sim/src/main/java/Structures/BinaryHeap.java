/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Structures;

/**
 *
 * @author Miguel
 */
public class BinaryHeap<T> {
    
    private ArrayList<BHNode<T>> heap;
    private int size;
    private int maxSize;

    public BinaryHeap(int maxSize) {
        this.maxSize = maxSize;
        this.size = 0;
        this.heap = new ArrayList(this.maxSize);
    }

    public ArrayList<BHNode<T>> getHeap() {
        return heap;
    }

    public void setHeap(ArrayList<BHNode<T>> heap) {
        this.heap = heap;
    }
    
    public int getSize() {
        return size;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    public BHNode getMin() {
        return getHeap().getElmenetAtIndex(0);
    }
    
    public boolean containsElement(T element) {
        for (int i = 0; i < getSize(); i++) {
            if (getHeap().getElmenetAtIndex(i).getElement() == element) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isEmpty() {
        if (getSize() == 0) {
            return true;
        }
        return false;
    }
    
    public int parent(int index) {
        return (index - 1) / 2;
    }
    
    public int leftChild(int index) {
        return 2 * index + 1;
    }
    
    public int rightChild(int index) {
        return 2 * index + 2;
    }
    
    private void swap(int pIndex, int cIndex) {
        ArrayNode anterior, padre, hijo;
        anterior = padre = hijo = null;
        int cont = 0;
        Integer pointer = getHeap().getHead();
        while (cont != cIndex) {
            if (pIndex != 0 && cont == pIndex - 1) {
                anterior = getHeap().getArray()[pointer];
            } if (cont == pIndex) {
                padre = getHeap().getArray()[pointer];
            }
            pointer = getHeap().getArray()[pointer].getNext();
            cont++;
        }
        hijo = getHeap().getArray()[pointer];
        pointer = hijo.getNext();
        if (pIndex == 0) {
            hijo.setNext(getHeap().getHead());
            getHeap().setHead(padre.getNext());
        } else {
            hijo.setNext(anterior.getNext());
            anterior.setNext(padre.getNext());
        }
        padre.setNext(pointer);
    }
    
    public void minHeapify(int index) {
        int lChild = leftChild(index);
        int rChild = rightChild(index);
        
        int smallest = index;
        
        if (lChild < getSize() && getHeap().getElmenetAtIndex(lChild).getPrioridad() < getHeap().getElmenetAtIndex(smallest).getPrioridad()) {
            smallest = lChild;
        } if (rChild < getSize() && getHeap().getElmenetAtIndex(rChild).getPrioridad() < getHeap().getElmenetAtIndex(smallest).getPrioridad()) {
            smallest = rChild;
        }
        
        if (smallest != index) {
            swap(index, smallest);
            minHeapify(smallest);
        }
    }
    
    public void insert(T element, int prioridad) {
        if (getSize() == getMaxSize()) {
            ArrayList<BHNode<T>> newHeap = new ArrayList(getMaxSize() + 5);
            for (int i = 0; i < getSize(); i++) {
                newHeap.insertAtIndex(getHeap().getElmenetAtIndex(i), i);
            }
            setHeap(newHeap);
        }
        BHNode<T> nodo = new BHNode(element, prioridad);
        int current = size;
        getHeap().insertFinal(nodo);
        size++;
        
        while (current != 0 && getHeap().getElmenetAtIndex(current).getPrioridad() < getHeap().getElmenetAtIndex(parent(current)).getPrioridad()) {
            swap(parent(current), current);
            current = parent(current);
        }
    }
    
    public T extractMin() {
        if (isEmpty()) {
            System.out.println("El montículo está vacío");
            return null;
        }
        BHNode<T> min = getHeap().getElmenetAtIndex(0);
        getHeap().deleteBegin();
        size--;
        if (size != 0) {
            getHeap().insertBegin(getHeap().getElmenetAtIndex(size - 1));
            getHeap().deleteFinal();
            minHeapify(0);
        }
        return min.getElement();
    }
    
    public T extractElement(BHNode extract) {
        if (isEmpty()) {
            System.out.println("El monticulo esta vacio");
            return null;
        }
        
        
        List<T> colaElementos = new List();
        List<Integer> colaPrioridades = new List();
        T eliminado = null;

        while (!getHeap().getElmenetAtIndex(0).equals(extract)) {
            colaElementos.insertFinal(getHeap().getElmenetAtIndex(0).getElement());
            colaPrioridades.insertFinal(getHeap().getElmenetAtIndex(0).getPrioridad());
            extractMin();
            if (getSize() == 0) {break;}
        }
        if (getSize() != 0) {eliminado = extractMin();}
        
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
    
}
