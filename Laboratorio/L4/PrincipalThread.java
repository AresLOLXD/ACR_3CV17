public class PrincipalThread {
    public static void main(String[] args) {
        new CuentaThread(true).start(); // Creo un objeto Thread que cuente los pares y lo inicio
        new CuentaThread(false).start();// Creo un objeto Thread que cuente los impares y lo inicio
    }
}
