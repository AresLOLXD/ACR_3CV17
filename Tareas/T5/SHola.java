import java.net.*; //Importamos las bibliotecas necesarias para los sockets
import java.io.*; //Importamos las bibliotecas para los streams(flujos)

public class SHola {
    public static void main(String args[]) {
        try {
            ServerSocket s = new ServerSocket(1234); // Iniciamos un socket servidor en el puerto 1234
            System.out.println("Esperando cliente ...");
            for (;;) {
                Socket cl = s.accept(); // Esperamos a la conexion de un socket cliente para la comunicacion
                System.out.println("Conexión establecida desde " + cl.getInetAddress() + ":" + cl.getPort());
                String mensaje = "Ares Ulises Juárez Martínez";
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream())); // Creamos un stream
                                                                                                // para escribir en el
                                                                                                // socket
                pw.println(mensaje); // Escribimos el mensaje
                pw.flush(); // Enviamos la informacion
                pw.close(); // Cerramos el stream
                cl.close(); // Cerramos el socket
            } // for
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }// main
}