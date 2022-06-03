import java.util.Random;
import java.util.Vector;

public class VectorRandom extends Thread {
    public VectorRandom(String nombre) {
        super(nombre);
    }

    @Override
    public void run() {
        Random rand = new Random(); // Generamos un objeto Random
        Vector<Integer> vector = new Vector<>(); // Definimos un nuevo vector
        int longitud = Math.abs(rand.nextInt(20)); // Generamos la longitud, maximo 20
        for (int i = 0; i < longitud; i++) {
            vector.add(rand.nextInt(100)); // Agregamos valores al vector, maximo valor 100
        }
        System.out.println("Vector desde " + getName() + "\n" + vector); // Imprimimos desde cual Hilo se esta mostrando
                                                                         // el vector actual
        long suma = 0L, sumaCuadrados = 0L;
        double promedio = 0L;
        for (Integer numero : vector) { // Iteramos el vector para obtener la suma y la suma de los cuadrados
            suma += numero;
            sumaCuadrados += numero * numero;
        }
        promedio = ((double) suma) / ((double) longitud); // Casteamos la suma y la longitud y calculamos el promedio
        System.out.println("Suma " + getName() + ": " + suma);
        System.out.println("Suma de cuadrados " + getName() + ": " + sumaCuadrados);
        System.out.println("Promedio " + getName() + ": " + promedio);
    }

    public static void main(String[] args) {
        new VectorRandom("Primero").start(); // Creamos un Thread llamado Primero y lo ejecutamos
        new VectorRandom("Segundo").start();// Creamos un Thread llamado Segundo y lo ejecutamos
    }
}