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
                Gestor ges = new Gestor();// Creamos el objeto gestor de usuarios
                ServerSocket s = new ServerSocket(3070); // Generamos un Servidor en el puerto 3070
                Socket cl = s.accept(); // Esperamos conexion
                System.out.println("Conexion aceptada");
                System.out.println("Conexión establecida desde"
                        + cl.getInetAddress() + ":" + cl.getPort()); // Mostramos la informacion de la conexion
                ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
                oos.flush(); // Hacemos flush por una cosa de Java
                oos.reset();// Hago un reset para que no tenga cache de objetos
                ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
                int tipo = ois.readInt(); // Recibimos el tipo de usuario del cliente
                switch (tipo) {
                    case 1:// Cliente
                        oos.writeBoolean(true); // Si autorizado
                        ges.gestionaClienteServidor(ois, oos); // Gestiona Cliente desde el servidor
                        oos.flush();
                        oos.reset();
                        break;
                    case 2:// Abastecedor
                        String pass = ois.readUTF();// Recibimos la contraseña
                        if (!(pass == null) && pass.equals("12345")) { // Solo la comparamos con 12345
                            oos.writeBoolean(true); // Le devolvemos que si paso
                            oos.flush();
                            oos.reset(); // Hago un reset para que no tenga cache de objetos
                            ges.gestionaAbastecedorServidor(ois, oos); // Gestiona Abastacedor desde el Servidor
                        } else {
                            oos.writeBoolean(false); // No autorizado
                            oos.flush();
                            oos.reset();// Hago un reset para que no tenga cache de objetos
                        }
                        break;
                    default:
                        oos.writeBoolean(false); // No autorizado
                        oos.flush();
                        oos.reset();
                        break;
                }
                ois.close();
                oos.close();
                cl.close();
                s.close();
                TimeUnit.SECONDS.sleep(5);// Sleep de 5 segundos
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Pues se murio");
                try {
                    TimeUnit.SECONDS.sleep(10); // Sleep de 10 segundos, porque puede ser que el puerto siga ocupado
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}
