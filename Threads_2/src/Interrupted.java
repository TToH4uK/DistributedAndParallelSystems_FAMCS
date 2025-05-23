import java.util.Scanner;

/**
 * A program that counts prime numbers in a single computational thread.
 * The main thread allows the user to interrupt the computation.
 */
class ThreadTestInterrupt {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Thread primeCounterThread = new Thread(() -> countPrimes(1, 100_000_000));

        primeCounterThread.start();

        System.out.println("Press Enter to stop the computation...");
        scanner.nextLine(); // Wait for user input to stop

        primeCounterThread.interrupt(); // Interrupt the computation thread

        try {
            primeCounterThread.join(); // Wait for the thread to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Computation stopped. Exiting.");
        scanner.close(); // Close the scanner to prevent resource leaks
    }

    private static void countPrimes(int min, int max) {
        int count = 0;

        for (int i = min; i <= max; i++) {
            if (Thread.currentThread().isInterrupted()) {
                break; // Exit if the thread is interrupted
            }
            if (isPrime(i)) {
                count++;
            }
            if (i % 100_000 == 0) { // Periodically check for interruption
                System.out.println("Checked up to: " + i);
            }
        }
        System.out.println("Total primes counted: " + count);
    }

    private static boolean isPrime(int x) {
        if (x <= 1) return false;
        int top = (int) Math.sqrt(x);
        for (int i = 2; i <= top; i++)
            if (x % i == 0) return false;
        return true;
    }
}