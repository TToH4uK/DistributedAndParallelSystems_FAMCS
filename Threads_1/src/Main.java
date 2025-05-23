import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A program that starts several threads, each of which performs the
 * same computation. The user specifies the number of threads. The
 * point is to see that the threads finish in an indeterminate order.
 */
class ThreadTest1 {

    private final static int MAX = 1_000_000;
    private static final List<String> results = new ArrayList<>();

    /**
     * When a thread belonging to this class is run it will count the
     * number of primes in its assigned range. It will store the result
     * in a shared list after the computation.
     */
    private static class CountPrimesThread extends Thread {
        int id;  // An id number for this thread; specified in the constructor.
        int min, max;

        public CountPrimesThread(int id, int min, int max) {
            this.id = id;
            this.min = min;
            this.max = max;
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            int count = countPrimes(min, max);
            long elapsedTime = System.currentTimeMillis() - startTime;

            synchronized (results) {
                results.add("Thread " + id + " counted " +
                        count + " primes in " + (elapsedTime / 1000.0) + " seconds.");
            }
        }
    }

    /**
     * Start several CountPrimesThreads. The number of threads, between 1 and 30,
     * is specified by the user.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numberOfThreads = 0;

        while (numberOfThreads < 1 || numberOfThreads > 30) {
            System.out.print("How many threads do you want to use (from 1 to 30)? ");
            numberOfThreads = scanner.nextInt();
            if (numberOfThreads < 1 || numberOfThreads > 30)
                System.out.println("Please enter a number between 1 and 30!");
        }

        int rangeSize = MAX / numberOfThreads;
        System.out.println("\nCreating " + numberOfThreads + " prime-counting threads...");
        CountPrimesThread[] workers = new CountPrimesThread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            int min = i * rangeSize + 1; // Start from 1 to MAX
            int max = (i + 1) == numberOfThreads ? MAX : (i + 1) * rangeSize; // Handle the last thread
            workers[i] = new CountPrimesThread(i, min, max);
        }

        for (int i = 0; i < numberOfThreads; i++) {
            workers[i].start();
        }

        // Wait for all threads to finish
        for (int i = 0; i < numberOfThreads; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Output results after all threads have finished
        for (String result : results) {
            System.out.println(result);
        }

        System.out.println("All threads have completed.");
        scanner.close(); // Close the scanner to prevent resource leaks
    }

    /**
     * Compute and return the number of prime numbers in the range
     * min to max, inclusive.
     */
    private static int countPrimes(int min, int max) {
        int count = 0;
        for (int i = min; i <= max; i++) {
            if (isPrime(i))
                count++;
        }
        return count;
    }

    /**
     * Test whether x is a prime number.
     * x is assumed to be greater than 1.
     */
    private static boolean isPrime(int x) {
        assert x > 1;
        int top = (int) Math.sqrt(x);
        for (int i = 2; i <= top; i++)
            if (x % i == 0)
                return false;
        return true;
    }

} // end class ThreadTest1