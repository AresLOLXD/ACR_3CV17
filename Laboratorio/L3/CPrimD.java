import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class CPrimD {
    public static void main(String[] args) {
        try {
            int pto = 2000;
            InetAddress dst = InetAddress.getByName("127.0.0.1"); // Obtenemos la direccion IP desde la direccion IP :v
            DatagramSocket cl = new DatagramSocket(); // Iniciamos un nuevo socket, sin asignar el puerto
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Creamos un ByteArrayOutputStream que es
                                                                      // unarreglo de bytes para poder enviarlo en el
                                                                      // datagrama
            DataOutputStream dos = new DataOutputStream(baos); // Creamos un DataOutputStream el cual se encargara de
                                                               // convertir los datos primitivos al arreglo de bytes
                                                               // para poder enviarlos en el datagrama
            dos.writeInt(4); // Escribimos un 4
            dos.flush(); // Terminamos de escribir
            dos.writeFloat(4.1f); // Escribimos un 4.1
            dos.flush();// Terminamos de escribir
            dos.writeLong(75L); // Escribimos un 75 pero long
            dos.flush();// Terminamos de escribir
            byte[] b = baos.toByteArray(); // Convertimos el ByteArrayOutputStream a un arreglo de bytes para enviarlo
            DatagramPacket p = new DatagramPacket(b,
                    b.length, dst, pto); // Creamos un DatagramaPacket que es el paquete colocando la IP y puerto
                                         // destino junto con el arreglo de bytes
            cl.send(p); // Lo enviamos
            cl.close(); // Cerramos la conexion
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }// main
}