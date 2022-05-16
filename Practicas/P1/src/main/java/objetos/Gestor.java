
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

/**
 *
 * @author aresu
 */
public class Gestor {

    private CarritoDeCompra getCarritoCompra() {
        CarritoDeCompra carr = new CarritoDeCompra();

        try {
            File file = new File("existencias");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            carr = (CarritoDeCompra) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carr;
    }

    private void saveCarritoCompra(CarritoDeCompra carr) {
        try {
            File file = new File("existencias");
            file.setLastModified(System.currentTimeMillis());
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(carr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gestionaAbastecedorServidor(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
        CarritoDeCompra carr = getCarritoCompra();
        Producto p;
        oos.writeObject(carr);
        oos.flush();
        oos.reset();
        boolean sePuedeSalir = false;
        while (!sePuedeSalir) {
            int operacion = ois.readInt();
            switch (operacion) {
                case 1: // Agrega producto
                    p = new Producto();
                    p.setIdProducto(carr.getProductos().size() + 1);
                    p.setNombre(ois.readUTF());
                    p.setCantidad(ois.readInt());
                    p.setPrecio(ois.readDouble());
                    p.setDescripcion(ois.readUTF());
                    carr.addProduct(p);
                    carr.setTotal(0.0);
                    break;
                case 2:// Actualiza producto
                    p = new Producto();
                    p.setIdProducto(ois.readInt());
                    p.setNombre(ois.readUTF());
                    p.setCantidad(ois.readInt());
                    p.setPrecio(ois.readDouble());
                    p.setDescripcion(ois.readUTF());
                    carr.removeProduct(p.getIdProducto(), true);
                    carr.addProduct(p);
                    carr.setTotal(0.0);
                    break;
                case 3:// Quita producto
                    carr.removeProduct(ois.readInt(), true);
                    carr.setTotal(0.0);
                    break;
                case 4:// Salir
                    sePuedeSalir = true;
                    break;
            }
            saveCarritoCompra(carr);

            oos.writeObject(carr);
            oos.flush();
            oos.reset();
        }

    }

    public void gestionaClienteServidor(ObjectInputStream ois, ObjectOutputStream oos)
            throws IOException, ClassNotFoundException, CloneNotSupportedException {

        CarritoDeCompra carr = getCarritoCompra();
        Producto p;
        int idProducto;
        oos.writeObject(carr);
        oos.flush();
        oos.reset();
        boolean sePuedeSalir = false;
        while (!sePuedeSalir) {
            int operacion = ois.readInt();
            switch (operacion) {
                case 1:// Mostrar detalles del producto
                    idProducto = ois.readInt();
                    p = carr.getProducto(idProducto);
                    oos.writeObject(p);
                    oos.flush();
                    oos.reset();
                    ArrayList<File> archivos = p.obtenerImagenes();
                    oos.writeInt(archivos.size());
                    oos.flush();
                    oos.reset();
                    byte[] b = new byte[1024];
                    for (File file : archivos) {
                        int n;
                        long enviados = 0, tam = file.length();
                        oos.writeLong(tam);
                        oos.flush();
                        oos.reset();
                        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
                            while (enviados < tam) {
                                n = dis.read(b);
                                oos.write(b, 0, n);
                                oos.flush();
                                oos.reset();
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
                        oos.reset();
                    } else {
                        oos.writeBoolean(false);
                        oos.flush();
                        oos.reset();
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
                            oos.reset();
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
                        oos.reset();
                    } else {
                        oos.writeBoolean(false);
                        oos.flush();
                        oos.reset();
                    }
                    break;
                case 4:// Devuelve productos
                    oos.writeObject(carr);
                    oos.flush();
                    oos.reset();
                    break;
                case 5:
                    sePuedeSalir = true;
                    break;
            }
            oos.reset();
        }

        saveCarritoCompra(carr);
    }

    public void gestionaClienteCliente(ObjectInputStream ois, ObjectOutputStream oos, BufferedReader br)
            throws IOException, ClassNotFoundException, CloneNotSupportedException {
        if (ois.readBoolean()) {
            CarritoDeCompra existencias = (CarritoDeCompra) ois.readObject();
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
                        oos.reset();
                        existencias = (CarritoDeCompra) ois.readObject();
                        for (Iterator<Producto> iterator = existencias.getProductos().iterator(); iterator.hasNext();) {
                            p = iterator.next();
                            System.out.println("ID: " + p.getIdProducto() + "\tNombre: " + p.getNombre()
                                    + "\tExistencias: " + p.getCantidad());

                        }
                        break;
                    case 3:// Mostrar detalles del producto
                        oos.writeInt(1);
                        oos.flush();
                        oos.reset();
                        System.out.print("Ingresa el ID del producto: ");

                        IdProducto = Integer.parseInt(br.readLine());
                        oos.writeInt(IdProducto);
                        oos.flush();
                        oos.reset();
                        p = (Producto) ois.readObject();
                        System.out.println(p);
                        int cantidad = ois.readInt();
                        byte[] b = new byte[1024];
                        for (int i = 0; i < cantidad; i++) {
                            int n;
                            long recibidos = 0, tam = ois.readLong();
                            File archivo = new File("Producto_" + (i + 1) + ".png");
                            try (DataOutputStream dos = new DataOutputStream(
                                    new FileOutputStream(archivo))) {
                                while (recibidos < tam) {
                                    n = ois.read(b);
                                    dos.write(b, 0, n);
                                    recibidos += n;
                                }
                            }
                            muestraImagen(archivo);
                        }
                        break;
                    case 4:// Comprar producto
                        System.out.print("Ingresa el ID del producto: ");

                        IdProducto = Integer.parseInt(br.readLine());
                        p = carr.getProducto(IdProducto);
                        if (p == null) {
                            p = (Producto) existencias.getProducto(IdProducto).clone();
                            if (!(p == null)) {
                                p.setCantidad(0);
                            }
                        }
                        if (!(p == null)) {
                            copiaProducto = (Producto) p.clone();
                            System.out.print("Ingresa la cantidad del producto: ");

                            copiaProducto.setCantidad(Integer.parseInt(br.readLine()));
                            oos.writeInt(2);
                            oos.flush();
                            oos.reset();
                            copiaProducto.setCantidad(p.getCantidad() + copiaProducto.getCantidad());
                            oos.writeObject(copiaProducto);
                            oos.flush();
                            oos.reset();
                            if (ois.readBoolean()) {
                                copiaProducto.setCantidad(copiaProducto.getCantidad() - p.getCantidad());
                                carr.addProduct(copiaProducto);
                                System.out.println("Articulo agregado al carrito");

                            } else {
                                System.out.println("No hay suficientes existencias");

                            }
                        } else {
                            System.out.println("El producto no existe");

                        }
                        break;
                    case 5:// Modificar carrito
                        boolean sePuedeSalir2 = false;
                        while (!sePuedeSalir2) {
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
                                    p = carr.getProducto(IdProducto);

                                    if (!(p == null)) {
                                        p = (Producto) p.clone();
                                        System.out.print("Ingresa la cantidad del producto: ");

                                        p.setCantidad(Integer.parseInt(br.readLine()));
                                        oos.writeInt(2);
                                        oos.flush();
                                        oos.reset();
                                        oos.writeObject(p);
                                        oos.flush();
                                        oos.reset();
                                        if (ois.readBoolean()) {
                                            carr.removeProduct(p.getIdProducto(), false);
                                            carr.addProduct(p);
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
                                    p = carr.getProducto(IdProducto);
                                    if (!(p == null)) {
                                        carr.removeProduct(IdProducto, false);
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
                    case 6:
                        // Finalizar compra
                        oos.writeInt(3);
                        oos.flush();
                        oos.reset();
                        oos.writeObject(carr);
                        oos.flush();
                        oos.reset();
                        if (ois.readBoolean()) {
                            carr.generateTicket();
                            carr = new CarritoDeCompra();
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
                        oos.reset();
                        break;
                    default:
                        System.out.println("Opcion invalida");

                        break;
                }
                oos.reset();

            }
        } else {
            System.out.println("Pues no puedes comprar gg");
        }
    }

    private void muestraImagen(File archivo) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(archivo);
        }
    }

    public void gestionaAbastecedorCliente(ObjectInputStream ois, ObjectOutputStream oos, BufferedReader br)
            throws IOException, ClassNotFoundException {
        System.out.print("Ingresa la contraseña: ");
        String pass = br.readLine().trim();
        oos.writeUTF(pass);
        oos.flush();
        oos.reset();
        if (ois.readBoolean()) {
            boolean sePuedeSalir = false;
            Producto p;
            CarritoDeCompra carr = (CarritoDeCompra) ois.readObject();
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
                    case 1:
                        oos.writeInt(1);
                        oos.flush();
                        oos.reset();
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
                        oos.reset();
                        oos.writeInt(p.getCantidad());
                        oos.flush();
                        oos.reset();
                        oos.writeDouble(p.getPrecio());
                        oos.flush();
                        oos.reset();
                        oos.writeUTF(p.getDescripcion());
                        oos.flush();
                        oos.reset();
                        System.out.println("Producto registrado");
                        deboLeer = true;
                        break;
                    case 2:
                        System.out.print("Ingresa el ID del producto: ");
                        p = carr.getProducto(Integer.parseInt(br.readLine().trim()));
                        if (!(p == null)) {
                            oos.writeInt(2);
                            oos.flush();
                            oos.reset();
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
                            oos.reset();
                            oos.writeUTF(p.getNombre());
                            oos.flush();
                            oos.reset();
                            oos.writeInt(p.getCantidad());
                            oos.flush();
                            oos.reset();
                            oos.writeDouble(p.getPrecio());
                            oos.flush();
                            oos.reset();
                            oos.writeUTF(p.getDescripcion());
                            oos.flush();
                            oos.reset();
                            System.out.println("Producto actualizado");
                        } else {
                            System.out.println("ID invalido");
                        }
                        deboLeer = true;
                        break;
                    case 3:
                        System.out.println("Ingresa el ID del producto");
                        p = carr.getProducto(Integer.parseInt(br.readLine().trim()));
                        if (!(p == null)) {
                            oos.writeInt(3);
                            oos.writeInt(p.getIdProducto());
                            oos.flush();
                            oos.reset();
                            System.out.println("Producto eliminado");
                        } else {
                            System.out.println("ID invalido");
                        }
                        deboLeer = true;
                        break;
                    case 4:
                        System.out.println(carr);
                        deboLeer = false;
                        break;
                    case 5:
                        sePuedeSalir = true;
                        oos.writeInt(4);
                        oos.flush();
                        oos.reset();
                        deboLeer = true;
                        break;
                    default:
                        deboLeer = false;
                        System.out.println("Opcion invalida");
                        break;
                }
                if (deboLeer) {
                    carr = (CarritoDeCompra) ois.readObject();
                }
                oos.reset();

            }
        } else {
            System.out.println("Contraseña incorrecta");
        }
    }

}
