import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

public class Mensaje implements Externalizable {
    private String destino;
    private int numeroDeIntentos;
    private boolean fueEnviado;
    private Date fechaDeEnvio;
    private double tiempoEnMinutos;
    private File archivoAdjunto;

    public String getdestino() {
        return destino;
    }

    public Date getFechaDeEnvio() {
        return fechaDeEnvio;
    }

    public int getNumeroDeIntentos() {
        return numeroDeIntentos;
    }

    public double getTiempoEnMinutos() {
        return tiempoEnMinutos;
    }

    public File getArchivoAdjunto() {
        return archivoAdjunto;
    }

    public boolean isFueEnviado() {
        return fueEnviado;
    }

    public void setArchivoAdjunto(File archivoAdjunto) {
        this.archivoAdjunto = archivoAdjunto;
    }

    public void setdestino(String destino) {
        this.destino = destino;
    }

    public void setFechaDeEnvio(Date fechaDeEnvio) {
        this.fechaDeEnvio = fechaDeEnvio;
    }

    public void setFueEnviado(boolean fueEnviado) {
        this.fueEnviado = fueEnviado;
    }

    public void setNumeroDeIntentos(int numeroDeIntentos) {
        this.numeroDeIntentos = numeroDeIntentos;
    }

    public void setTiempoEnMinutos(double tiempoEnMinutos) {
        this.tiempoEnMinutos = tiempoEnMinutos;
    }

    @Override
    public void writeExternal(ObjectOutput out)
            throws IOException {
        out.writeObject(destino); // Seleccionamos el destino para que sea serializado
        out.writeObject(fechaDeEnvio); // Seleccionamos la fecha de envio para que sea serializado
        out.writeInt(numeroDeIntentos); // Seleccionamos el numero de intentos para que sea serializado

    }

    @Override
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {
        this.destino = (String) in.readObject(); // Lo deserializamos en el mismo orden que lo serializamos
        this.fechaDeEnvio = (Date) in.readObject();
        this.numeroDeIntentos = in.readInt();

    }

    @Override
    public String toString() {
        return "Mensaje=(\n" +
                "destino=" + destino + "\n" +
                "numeroDeIntentos=" + numeroDeIntentos + "\n" +
                "fueEnviado=" + fueEnviado + "\n" +
                "fechaDeEnvio=" + (!(fechaDeEnvio == null)
                        ? fechaDeEnvio.toString()
                        : "null")
                + "\n" +
                "tiempoEnMinutos=" + tiempoEnMinutos + "\n" +
                "archivoAdjunto=" + (!(archivoAdjunto == null)
                        ? archivoAdjunto.toString()
                        : "null")
                + "\n" +
                ")\n";
    }

}
