import java.net.*; //Importamos las bibliotecas necesarias para los sockets
import java.io.*; //Importamos las bibliotecas para los streams(flujos)

public class Cliente {
    public static void main(String args[]) {
        try {
            BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in)); // Creamos un stream para la
                                                                                       // entrada del usuario (Consola)
            System.out.printf("Escriba la dirección del servidor: ");
            String host = br1.readLine();
            System.out.printf("\n\nEscriba el puerto: ");
            int pto = Integer.parseInt(br1.readLine());
            Socket cl = new Socket(host, pto); // Creamos un socket cliente, apuntando al servidor y al puerto para la
                                               // conexion
            BufferedReader br2 = new BufferedReader(new InputStreamReader(cl.getInputStream())); // Creamos un stream
                                                                                                 // para el comunicacion
                                                                                                 // entre el servidor
                                                                                                 // cliente
            String mensaje = br2.readLine(); // Leemos el mensaje del stream
            System.out.println("Recibimos un mensaje desde el servidor");
            System.out.println("Mensaje: " + mensaje);
            br1.close(); // Cerramos la conexion al stream de la consola
            br2.close();// Cerramos la conexion al stream del socket
            cl.close(); // Desconectamos el socket
        } catch (Exception e) {
            e.printStackTrace(); // Por si acaso
        }
    }
}