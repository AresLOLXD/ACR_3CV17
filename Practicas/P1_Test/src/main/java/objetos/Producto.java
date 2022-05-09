package objetos;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Producto implements Externalizable {

    public Producto(int idProducto, String nombre, int cantidad, double precio, String descripcion) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public Producto() {
    }

    private int idProducto;
    private String nombre;
    private int cantidad;
    private double precio;
    private String descripcion;

    public int getCantidad() {
        return cantidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Producto{\nidProducto=").append(idProducto);
        sb.append(",\n nombre=").append(nombre);
        sb.append(",\n cantidad=").append(cantidad);
        sb.append(",\n precio=").append(precio);
        sb.append(",\n descripcion=").append(descripcion);
        sb.append('}');
        return sb.toString();
    }

    public ArrayList<File> obtenerImagenes() {
        ArrayList<File> imagenes = new ArrayList<File>();
        Path path = Paths.get("./Imagenes/");
        File a1, a2, a3, a4;
        if (Files.exists(path)) {
            a1 = new File("./Imagenes/" + idProducto + "_1.png");
            a2 = new File("./Imagenes/" + idProducto + "_2.png");
            a3 = new File("./Imagenes/" + idProducto + "_3.png");
            a4 = new File("./Imagenes/" + idProducto + "_4.png");
        } else {
            a1 = new File("./" + idProducto + "_1.png");
            a2 = new File("./" + idProducto + "_2.png");
            a3 = new File("./" + idProducto + "_3.png");
            a4 = new File("./" + idProducto + "_4.png");
        }
        imagenes.add(a1);
        imagenes.add(a2);
        imagenes.add(a3);
        imagenes.add(a4);
        return imagenes;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Producto nuevo = new Producto(this.idProducto, this.nombre, this.cantidad, this.precio, this.descripcion);
        return nuevo;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(idProducto);
        out.writeUTF(nombre);
        out.writeUTF(descripcion);
        out.writeInt(cantidad);
        out.writeDouble(precio);

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.idProducto = in.readInt();
        this.nombre = in.readUTF();
        this.descripcion = in.readUTF();
        this.cantidad = in.readInt();
        this.precio = in.readDouble();

    }

}
