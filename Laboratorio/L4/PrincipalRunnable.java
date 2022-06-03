public class PrincipalRunnable {
    public static void main(String[] args) {
        new Thread(new CuentaRunnable(true)).start(); // Creo un objeto Thread y le paso el objeto Runnable para contar
                                                      // los pares y lo inicio
        new Thread(new CuentaRunnable(false)).start();// Creo un objeto Thread y le paso el objeto Runnable para contar
                                                      // los impares y lo inicio
    }
}
