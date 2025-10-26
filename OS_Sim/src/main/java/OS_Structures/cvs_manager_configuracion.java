package OS_Structures;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author vince
 */
public class cvs_manager_configuracion {
    private static final String CSV_HEADER = "Schedule,MemorySpace,CycleDuration,Unit,Quantum";
    public cvs_manager_configuracion() {
    }

    public void guardarConfiguracion(String filePath, String schedule, long memorySpace, long cycleDuration, String unit, long quantum) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println(CSV_HEADER);
            String configData = String.join(",",
                    schedule,
                    String.valueOf(memorySpace),
                    String.valueOf(cycleDuration),
                    unit,
                    String.valueOf(quantum) 
            );
            pw.println(configData);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de configuración CSV: " + e.getMessage());
        }
    }

    public String[] cargarConfiguracion(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("El archivo de configuración no existe: " + filePath);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();

            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return line.split(","); 
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de configuración CSV: " + e.getMessage());
        }
        return null; 
    }
}