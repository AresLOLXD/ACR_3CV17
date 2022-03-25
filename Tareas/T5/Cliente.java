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
            Socket cl = new Socket(host, pto);
            BufferedReader br2 = new BufferedReader(new InputStreamReader(cl.getInputStream()));
            String mensaje = br2.readLine();
            System.out.println("Recibimos un mensaje desde el servidor");
            System.out.println("Mensaje: " + mensaje);
            br1.close();
            br2.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}