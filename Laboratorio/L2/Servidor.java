import java.net.*;//Se importa las bibliotecas para el uso de Sockets
import java.io.*; //Se importa las bibliotecas para el manejo de streams(flujos)

public class Servidor {
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(7000); // Creamos un socket servidor con el puerto 7000
            System.out.println("Esperamos conexion");
            Socket cl = s.accept(); // Esperamos la conexion
            System.out.println("Conexión establecida desde"
                    + cl.getInetAddress() + ":" + cl.getPort()); // Mostramos la direccion desde la cual se esta
                                                                 // conectando
            DataInputStream dis = new DataInputStream(
                    cl.getInputStream()); // Creamos un stream para recibir
                                          // informacion del socket cliente
            byte[] b = new byte[1024]; // Creamos un buffer de 1MB para recibir los chunks de los archivos
            String nombre = dis.readUTF(); // Leemos el nombre del archivo
            System.out.println("Recibimos el archivo:" + nombre);
            long tam = dis.readLong(); // Leemos el tamaño del archivo en bytes
            DataOutputStream dos = new DataOutputStream(
                    new FileOutputStream("Mensaje_servidor.txt")); // Creamos un stream para escribir el archivo en el
                                                                   // mismo directorio donde se ejecuta el Servidor
            long recibidos = 0; // Bytes recibidos
            int n, porcentaje; // Bytes leidos y el porcentaje
            while (recibidos < tam) { // Iteramos mientras no terminemos de leer el archivo por completo
                n = dis.read(b); // Leemos un maximo de 1MB de chunk
                dos.write(b, 0, n); // Lo escribimos en el flujo del archivo
                dos.flush(); // Escribimos la informacion en el archivo
                recibidos = recibidos + n; // Actualizamos los bytes recibidos
                porcentaje = (int) (recibidos * 100 / tam);
                System.out.print("Recibido: " + porcentaje + "% \r");
            } // While
            System.out.println("\n\n\nArchivo recibido.");
            dos.close();
            dis.close();
            FileInputStream fi = new FileInputStream("Mensaje_servidor.txt"); // Abrimos un stream para el archivo
                                                                              // recibido
            ObjectInputStream si = new ObjectInputStream(fi); // Abrimos un stream para leer el objeto del stream del
                                                              // archivo
            Mensaje mensajeRecibido = (Mensaje) si.readObject(); // Almacenamos el objeto y lo casteamos
            System.out.println(mensajeRecibido); // Lo imprimimos
            si.close();
            fi.close();
            cl.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }
}
