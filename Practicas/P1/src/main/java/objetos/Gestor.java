
package objetos;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class Gestor {

    private CarritoDeCompra getCarritoCompra() { // Metodo para obtener el carrito del archivo
        CarritoDeCompra carr = new CarritoDeCompra();
        try {
            File file = new File("existencias");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            carr = (CarritoDeCompra) ois.readObject(); // Intentamos obtener el carrito del archivo
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carr;
    }

    private void saveCarritoCompra(CarritoDeCompra carr) {// Metodo para guardar el carrito en el archivo
        try {
            File file = new File("existencias");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(carr); // Escribimos el carrito en el archivo
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gestionaAbastecedorServidor(ObjectInputStream ois, ObjectOutputStream oos)
            throws IOException { // Manejamos el abastecedor desde el servidor
        CarritoDeCompra carr = getCarritoCompra();
        Producto p;
        oos.writeObject(carr);
        oos.flush();
        oos.reset(); // Hago un reset para que no tenga cache de objetos
        boolean sePuedeSalir = false;
        while (!sePuedeSalir) {// Ciclamos mientras no se deba salir
            int operacion = ois.readInt();
            switch (operacion) {
                case 1: // Agrega producto
                    p = new Producto(); // Recibimos las propiedades del nuevo producto
                    p.setIdProducto(carr.getProductos().size() + 1);
                    p.setNombre(ois.readUTF());
                    p.setCantidad(ois.readInt());
                    p.setPrecio(ois.readDouble());
                    p.setDescripcion(ois.readUTF());
                    carr.addProduct(p); // Lo agregamos
                    carr.setTotal(0.0); // Cambiamos el total a 0
                    break;
                case 2:// Actualiza producto
                    p = new Producto();
                    p.setIdProducto(ois.readInt());
                    p.setNombre(ois.readUTF());
                    p.setCantidad(ois.readInt());
                    p.setPrecio(ois.readDouble());
                    p.setDescripcion(ois.readUTF());
                    carr.removeProduct(p.getIdProducto(), true); // Lo borramos y reindexamos
                    carr.addProduct(p); // Agregamos producto
                    carr.setTotal(0.0); // Cambiamos el total a 0
                    break;
                case 3:// Quita producto
                    carr.removeProduct(ois.readInt(), true); // Lo quitamos de la lista
                    carr.setTotal(0.0);
                    break;
                case 4:// Salir
                    sePuedeSalir = true;
                    break;
            }
            saveCarritoCompra(carr);

            oos.writeObject(carr);
            oos.flush();// Enviamos el carrito actualizado
            oos.reset();// Hago un reset para que no tenga cache de objetos
        }

    }

    public void gestionaClienteServidor(ObjectInputStream ois, ObjectOutputStream oos)
            throws IOException, ClassNotFoundException, CloneNotSupportedException {
        CarritoDeCompra carr = getCarritoCompra();
        Producto p;
        int idProducto;
        oos.writeObject(carr);
        oos.flush();
        oos.reset();// Hago un reset para que no tenga cache de objetos
        boolean sePuedeSalir = false;
        while (!sePuedeSalir) {
            int operacion = ois.readInt();
            switch (operacion) {
                case 1:// Mostrar detalles del producto
                    idProducto = ois.readInt();
                    p = carr.getProducto(idProducto);
                    oos.writeObject(p);
                    oos.flush();
                    oos.reset();// Hago un reset para que no tenga cache de objetos
                    ArrayList<File> archivos = p.obtenerImagenes();
                    oos.writeInt(archivos.size());
                    oos.flush();
                    oos.reset();// Hago un reset para que no tenga cache de objetos
                    byte[] b = new byte[1024];
                    for (File file : archivos) {
                        int n;
                        long enviados = 0, tam = file.length();
                        oos.writeLong(tam);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
                            while (enviados < tam) {
                                n = dis.read(b);
                                oos.write(b, 0, n);
                                oos.flush();
                                oos.reset();// Hago un reset para que no tenga cache de objetos
                                enviados += (long) n;
                            }
                        }
                    }
                    break;
                case 2: // Comprobar existencias
                    p = (Producto) ois.readObject();
                    if (carr.getProducto(p.getIdProducto()).getCantidad() >= p.getCantidad()) {
                        oos.writeBoolean(true);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                    } else {
                        oos.writeBoolean(false);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                    }
                    break;
                case 3:// Comprar nuevo carrito
                    CarritoDeCompra nuevo = (CarritoDeCompra) ois.readObject();
                    CarritoDeCompra propuesta = (CarritoDeCompra) carr.clone();
                    boolean siCompro = true;
                    for (Iterator<Producto> iterator = nuevo.getProductos().iterator(); iterator.hasNext();) {
                        p = iterator.next();
                        if (carr.getProducto(p.getIdProducto()).getCantidad() < p.getCantidad()) {
                            oos.writeBoolean(false);
                            oos.flush();
                            oos.reset();// Hago un reset para que no tenga cache de objetos
                            siCompro = false;
                            break;
                        } else {
                            Producto actualiza = (Producto) carr.getProducto(p.getIdProducto()).clone();
                            actualiza.setCantidad(-p.getCantidad());
                            propuesta.addProduct(actualiza);
                        }
                    }
                    if (siCompro) {
                        carr.setProductos(propuesta.getProductos());
                        carr.setTotal(0.0);

                        oos.writeBoolean(true);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                    } else {
                        oos.writeBoolean(false);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                    }
                    break;
                case 4:// Devuelve productos
                    oos.writeObject(carr);
                    oos.flush();
                    oos.reset();// Hago un reset para que no tenga cache de objetos
                    break;
                case 5:
                    sePuedeSalir = true;
                    break;
            }
            oos.reset();// Hago un reset para que no tenga cache de objetos
        }

        saveCarritoCompra(carr);
    }

    public void gestionaClienteCliente(ObjectInputStream ois, ObjectOutputStream oos, BufferedReader br)
            throws IOException, ClassNotFoundException, CloneNotSupportedException { // Metodo para gestionar el cliente
                                                                                     // desde el cliente
        if (ois.readBoolean()) {
            CarritoDeCompra existencias = (CarritoDeCompra) ois.readObject(); // Obtenemos el carrito actual
            CarritoDeCompra carr = new CarritoDeCompra();
            boolean sePuedeSalir = false;
            Producto p, copiaProducto;
            int IdProducto;
            while (!sePuedeSalir) {
                System.out.println("" +
                        "Ingresa la operacion que quieras realizar:\n" +
                        "1)Revisar carrito de compras\n" +
                        "2)Listar productos en tienda\n" +
                        "3)Mostrar detalles del producto\n" +
                        "4)Comprar producto\n" +
                        "5)Modificar carrito\n" +
                        "6)Finalizar compra\n" +
                        "7)Salir\n" +
                        "");

                int opcion = Integer.parseInt(br.readLine());
                switch (opcion) {
                    case 1: // Revisar carrito
                        System.out.println(carr);
                        break;
                    case 2:// Listar producots en tienda
                        oos.writeInt(4);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        existencias = (CarritoDeCompra) ois.readObject(); // Revisamos las existencias de productos
                        for (Iterator<Producto> iterator = existencias.getProductos().iterator(); iterator.hasNext();) {
                            p = iterator.next();
                            System.out.println("ID: " + p.getIdProducto() + "\tNombre: " + p.getNombre()
                                    + "\tExistencias: " + p.getCantidad());
                        }
                        break;
                    case 3:// Mostrar detalles del producto
                        oos.writeInt(1);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        System.out.print("Ingresa el ID del producto: ");

                        IdProducto = Integer.parseInt(br.readLine());
                        oos.writeInt(IdProducto); // Enviamos el ID del producto
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        p = (Producto) ois.readObject(); // Obtenemos el producto
                        System.out.println(p);

                        int cantidad = ois.readInt();// Cantidad de imagenes del producto
                        byte[] b = new byte[1024]; // Buffer para escribir la imagen
                        for (int i = 0; i < cantidad; i++) {
                            int n;
                            long recibidos = 0, tam = ois.readLong();// Leemos el tamaño del archivo
                            File archivo = new File("Producto_" + (i + 1) + ".png"); // Creamos un archivo
                            try (DataOutputStream dos = new DataOutputStream(
                                    new FileOutputStream(archivo))) {
                                while (recibidos < tam) { // Escribimos la imagen mientras tengamos que recibir
                                    n = ois.read(b);
                                    dos.write(b, 0, n);
                                    recibidos += n;
                                }
                            }
                            muestraImagen(archivo); // Mostramos la imagen
                        }
                        break;
                    case 4:// Comprar producto
                        System.out.print("Ingresa el ID del producto: ");

                        IdProducto = Integer.parseInt(br.readLine());
                        p = carr.getProducto(IdProducto); // Obtenemos el producto para revisar si ya esta en el carrito
                        if (p == null) { // Si no existe entonces hay que tomarlo de las existencias
                            p = existencias.getProducto(IdProducto); // Lo buscamos en las existencias
                            if (!(p == null)) { // Si lo encontro entonces lo clonamos
                                p = (Producto) p.clone();// Lo clonamos
                                p.setCantidad(0); // Ponemos la cantidad de 0 para que lo cambiemos despues
                            }
                        }
                        if (!(p == null)) { // Si el producto es diferente de null
                            copiaProducto = (Producto) p.clone(); // Lo clonamos
                            System.out.print("Ingresa la cantidad del producto: ");
                            copiaProducto.setCantidad(Integer.parseInt(br.readLine())); // Ponemos la cantidad desde el
                                                                                        // teclado
                            oos.writeInt(2); // Le decimos que queremos comprar un objeto
                            oos.flush();
                            oos.reset();// Hago un reset para que no tenga cache de objetos
                            copiaProducto.setCantidad(p.getCantidad() + copiaProducto.getCantidad()); // Lo agregamos a
                                                                                                      // la copia
                            oos.writeObject(copiaProducto); // Lo enviamos
                            oos.flush();
                            oos.reset();// Hago un reset para que no tenga cache de objetos
                            if (ois.readBoolean()) { // Significa que si hay existencias suficientes
                                copiaProducto.setCantidad(copiaProducto.getCantidad() - p.getCantidad());
                                carr.addProduct(copiaProducto); // Lo agregamos al carrito
                                System.out.println("Articulo agregado al carrito");

                            } else { // No hay existencias
                                System.out.println("No hay suficientes existencias");
                            }
                        } else {
                            System.out.println("El producto no existe");
                        }
                        break;
                    case 5:// Modificar carrito
                        boolean sePuedeSalir2 = false;
                        while (!sePuedeSalir2) { // Entramos en el ciclo para modificar el carrito
                            System.out.println("" +
                                    "Ingresa la operacion que quieras realizar:\n" +
                                    "1)Mostrar carrito\n" +
                                    "2)Modifcar cantidad de un producto\n" +
                                    "3)Eliminar producto\n" +
                                    "4)Salir\n" +
                                    "");

                            int opcion2 = Integer.parseInt(br.readLine());
                            switch (opcion2) {
                                case 1:// Mostrar carrito
                                    System.out.println(carr);

                                    break;
                                case 2:// Modificar cantidad de un producto
                                    System.out.print("Ingresa el ID del producto: ");

                                    IdProducto = Integer.parseInt(br.readLine());
                                    p = carr.getProducto(IdProducto); // Buscamos el producto que queremos modificar

                                    if (!(p == null)) { // Si el objeto es diferente de nulo
                                        p = (Producto) p.clone();
                                        System.out.print("Ingresa la cantidad del producto: ");

                                        p.setCantidad(Integer.parseInt(br.readLine()));
                                        oos.writeInt(2);
                                        oos.flush();
                                        oos.reset();// Hago un reset para que no tenga cache de objetos
                                        oos.writeObject(p);
                                        oos.flush();
                                        oos.reset();// Hago un reset para que no tenga cache de objetos
                                        if (ois.readBoolean()) { // Si hay existencias
                                            carr.removeProduct(p.getIdProducto(), false); // Lo borramos
                                            carr.addProduct(p);// Y lo agregamos
                                            System.out.println("Carrito modificado correctamente");
                                        } else {
                                            System.out.println("No hay suficientes existencias");
                                        }
                                    } else {
                                        System.out.println("El producto no esta en el carrito");
                                    }
                                    break;
                                case 3:// Elimar producto
                                    System.out.print("Ingresa el ID del producto: ");

                                    IdProducto = Integer.parseInt(br.readLine());
                                    p = carr.getProducto(IdProducto); // Lo buscamos en el carrito
                                    if (!(p == null)) {
                                        carr.removeProduct(IdProducto, false); // Lo quitamos
                                        System.out.println("Producto eliminado del carrito");
                                    } else {
                                        System.out.println("El producto no esta en el carrito");
                                    }
                                    break;
                                case 4: // Salir
                                    sePuedeSalir2 = true;
                                    break;
                                default:
                                    System.out.println("Opcion invalida");
                                    break;
                            }
                        }
                        break;
                    case 6: // Finalizar compra
                        oos.writeInt(3);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        oos.writeObject(carr);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        if (ois.readBoolean()) { // Si paso la compra
                            carr.generateTicket(); // Generamos el ticket
                            carr = new CarritoDeCompra(); // Creamos un nuevo carrito de compra
                            System.out.println("Compra existosa");
                        } else {
                            System.out.println("Hubo un error con la compra");
                        }
                        break;
                    case 7:
                        // Finalizar compra
                        sePuedeSalir = true;
                        oos.writeInt(5);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        break;
                    default:
                        System.out.println("Opcion invalida");
                        break;
                }
                oos.reset();// Hago un reset para que no tenga cache de objetos

            }
        } else {
            System.out.println("Pues no puedes comprar gg");
        }
    }

    public void gestionaAbastecedorCliente(ObjectInputStream ois, ObjectOutputStream oos, BufferedReader br)
            throws IOException, ClassNotFoundException { // Metodo para gestionar el abastecedor desde el cliente
        System.out.print("Ingresa la contraseña: ");
        String pass = br.readLine().trim();
        oos.writeUTF(pass); // Enviamos la contraseña
        oos.flush();
        oos.reset();// Hago un reset para que no tenga cache de objetos
        if (ois.readBoolean()) { // Si lo autorizamos
            boolean sePuedeSalir = false;
            Producto p;
            CarritoDeCompra carr = (CarritoDeCompra) ois.readObject(); // Obtenemos las existencias
            boolean deboLeer = false;
            while (!sePuedeSalir) {
                System.out.println("Ingresa la operacion que quieres realizar:\n" +
                        "1)Agregar productos\n" +
                        "2)Actualiza un producto\n" +
                        "3)Quita un producto\n" +
                        "4)Mostrar productos\n" +
                        "5)Salir\n" +
                        "");
                int operacion = Integer.parseInt(br.readLine().trim());
                switch (operacion) {
                    case 1: // Registrar producto
                        oos.writeInt(1);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        p = new Producto();
                        System.out.print("Ingresa el nombre del producto: ");
                        p.setNombre(br.readLine().trim().replaceAll("\u001B\\[[\\d;]*[^\\d;]", ""));
                        System.out.print("Ingresa la descripcion del producto: ");
                        p.setDescripcion(br.readLine().trim().replaceAll("\u001B\\[[\\d;]*[^\\d;]", ""));
                        System.out.print("Ingresa la cantidad de articulos que habra: ");
                        p.setCantidad(Integer.parseInt(br.readLine().trim()));
                        System.out.print("Ingresa el precio: ");
                        p.setPrecio(Double.parseDouble(br.readLine().trim()));
                        oos.writeUTF(p.getNombre());
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        oos.writeInt(p.getCantidad());
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        oos.writeDouble(p.getPrecio());
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        oos.writeUTF(p.getDescripcion());
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        System.out.println("Producto registrado");
                        deboLeer = true;
                        break;
                    case 2: // Modificar producto
                        System.out.print("Ingresa el ID del producto: ");
                        p = carr.getProducto(Integer.parseInt(br.readLine().trim())); // Buscamos el producto en las
                                                                                      // existencias
                        if (!(p == null)) { // Si el producto es diferente de nulo
                            oos.writeInt(2);
                            oos.flush();
                            oos.reset();// Hago un reset para que no tenga cache de objetos
                            System.out.print("Ingresa el nombre del producto: ");
                            p.setNombre(br.readLine().trim().replaceAll("\u001B\\[[\\d;]*[^\\d;]", ""));
                            System.out.print("Ingresa la descripcion del producto: ");
                            p.setDescripcion(br.readLine().trim().replaceAll("\u001B\\[[\\d;]*[^\\d;]", ""));
                            System.out.print("Ingresa la cantidad de articulos que habra: ");
                            p.setCantidad(Integer.parseInt(br.readLine().trim()));
                            System.out.print("Ingresa el precio: ");
                            p.setPrecio(Double.parseDouble(br.readLine().trim()));
                            oos.writeInt(p.getIdProducto());
                            oos.flush();
                            oos.reset();// Hago un reset para que no tenga cache de objetos
                            oos.writeUTF(p.getNombre());
                            oos.flush();
                            oos.reset();// Hago un reset para que no tenga cache de objetos
                            oos.writeInt(p.getCantidad());
                            oos.flush();
                            oos.reset();// Hago un reset para que no tenga cache de objetos
                            oos.writeDouble(p.getPrecio());
                            oos.flush();
                            oos.reset();// Hago un reset para que no tenga cache de objetos
                            oos.writeUTF(p.getDescripcion());
                            oos.flush();
                            oos.reset();// Hago un reset para que no tenga cache de objetos
                            System.out.println("Producto actualizado");
                        } else {
                            System.out.println("ID invalido");
                        }
                        deboLeer = true;
                        break;
                    case 3: // Borar producto
                        System.out.println("Ingresa el ID del producto");
                        p = carr.getProducto(Integer.parseInt(br.readLine().trim()));// Buscamos el producto
                        if (!(p == null)) { // Si el producto es diferente de nulo
                            oos.writeInt(3);
                            oos.writeInt(p.getIdProducto());
                            oos.flush();
                            oos.reset();// Hago un reset para que no tenga cache de objetos
                            System.out.println("Producto eliminado");
                        } else {
                            System.out.println("ID invalido");
                        }
                        deboLeer = true;
                        break;
                    case 4: // Mostramos las existencias
                        System.out.println(carr);
                        deboLeer = false;
                        break;
                    case 5: // Salir del ciclo
                        sePuedeSalir = true;
                        oos.writeInt(4);
                        oos.flush();
                        oos.reset();// Hago un reset para que no tenga cache de objetos
                        deboLeer = true;
                        break;
                    default:// Opcion invalida
                        deboLeer = false;
                        System.out.println("Opcion invalida");
                        break;
                }
                if (deboLeer) {
                    carr = (CarritoDeCompra) ois.readObject();
                }
                oos.reset();// Hago un reset para que no tenga cache de objetos

            }
        } else {
            System.out.println("Contraseña incorrecta");
        }
    }

    private void muestraImagen(File archivo) throws IOException { // Metodo para mostrar el archivo
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(archivo); // Trata de abrir el archivo
        }
    }
}
