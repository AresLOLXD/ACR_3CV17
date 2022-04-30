import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class SHolaD {
    public static void main(String[] args) {
        try {
            DatagramSocket s = new DatagramSocket(2000); // Inicia el servidor en el puerto 2000
            System.out.println("Servidor iniciado, esperando cliente");
            for (;;) {
                DatagramPacket p = new DatagramPacket(
                        new byte[2000], 2000); // Preparamos un Datagrama para recibir los datos, con longitud 2000
                                               // bytes
                s.receive(p); // Esperamos hasta que llegue un datagrama
                System.out.println("Datagrama recibido desde " + p.getAddress() + ":" + p.getPort()); // Mostramos desde
                                                                                                      // que IP y cual
                                                                                                      // puerto viene
                String msj = new String(p.getData(), 0, p.getLength()); // Obtenemos el mensaje y el tamaño del mensaje
                System.out.println("Con el mensaje: " + msj + "\n\n"); // Imprimimos el mensaje
            } // for
              // s.close();
        } catch (Exception e) {
            e.printStackTrace(); // Por si acaso
        } // catch
    }// main
}