import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class CHolaD {
    public static void main(String[] args) {
        try {
            DatagramSocket cl = new DatagramSocket(); // Creamos un socket para enviar datagramas
            System.out.println("Cliente iniciado, escriba un mensaje de saludo");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // Creamos un buffer reader para
                                                                                      // que lea de la consola
            String mensaje = br.readLine();// Leemos al final de la linea
            byte[] mensajeBytes = mensaje.getBytes(); // Obtenemos los bytes de la cadena introducida
            String dst = "127.0.0.1"; // IP destino tambien podria ser un hostname
            int pto = 2000; // Puertoo destino
            byte[] chunk = new byte[2000]; // Arreglo de bytes para poder dividir el mensaje en chunks (pedazos) de
                                           // 2,000 bytes
            int bytesLeidos = 0; // Variable que nos indica cuantos bytes se han leido
            ByteArrayInputStream bytearray = new ByteArrayInputStream(mensajeBytes); // Creamos un flujo de bytes del
                                                                                     // mensaje
            while ((bytesLeidos = bytearray.read(chunk, 0, 2000)) != -1) { // Leemos 2,000 o menos, si regresara -1
                                                                           // significa que ya no hay mas por leer
                DatagramPacket p = new DatagramPacket(chunk,
                        bytesLeidos, InetAddress.getByName(dst), pto); // Creamos un datagrama y lo configuramos con el
                                                                       // chunk, la cantidad de bytes, el hostname
                                                                       // destino y el puerto destino
                cl.send(p); // Enviamos el datagrama
            }
            cl.close(); // Cerramos el socket
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }// main
}