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
public class BHNode<T> {
    
    private T element;
    private int priority;
    
    public BHNode(T element, int priority){
        this.element = element;
        this.priority = priority;
    }

    public T getElement() {
        return element;
    }

    public int getPriority() {
        return priority;
    }
    
    @Override
    public String toString() {
        return this.element.toString();
    }
    
}
