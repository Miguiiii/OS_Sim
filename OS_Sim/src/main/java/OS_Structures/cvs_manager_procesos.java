package OS_Structures;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author vince
 */

public class cvs_manager_procesos {

    private final String filePath;
    private static final String CSV_HEADER = "ID,Name,Priority,BirthTime,MaxRunTime,Pile,ProgramCounter,State";

    public cvs_manager_procesos(String fileName) {
        this.filePath = fileName;
    }
    
    public void guardarProceso(OS_Process process) {
        File file = new File(filePath);
        boolean isNewFile = !file.exists();
        try (FileWriter fw = new FileWriter(file, true);
             PrintWriter pw = new PrintWriter(fw)) {

            if (isNewFile) {
                pw.println(CSV_HEADER);
            }
            String processData = String.join(",",
                    String.valueOf(process.getId()),
                    process.getName(),
                    String.valueOf(process.getPriority()),
                    String.valueOf(process.getBirthTime()),
                    //String.valueOf(process.getMaxRunTime()),
                    String.valueOf(process.getPile()),
                    //String.valueOf(process.getProgram_counter()),
                    String.valueOf(process.getState())
            );

            pw.println(processData);
 
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo CSV: " + e.getMessage());
        }
    }

    public OS_Process buscarProcesoPorId(int targetId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); 

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                int currentId = Integer.parseInt(data[0]);

                if (currentId == targetId) {
                    String name = data[1];
                    int priority = Integer.parseInt(data[2]);
                    long birthTime = Long.parseLong(data[3]);
                    long maxRunTime = Long.parseLong(data[4]);
                    long pile = Long.parseLong(data[5]);
                    //OS_Process foundProcess = new OS_Process(name, currentId, birthTime, maxRunTime, pile, priority);
                   // return foundProcess;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo CSV: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear un número del CSV: " + e.getMessage());
        }
        
        return null;
    }
}