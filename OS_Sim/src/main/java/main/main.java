/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author vince
 */
public class main {

    public static void main(String[] args) {
        GUI ventana = new GUI();
        Thread counterThread = new Thread(() -> {
            long cycleCount = 0;
            while (true) {
                cycleCount++;
                ventana.updateCycleCount(cycleCount);

                try {
                    long duration = ventana.getCycleDuration();
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Hilo del contador interrumpido");
                    break;
                }
            }
        });

        counterThread.setDaemon(true); 
        counterThread.start();
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
    }
}