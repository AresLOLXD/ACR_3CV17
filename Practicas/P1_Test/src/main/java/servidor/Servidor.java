package servidor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import objetos.Gestor;

public class Servidor {

    public static void main(String[] args) {
        while (true) {
            try {
                System.out.println("Esperando conexion en el puerto 3070");
                Gestor ges = new Gestor();
                ServerSocket s = new ServerSocket(3070);
                Socket cl = s.accept();
                System.out.println("Conexion aceptada");
                System.out.println("Conexión establecida desde"
                        + cl.getInetAddress() + ":" + cl.getPort());
                ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
                oos.flush();
                oos.reset();
                ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
                int tipo = ois.readInt();
                switch (tipo) {
                    case 1:
                        oos.writeBoolean(true);
                        ges.gestionaClienteServidor(ois, oos);
                        oos.flush();
                        oos.reset();
                        break;
                    case 2:
                        String pass = ois.readUTF();
                        if (!(pass == null) && pass.equals("12345")) {
                            oos.writeBoolean(true);
                            oos.flush();
                            oos.reset();
                            ges.gestionaAbastecedorServidor(ois, oos);
                        } else {
                            oos.writeBoolean(false);
                            oos.flush();
                            oos.reset();
                        }
                        break;
                    default:
                        oos.writeBoolean(false);
                        oos.flush();
                        oos.reset();
                        break;
                }
                ois.close();
                oos.close();
                cl.close();
                s.close();
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Pues se murio");
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}
