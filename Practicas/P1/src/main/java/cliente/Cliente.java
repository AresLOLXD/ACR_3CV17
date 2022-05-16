package cliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import objetos.Gestor;

public class Cliente {

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(System.in)); // Creamos un buffer para la lectura del teclado
            System.out.print("Ingresa la direccion IP: ");
            String host = br.readLine();
            System.out.print("Ingresa el puerto: ");
            int puerto = Integer.parseInt(br.readLine());
            Socket cl = new Socket(host, puerto); // Creamos un socket para conectarnos al servidor con el host y el
                                                  // puerto
            System.out.println("Ingresa el tipo de usuario:\n1)Cliente\n2)Admin");
            int tipo = Integer.parseInt(br.readLine().trim());
            Gestor ges = new Gestor(); // Generamos un objeto para manejar el tipo de usuario
            ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
            oos.flush(); // Hacemos un flush porque parece que hay un bug y se debe hacer inmediatamente
                         // al crear el ObjectOutputStream
            oos.reset(); // Hago un reset para que no tenga cache de objetos
            ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
            oos.writeInt(tipo); // Enviamos el tipo de usuario
            oos.flush();
            oos.reset();// Hago un reset para que no tenga cache de objetos
            switch (tipo) {
                case 1: // Cliente
                    ges.gestionaClienteCliente(ois, oos, br); // Gestionamos si se refiere a un cliente, desde el
                                                              // cliente
                    break;
                case 2:// Abastecedor
                    ges.gestionaAbastecedorCliente(ois, oos, br); // Gestionamos a un Abastecedor, desde el cliente
                    break;
                default:
                    System.out.println("Opcion invalida");
                    break;
            }
            br.close();
            ois.close();
            oos.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
