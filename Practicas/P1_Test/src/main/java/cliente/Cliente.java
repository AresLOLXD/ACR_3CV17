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
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Ingresa la direccion IP: ");
            String host = br.readLine();
            System.out.print("Ingresa el puerto: ");
            int puerto = Integer.parseInt(br.readLine());
            Socket cl = new Socket(host, puerto);
            System.out.println("Ingresa el tipo de usuario:\n1)Cliente\n2)Admin");
            int tipo = Integer.parseInt(br.readLine().trim());
            Gestor ges = new Gestor();
            ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
            oos.flush();
            oos.reset();
            ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
            oos.writeInt(tipo);
            oos.flush();
            oos.reset();
            switch (tipo) {
                case 1:
                    ges.gestionaClienteCliente(ois, oos, br);
                    break;
                case 2:

                    ges.gestionaAbastecedorCliente(ois, oos, br);
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
