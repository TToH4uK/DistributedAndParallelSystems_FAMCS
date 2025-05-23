import java.util.ArrayList;
import java.util.List;

class ThreadTest3 {

    private final static int MAX = 1_000_000;
    private static final List<String> results = new ArrayList<>();
    private static final int NUM_THREADS = 4;

    private static class CountPrimesThread extends Thread {
        int id;
        int start;

        public CountPrimesThread(int id, int start) {
            this.id = id;
            this.start = start;
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            int count = countPrimes(start);
            long elapsedTime = System.currentTimeMillis() - startTime;

            synchronized (results) {
                results.add("Thread " + id + " counted " + count + " primes in " + (elapsedTime / 1000.0) + " seconds.");
            }
        }
    }

    public static void main(String[] args) {
        int chunkSize = MAX / (NUM_THREADS * 10); // Smaller chunks for better load balancing
        List<CountPrimesThread> threads = new ArrayList<>();

        System.out.println("\nCreating " + NUM_THREADS + " prime-counting threads...");

        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * chunkSize + 1; // Start point for each thread
            for (int j = 0; j < 10; j++) { // 10 sub-tasks for each thread
                int taskStart = start + j * chunkSize;
                if (taskStart > MAX) break; // Prevent overflow
                CountPrimesThread thread = new CountPrimesThread(i, taskStart);
                threads.add(thread);
                thread.start();
            }
        }

        for (CountPrimesThread thread : threads) {
            try {
                thread.join(); // Wait for each thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        for (String result : results) {
            System.out.println(result);
        }

        System.out.println("All threads have completed.");
    }

    private static int countPrimes(int start) {
        int count = 0;
        int end = start + (MAX / NUM_THREADS / 10); // Define the range for the current task
        for (int i = start; i < end && i <= MAX; i++) {
            if (isPrime(i))
                count++;
        }
        return count;
    }

    private static boolean isPrime(int x) {
        if (x < 2) return false;
        int top = (int) Math.sqrt(x);
        for (int i = 2; i <= top; i++)
            if (x % i == 0)
                return false;
        return true;
    }
}