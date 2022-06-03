public class EjemploRunnable implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) { // Iteramos 10 veces
            System.out.println(i + " " +
                    Thread.currentThread().getName()); // Mostrarmos el numero de iteracion y mandamos a llamar la
                                                       // funcion que devuelve el nombre del hilo
        }
        System.out.println("Termina thread: "
                + Thread.currentThread().getName()); // Mostramos que termino el hilo y su nombre
    }

    public static void main(String[] args) {
        new Thread(new EjemploRunnable(),
                "Luis").start(); // Creamos un nuevo objeto de tipo Thread y lo iniciamos con el nombre de Luis
        new Thread(new EjemploRunnable(),
                "Enrique").start();// Creamos un nuevo objeto de tipo Thread y lo iniciamos con el nombre de
                                   // Enrique
        System.out.println("Termina thread main");
    }

}
