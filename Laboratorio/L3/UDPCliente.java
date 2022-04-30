import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class UDPCliente {
    public final static int PUERTO = 7;
    private final static int LIMITE = 100;

    public static void main(String[] args) {
        boolean bandera = false;
        SocketAddress remoto = new InetSocketAddress("127.0.0.1", PUERTO); // Definimos la direccion del servidor y su
                                                                           // puerto, en este caso el del puerto UDP
                                                                           // Echo
        try {
            DatagramChannel canal = DatagramChannel.open(); // Abrimos un datagram channel
            canal.configureBlocking(false); // Lo configuramos a que no sea bloqueante
            canal.connect(remoto); // Le decimos que se conecte a la direccion remota
            Selector selector = Selector.open(); // Crea un selector para canal
            canal.register(selector, SelectionKey.OP_WRITE); // Le decimos a canal que usaremos este selector y que
                                                             // vamos a escribir
            ByteBuffer buffer = ByteBuffer.allocateDirect(4);// Vamos a crear un buffer con una capacidad de 4 bytes, el
                                                             // tamaño de un entero
            int n = 0;
            while (true) {
                selector.select(5000L); // espera 5 segundos por la conexión
                Set<SelectionKey> sk = selector.selectedKeys();// Obtenemos las llaves del selector
                if (sk.isEmpty() && n == LIMITE || bandera) { // Revisamos si el conjunto de llaves esta vacio y es el
                                                              // limite de elementos enviados o si la bandera esta
                                                              // activa
                    canal.close(); // Cerramos el canal
                    break;
                } else {
                    Iterator<SelectionKey> it = sk.iterator(); // Creamos un iterador de las llaves
                    while (it.hasNext()) { // Mientras el iterador tenga llaves iteramos
                        SelectionKey key = it.next(); // Obtenemos la llave actual que se esta iterando
                        it.remove(); // Y la eliminamos
                        if (key.isWritable()) { // Si la llave puede escribir, entonces lo hacemos
                            buffer.clear(); // Limpiamos el buffer
                            buffer.putInt(n); // Colocamos el valor de n
                            buffer.flip(); // Cambiamos el modo del buffer
                            canal.write(buffer); // Escribimos lo del buffer en el canal y lo enviamos
                            System.out.println("Escribiendo el dato: " + n);
                            n++;
                            if (n == LIMITE) {
                                // todos los paquetes han sido escritos;
                                buffer.clear();
                                buffer.putInt(1000);
                                buffer.flip();
                                canal.write(buffer);
                                bandera = true;
                                key.interestOps(SelectionKey.OP_READ); // Cambiamos a leer
                                break;
                            } // if
                        } // if
                    } // while
                } // else
            } // while
        } catch (Exception e) {
            System.err.println(e);
        } // catch
    }// main
}// class