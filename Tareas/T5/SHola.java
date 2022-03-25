import java.net.*; //Importamos las bibliotecas necesarias para los sockets
import java.io.*; //Importamos las bibliotecas para los streams(flujos)

public class SHola {
    public static void main(String args[]) {
        try {
            ServerSocket s = new ServerSocket(1234);
            System.out.println("Esperando cliente ...");
            for (;;) {
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde " + cl.getInetAddress() + ":" + cl.getPort());
                String mensaje = "Ares Ulises Juárez Martínez";
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()));
                pw.println(mensaje);
                pw.flush();
                pw.close();
                cl.close();
            } // for
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }// main
}