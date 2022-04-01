import java.net.*;//Se importa las bibliotecas para el uso de Sockets
import java.util.Date;
import java.io.*; //Se importa las bibliotecas para el manejo de streams(flujos)

public class Cliente {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(System.in)); // Crea un lector de buffer para la entrada por defecto(Consola)
            System.out.printf("Escriba la dirección del servidor: ");
            String host = br.readLine(); // Se lee la direccion recibida por el usuario
            System.out.printf("\n\nEscriba el puerto: ");
            int pto = Integer.parseInt(br.readLine()); // Se lee y convierte el puerto recibido por el usuario
            Socket cl = new Socket(host, pto); // Se crea un socket de tipo cliente
                                               // para el explorador
            Mensaje mensaje = new Mensaje(); // Creamos un objeto Mensaje y le asignamos valores
            mensaje.setArchivoAdjunto(new File("prueba.txt"));
            mensaje.setFechaDeEnvio(new Date(2323223232L)); // Miercoles, 28 de Enero de 1970. 02:50:23 IST
            mensaje.setFueEnviado(true);
            mensaje.setNumeroDeIntentos(1);
            mensaje.setTiempoEnMinutos(0.05);
            mensaje.setdestino("destino@prueba.com");
            System.out.println(mensaje);
            FileOutputStream fo = new FileOutputStream("Mensaje_cliente.txt"); // Abrimos el stream al archivo para
                                                                               // guardar el objeto serializable
            ObjectOutputStream so = new ObjectOutputStream(fo); // Abrimos un stream con el stream del archivo para
                                                                // escribir objetos
            so.writeObject(mensaje); // Escribimos el objeto
            so.flush(); // Lo enviamos
            so.close();
            fo.close();

            File f = new File("Mensaje_cliente.txt"); // Ahora vamos a necesitar ese archivo otra vez, para poder
                                                      // enviarlo con el socket
            String archivo = f.getAbsolutePath(); // Ruta absoluta del archivo
            String nombre = f.getName(); // Nombre del archivo
            long tam = f.length(); // Tamaño del archivo
            DataOutputStream dos = new DataOutputStream(
                    cl.getOutputStream()); // Se abre el stream de salida del socket para el servidor
            DataInputStream dis = new DataInputStream(
                    new FileInputStream(archivo)); // Este stream espara poder leer el archivo y enviarlo
            dos.writeUTF(nombre); // Le enviamos el nombre
            dos.flush(); // Enviamos la informacion
            dos.writeLong(tam); // Enviamos el tamaño del archivo
            dos.flush(); // Enviamos la informacion
            byte[] b = new byte[1024]; // Creamos un buffer temporal de 1MB para poder enviar chunks del archivo
            long enviados = 0; // Cantidad de chunks enviados
            int porcentaje, n; // Porcentaje enviado y bytes leidos
            n = dis.read(b); // Leemos maximo 1MB del archivo
            dos.write(b, 0, n); // Enviamos el chunk leido
            dos.flush(); // Enviamos la informacion
            enviados = enviados + n; // Sumamos la cantidad de bytes leidos
            porcentaje = (int) (enviados * 100 / tam); // Actualizamos el porcentaje
            System.out.print("Enviado: " + porcentaje + "%\r"); // Mostramos el porcentaje
            System.out.println("\n\nArchivo enviado");
            dis.close(); // Cerramos el buffer del archivo
            dos.close();
            cl.close();

        } catch (Exception e) {
            e.printStackTrace(); // Por cualquier cosa
        }
    }
}
