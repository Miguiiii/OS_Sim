/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Structures;

/**
 *
 * @author Miguel
 */
public class BHNode<T> {
    
    private T element;
    private int prioridad;
    
    public BHNode(T element, int prioridad){
        this.element = element;
        this.prioridad = prioridad;
    }

    public T getElement() {
        return element;
    }

    public int getPrioridad() {
        return prioridad;
    }
    
}
