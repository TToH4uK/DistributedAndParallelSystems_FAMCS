import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ThreadTest2 {

    private final static int MAX = 1_000_000;
    private static final List<String> results = new ArrayList<>();
    private static final int NUM_THREADS = 4;

    private static class CountPrimesTask implements Runnable {
        int id;
        int start;

        public CountPrimesTask(int id, int start) {
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
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        int chunkSize = MAX / (NUM_THREADS * 10); // Smaller chunks for better load balancing

        System.out.println("\nCreating " + NUM_THREADS + " prime-counting threads...");

        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * chunkSize + 1; // Start point for each thread
            for (int j = 0; j < 10; j++) { // 10 sub-tasks for each thread
                int taskStart = start + j * chunkSize;
                if (taskStart > MAX) break; // Prevent overflow
                CountPrimesTask task = new CountPrimesTask(i, taskStart);
                executor.execute(task);
            }
        }

        executor.shutdown();
        try {
            // Wait until all tasks are finished
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
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