public class CuentaThread extends Thread {
    private final boolean cuentaPares;// Creamos una bandera para saber si debe contar los pares o los impares

    public CuentaThread(boolean cuentaPares) {
        this.cuentaPares = cuentaPares;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            if (cuentaPares && i % 2 == 0)// Si debe contar los pares y el numero es par, lo imprime
                System.out.println("Pares: " + i);
            else if (!cuentaPares && i % 2 == 1)// Si NO debe contar los pares y el numero es impar, lo imprime
                System.out.println("Impares: " + i);

        }
    }
}
