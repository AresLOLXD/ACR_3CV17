package objetos;

import java.awt.Desktop;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class CarritoDeCompra implements Externalizable {

    private ArrayList<Producto> productos;
    private double total;

    public CarritoDeCompra() {
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

    private void actualizaPrecio() {
        total = 0.0;
        for (Iterator iterator = productos.iterator(); iterator.hasNext();) {
            Producto p = (Producto) iterator.next();
            total += p.getCantidad() * p.getPrecio();
        }
    }

    public void generateTicket() throws IOException {
        PDDocument document = new PDDocument();
        PDPage pagina = new PDPage();
        document.addPage(pagina);
        PDPageContentStream contentStream = new PDPageContentStream(document, pagina);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.COURIER, 10);
        contentStream.setLeading(14.5f);
        contentStream.newLineAtOffset(25, 700);
        SimpleDateFormat dsf = new SimpleDateFormat("EEEE dd MMM yyyy", Locale.getDefault());
        contentStream.showText("Fecha: " + dsf.format(new Date()));
        contentStream.newLine();
        contentStream.showText("Producto       Cantidad       Precio       SubTotal");
        contentStream.newLine();
        for (Iterator it = productos.iterator(); it.hasNext();) {
            Producto p = (Producto) it.next();
            contentStream.showText(p.getNombre().substring(0, Math.min(12, p.getNombre().length()))
                    + "   "
                    + p.getCantidad() + "    " + p.getPrecio() + "    "
                    + (p.getCantidad() * p.getPrecio()));
            contentStream.newLine();
        }
        contentStream.showText("Total:            " + total);
        contentStream.endText();
        contentStream.close();
        dsf = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
        String ruta = "Ticket" + dsf.format(new Date()) + ".pdf";
        document.save(ruta);
        document.close();
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(ruta));
        }

    }

    public void addProduct(Producto nuevoProducto) {
        boolean seActualizo = false;
        for (int i = 0; i < productos.size(); i++) {
            Producto p = (Producto) productos.get(i);
            if (nuevoProducto.getIdProducto() == p.getIdProducto()) {
                p.setCantidad(p.getCantidad() + nuevoProducto.getCantidad());
                seActualizo = true;
                break;
            }
        }
        if (!seActualizo) {
            productos.add(nuevoProducto);
        }
        actualizaPrecio();
    }

    public void removeProduct(int idProducto, boolean reIndex) {
        for (int i = 0; i < productos.size(); i++) {
            Producto get = (Producto) productos.get(i);
            if (get.getIdProducto() == idProducto) {
                productos.remove(get);
                break;
            }
        }
        if (reIndex)
            for (int i = 0; i < productos.size(); i++) {
                Producto get = (Producto) productos.get(i);
                get.setIdProducto(i + 1);
            }
        actualizaPrecio();
    }

    public Producto getProducto(int idProducto) {
        Producto p = null;
        for (int i = 0; i < productos.size(); i++) {
            Producto get = (Producto) productos.get(i);
            if (get.getIdProducto() == idProducto) {
                p = get;
                break;
            }
        }
        return p;
    }

    @Override
    public String toString() {
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
    protected Object clone() throws CloneNotSupportedException {
        CarritoDeCompra nuevo = new CarritoDeCompra();
        for (Iterator iterator = productos.iterator(); iterator.hasNext();) {
            Producto p = (Producto) iterator.next();
            Producto nuevoP = (Producto) p.clone();
            nuevo.addProduct(nuevoP);
        }
        return nuevo;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(total);
        out.writeInt(productos.size());
        for (Producto producto : productos) {
            out.writeObject(producto);
        }

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.total = in.readDouble();
        int cantidad = in.readInt();
        productos = new ArrayList<Producto>();
        for (int i = 0; i < cantidad; i++) {
            productos.add((Producto) in.readObject());
        }
    }

}
