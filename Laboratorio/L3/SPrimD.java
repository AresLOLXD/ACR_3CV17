import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class SPrimD {
    public static void main(String[] args) {
        try {
            DatagramSocket s = new DatagramSocket(2000); // Iniciamos un datagram socket en el puerto 2000
            System.out.println("Servidor iniciado, esperando cliente");
            for (;;) {
                DatagramPacket p = new DatagramPacket(new byte[2000],
                        2000); // Creamos un datagram packet para poder recibir a lo maximo 2000 bytes de
                               // informacion
                s.receive(p); // Esperamos a que llegue el paquete
                System.out.println("Datagrama recibido desde"
                        + p.getAddress() + ":" + p.getPort()); // Mostramos desde donde llega el datagrama host:puerto
                DataInputStream dis = new DataInputStream(
                        new ByteArrayInputStream(p.getData())); // Cremos un DataInputStream con los bytes de nuestro
                                                                // datagrama, DataInputStream se encargara de
                                                                // traducirlos a datos primitivos
                int x = dis.readInt(); // Leemos un int
                float f = dis.readFloat(); // Leemos un float
                long z = dis.readLong(); // Leemos un
                System.out.println("\n\nEntero :" +
                        x + " Flotante: " + f + " Entero largo: " + z);
            } // for
              // s.close()
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }// main
}