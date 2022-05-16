package objetos;

import java.awt.Desktop;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class CarritoDeCompra implements Externalizable { // Usamos Externalizable para poder escribir el ArrayList

    private ArrayList<Producto> productos;
    private double total;

    public CarritoDeCompra() { // Constructor default
        productos = new ArrayList<Producto>();
        total = 0.0;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public double getTotal() {
        return total;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    private void actualizaTotal() { // Metodo para actualizar el total del carrito, iterando todos los elementos
        total = 0.0;
        for (Iterator iterator = productos.iterator(); iterator.hasNext();) {
            Producto p = (Producto) iterator.next();
            total += p.getCantidad() * p.getPrecio();
        }
    }

    public void generateTicket() throws IOException { // Metodo que genera el ticket del carrito de compra
        PDDocument document = new PDDocument(); // Creamos un objeto del PDF
        PDPage pagina = new PDPage(); // Creamos una pagina que agregar al PDF
        document.addPage(pagina);// Lo agregamos
        PDPageContentStream contentStream = new PDPageContentStream(
                document, pagina); // Creamos un stream para la pagina
        contentStream.beginText(); // Decimos que vamos a empezar a escribir
        contentStream.setFont(PDType1Font.COURIER, 10); // Configuramos la tipografia y el tamaño de letra
        contentStream.setLeading(14.5f); // Configuramos el leading
        contentStream.newLineAtOffset(25, 700); // Le decimos donde vamos a empezar a escribir el texto
        SimpleDateFormat dsf = new SimpleDateFormat(
                "EEEE dd MMM yyyy", Locale.getDefault()); // Creamos un DateFormat, solo para el ticket
        contentStream.showText("Fecha: " + dsf.format(new Date())); // Escribimos la fecha
        contentStream.newLine(); // Nueva linea
        contentStream.showText("Producto       Cantidad       Precio       SubTotal"); // Escribimos las cabeceras
        contentStream.newLine(); // Nueva linea
        for (Iterator it = productos.iterator(); it.hasNext();) { // Iteramos todos los productos
            Producto p = (Producto) it.next();
            contentStream.showText(
                    p.getNombre()
                            .substring(0, Math.min(12, p.getNombre().length())) // Ponemos el titulo del producto o solo
                                                                                // 12 caracteres
                            + "   "
                            + p.getCantidad() + "    " + p.getPrecio() + "    "// Mostramos la cantidad y el precio
                            + (p.getCantidad() * p.getPrecio())); // Y el subtotal del producto
            contentStream.newLine(); // Nueva linea
        }
        contentStream.showText("Total:            " + total); // Total
        contentStream.endText(); // Finalizamos el texto
        contentStream.close();// Cerramos el stream
        dsf = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss"); // Creamos el nuevo formato para guardar el archivo
        String ruta = "Ticket" + dsf.format(new Date()) + ".pdf"; // Nombre del ticket
        document.save(ruta);// Lo guardamos
        document.close(); // Cerramos el documento
        if (Desktop.isDesktopSupported()) { // Si Desktop esta disponible
            Desktop.getDesktop().open(new File(ruta)); // Abrimos el PDF
        }

    }

    public void addProduct(Producto nuevoProducto) { // Agregamos un producto al carrito de compra
        boolean seActualizo = false;
        for (int i = 0; i < productos.size(); i++) {
            Producto p = (Producto) productos.get(i);
            if (nuevoProducto.getIdProducto() == p.getIdProducto()) { // Checamos si ya esta registrado
                p.setCantidad(p.getCantidad() + nuevoProducto.getCantidad()); // Actualizamos la cantidad
                seActualizo = true;
                break;
            }
        }
        if (!seActualizo) { // Si no lo encontro
            productos.add(nuevoProducto); // Lo agregamos al ArrayList
        }
        actualizaTotal(); // Actualizamos el total
    }

    public void removeProduct(int idProducto, boolean reIndex) { // Quitamos el producto del IDProducto y le pasamos si
                                                                 // debemos reindexar
        for (int i = 0; i < productos.size(); i++) { // Iteramos todos los productos
            Producto get = (Producto) productos.get(i);
            if (get.getIdProducto() == idProducto) { // Si el producto es el mismo
                productos.remove(get); // Lo eliminamos
                break;
            }
        }
        if (reIndex) // SI hay que reindexar
            for (int i = 0; i < productos.size(); i++) {
                Producto get = (Producto) productos.get(i);
                get.setIdProducto(i + 1); // Cambiamos el ID
            }
        actualizaTotal(); // Actualizamos el total
    }

    public Producto getProducto(int idProducto) { // Obtenemos el producto dado el ID
        Producto p = null;
        for (int i = 0; i < productos.size(); i++) {
            Producto get = (Producto) productos.get(i);
            if (get.getIdProducto() == idProducto) { // SI el producto es igual al ID
                p = get;
                break;
            }
        }
        return p;// Lo regresamos
    }

    @Override
    public String toString() { // Sobreescribimos el toString para imprimir el carrito
        String eh = "";
        ArrayList<String> result = new ArrayList<>();
        for (Object objP : productos) {
            Producto p = (Producto) objP;
            result.add(p.toString());
        }
        eh = String.join(",\n", result);
        return "CarritoDeCompra{\n" + "productos=" + eh + ", \ntotal=" + total + '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException { // Metodo para clonar el carrito
        CarritoDeCompra nuevo = new CarritoDeCompra();
        for (Iterator iterator = productos.iterator(); iterator.hasNext();) {
            Producto p = (Producto) iterator.next();
            Producto nuevoP = (Producto) p.clone();
            nuevo.addProduct(nuevoP);
        }
        return nuevo;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException { // Metodo para escribir el objeto
        out.writeDouble(total);
        out.writeInt(productos.size());
        for (Producto producto : productos) {
            out.writeObject(producto);
        }

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException { // Metodo para leer el objeto
        this.total = in.readDouble();
        int cantidad = in.readInt();
        productos = new ArrayList<Producto>();
        for (int i = 0; i < cantidad; i++) {
            productos.add((Producto) in.readObject());
        }
    }

}
