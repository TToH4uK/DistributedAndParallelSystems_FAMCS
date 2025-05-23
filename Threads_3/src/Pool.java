import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class ThreadTest2 {

    private final static int MAX = 100_000_000;
    private static final List<String> results = new ArrayList<>();
    private static final List<CountPrimesThread> workers = new ArrayList<>();

    private static class CountPrimesThread extends Thread {
        int id;
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
                results.add("Thread " + id + " counted " + count + " primes in " + (elapsedTime / 1000.0) + " seconds.");
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numberOfThreads = 0;

        while (numberOfThreads < 1 || numberOfThreads > 30) {
            System.out.print("How many threads do you want to use (from 1 to 30)? ");
            numberOfThreads = scanner.nextInt();
        }

        int rangeSize = MAX / numberOfThreads;
        System.out.println("\nCreating " + numberOfThreads + " prime-counting threads...");

        for (int i = 0; i < numberOfThreads; i++) {
            int min = i * rangeSize + 1;
            int max = (i + 1) == numberOfThreads ? MAX : (i + 1) * rangeSize;
            CountPrimesThread worker = new CountPrimesThread(i, min, max);
            workers.add(worker);
            worker.start();
        }

        for (CountPrimesThread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        for (String result : results) {
            System.out.println(result);
        }

        System.out.println("All threads have completed.");
        scanner.close();
    }

    private static int countPrimes(int min, int max) {
        int count = 0;
        for (int i = min; i <= max; i++) {
            if (isPrime(i))
                count++;
        }
        return count;
    }

    private static boolean isPrime(int x) {
        assert x > 1;
        int top = (int) Math.sqrt(x);
        for (int i = 2; i <= top; i++)
            if (x % i == 0)
                return false;
        return true;
    }
}