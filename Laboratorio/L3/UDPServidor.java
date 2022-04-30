import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class UDPServidor {
    public final static int PUERTO = 7;
    public final static int TAM_MAXIMO = 65507;

    public static void main(String[] args) {
        int port = PUERTO;
        try {
            DatagramChannel canal = DatagramChannel.open(); // Abrimos un DatagramChannel
            canal.configureBlocking(false); // Lo configuramos a que no sea bloqueante
            DatagramSocket socket = canal.socket(); // Creamos un datagrama socket
            SocketAddress dir = new InetSocketAddress(port); // Obtenemos la direccion actual y el puerto configurado
            socket.bind(dir); // Se lo asignamos al socket
            Selector selector = Selector.open(); // Abrimos el selector
            canal.register(selector,
                    SelectionKey.OP_READ); // Especificamos que vamos a leer
            ByteBuffer buffer = ByteBuffer.allocateDirect(
                    TAM_MAXIMO); // Asignamos el tamaño maximo del paquete, el cual es 65507 bytes, el tamaño
                                 // maximo de UDP
            while (true) {
                selector.select(5000);// Esperamos 5 segundos
                Set<SelectionKey> sk = selector.selectedKeys(); // Obtenemos las llaves
                Iterator<SelectionKey> it = sk.iterator(); // Obtenemos un iterador de las llaves
                while (it.hasNext()) {
                    SelectionKey key = it.next(); // Obtenemos la llave siguiente
                    it.remove(); // Y la eliminamos
                    if (key.isReadable()) {// Si la llave se puede leer procedemos
                        buffer.clear(); // Limpiamos el buffer
                        SocketAddress client = canal.receive(buffer); // Obtenemos en el buffer lo que hayan mandado por
                                                                      // el canal
                        buffer.flip();// Cambiamos el modo del buffer
                        int eco = buffer.getInt(); // Obtenemos el entero que se haya leido
                        if (eco == 1000) { // Si eco == 0 significa que terminamos y cerramos el canal y salimos del
                                           // programa
                            canal.close();
                            System.exit(0);
                        } else {
                            System.out.println("Dato leido : " + eco);
                            buffer.flip();
                            canal.send(buffer, client);// Reenviamos justo lo que se ha recibido
                        } // else
                    } // if
                } // while2
            } // while
        } catch (IOException e) {
            System.err.println(e);
        } // catch
    }// main
}// class