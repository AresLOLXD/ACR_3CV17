
public class EjemploThread extends Thread { // Usamos herencia de la clase Thread
    public EjemploThread(String nombre) { // Creamos un constructor que reciba el nombre del hilo
        super(nombre); // Mandamos a llamar el constructor de la clase padre
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) { // Iteramos 10 veces
            System.out.println(i + " " + getName()); // Mostrarmos el numero de iteracion y mandamos a llamar la funcion
                                                     // que devuelve el nombre del hilo
        }
        System.out.println("Termina thread: " + getName()); // Mostramos que termino el hilo y su nombre
    }

    public static void main(String[] args) {
        new EjemploThread("Luis").start(); // Creamos un nuevo objeto de tipo Thread y lo iniciamos con el nombre de
                                           // Luis
        new EjemploThread("Enrique").start();// Creamos un nuevo objeto de tipo Thread y lo iniciamos con el nombre de
                                             // Enrique
        System.out.println("Termina thread main");
    }

}