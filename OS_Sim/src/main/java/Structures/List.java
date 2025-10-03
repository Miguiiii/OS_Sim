/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Structures;
import java.util.Iterator;
/**
 *
 * @author Miguel
 * @param <T> Any Object
 */
public class List<T> implements Iterable<T> {
    private Node<T> head;
    private int length;

    public List() {
        this.head = null;
        this.length = 0;
    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
    
    public boolean isEmpty() {
        return getHead() == null;
    }
    
    public boolean hasElement(T element) {
        for (T i:this) {
            if (i == element) {return true;}
        }
        return false;
    }
    
    public T getElmenetAtIndex(int index) {
        T element = null;
        int cont = 0;
        for (T i:this) {
            if (cont == index) {
                element = i;
                break;
            }
            cont++;
        }
        return element;
    }
    
    public void insertBegin(T element) {
        Node<T> Node = new Node(element);
        if (isEmpty()) {
            setHead(Node);
        } else {
            Node.setNext(getHead());
            setHead(Node);
        }
        length++;
    }
    
    public void insertFinal(T element) {
        if (isEmpty()) {
            insertBegin(element);
        } else {
            Node<T> Node = new Node(element);
            Node pointer = getHead();
            while (pointer.getNext() != null) {
                pointer = pointer.getNext();
            }
            pointer.setNext(Node);
            length++;
        }
    }
    
    public void insertAtIndex(T element, int index) {
        if (isEmpty() || index == 0) {
            insertBegin(element);
            return;
        }
        if (index > getLength()) {
            System.out.println("Invalid Index");
            return;
        }
        if (index == getLength()) {
            insertFinal(element);
            return;
        }

        Node<T> Node = new Node(element);
        Node pointer = getHead();
        int cont = 0;
        while (cont < index - 1) {
            pointer = pointer.getNext();
            cont++;
        }
        Node temp = pointer.getNext();
        Node.setNext(temp);
        pointer.setNext(Node);
        length++;

    }
    
    public Node deleteBegin() {
        if(isEmpty()) {
            System.out.println("La lista esta vacia");
            return null;
        } else {
            Node pointer = getHead();
            setHead(pointer.getNext());
            pointer.setNext(null);
            length--;
            return pointer;
        }
    }
    
    public Node deleteFinal() {
        if (isEmpty()) {
            System.out.println("La lista esta vacia");
            return null;
        } else if (getLength() == 1) {
            return deleteBegin();
        } else {
            Node pointer = getHead();
            while (pointer.getNext().getNext() != null) {
                pointer = pointer.getNext();
            }
            Node temp = pointer.getNext();
            pointer.setNext(null);
            length--;
            return temp;
        }
    }
    
    public Node deleteAtIndex(int index) {
        
        if (index == 0) {
            return deleteBegin();
        } else if (index == getLength()) {
            return deleteFinal();
        } else if (index < getLength()) {
            Node pointer = getHead();
            for (int i = 0; i < index - 1; i++) {
                pointer = pointer.getNext();
            }
            Node temp = pointer.getNext();
            pointer.setNext(temp.getNext());
            temp.setNext(null);
            length--;
            return temp;
        } else {
            System.out.println("Index not valid");
            return null;
        }
    }
    
    public Node deleteElement(T element) {
        
        Node pointer = getHead();
        
        if (pointer.getElement() == element) {
            return deleteBegin();
        } else {
            while (pointer.getNext().getElement() != element) {
                pointer = pointer.getNext();
                if (pointer.getNext() == null) {
                    break;
                }
            }
            if (pointer.getNext() == null) {
                return null;
            }
            Node temp = pointer.getNext();
            pointer.setNext(temp.getNext());
            temp.setNext(null);
            length--;
            return temp;
        }

    }
    
    public void print() {
        
    }

    @Override
    public Iterator iterator() {
        return new IteratorList(this);
    }
    
}


class IteratorList<T> implements Iterator<T> {
    
    Node<T> pointer;
    
    public IteratorList(List list) {
        pointer = list.getHead();
    }
    
    @Override
    public boolean hasNext() {
        return pointer != null;
    }

    @Override
    public T next() {
        T current = pointer.getElement();
        pointer = pointer.getNext();
        return current;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}