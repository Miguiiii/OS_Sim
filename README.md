# Pautas, Requerimientos Funcionales y To-Do List del Proyecto
Siguiendo las pautas dadas en https://drive.google.com/file/d/1t-xXMrJv_zqz4MnGmJWq6zi3TIQlhwNr/view
## Requerimientos Funcionales
-Uso de Hilos/Threads para la simulación de los procesos y Semáforos/Semaphores para garantizar exclusión mútua.
-Desarrollo de 6 políticas de planificación que se muestren en el Stallings. Ordenamiento de la cola posterior a discreción del equipo.
-La simulación debe permitir en tiempo de ejecución:
  -Intercambiar los tipos de algoritmos de planificación de procesos.
  -La duración de un ciclo de ejecución (en segundos o ms).
-Indicación de los siguientes parámetros para escritura/carga en o desde un archivo csv o json:
  -Duración del ciclo de ejecución de una instrucción. (En ms o segundos).
  -Número de instrucciones por programa, o longitud.
  -Si el proceso es CPU bound o I/O bound.
  -El número de ciclos para realizar una excepción (para que el proceso haga una solicitud de E/S).
  -El número de ciclos en el que se completa la solicitud de dicha excepción.
-Implementación de los estados de proceso:
  -Modelo de estados que incluya: Nuevo, Listo, Ejecución, Bloqueado, Terminado y Suspendido.
  -Suspender procesos que requieran más memoria para terminar de ejecutarse.
  -Alistar procesos suspendidos una vez su condición se cumpla.

### Requerimientos de la GUI
