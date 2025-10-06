/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OS_Structures;

/**
 *
 * @author sl005
 */
public class OperatingSystem {
    private long cycleDuration;
    private boolean isCycleInSeconds = true;
    public static long cycleCounter = 0;

    public OperatingSystem(boolean isCycleInSeconds, long cycleDuration) {
        if (isCycleInSeconds) {
            this.cycleDuration = cycleDuration*1000;
        } else {
            this.cycleDuration = cycleDuration;
        }
        this.startSystem();
    }
    
    public OperatingSystem() {
        this(true, 1);
    }
    
    public long getCounter() {
        return cycleCounter;
    }
    
    private void startSystem() {
        Thread counterThread = new Thread(() -> {
            long cycleCount = 0;
            while (true) {
                cycleCount++;
                //Update ventana comentado porque falta reimplementarlo correctamente
                //ventana.updateCycleCount(cycleCount);

                try {
                    //Obtener duracion de ciclo desde ventana comentado porque se busca obtenerlo de otro modo
                    //long duration = ventana.getCycleDuration();
                    Thread.sleep(cycleDuration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Hilo del contador interrumpido");
                    break;
                }
            }
        });
        counterThread.setDaemon(true); 
        counterThread.start();
    }
    
}
