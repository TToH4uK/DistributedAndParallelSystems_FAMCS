import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class DivisorTask implements Runnable {
    private final int number;
    private final BlockingQueue<Integer> resultQueue;

    public DivisorTask(int number, BlockingQueue<Integer> resultQueue) {
        this.number = number;
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {
        int count = countDivisors(number);
        try {
            resultQueue.put(count);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int countDivisors(int n) {
        int count = 0;
        for (int i = 1; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                count += (i * i == n) ? 1 : 2; // Count both divisor and complement
            }
        }
        return count;
    }
}

class ThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final BlockingQueue<Integer> resultQueue;
    private final int numberOfThreads;
    private final Thread[] workers;

    public ThreadPool(int numberOfThreads, BlockingQueue<Integer> resultQueue) {
        this.numberOfThreads = numberOfThreads;
        this.resultQueue = resultQueue;
        this.taskQueue = new LinkedBlockingQueue<>();
        workers = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            workers[i] = new Thread(() -> {
                try {
                    while (true) {
                        Runnable task = taskQueue.take();
                        task.run();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            workers[i].start();
        }
    }

    public void submit(Runnable task) {
        taskQueue.offer(task);
    }

    public void shutdown() {
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }
}

public class MaxDivisors {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis(); // Start the timer

        int maxNumber = 10_000_000; // Replace with your upper limit
        int numberOfThreads = 4;
        BlockingQueue<Integer> resultQueue = new LinkedBlockingQueue<>();
        ThreadPool threadPool = new ThreadPool(numberOfThreads, resultQueue);

        int maxDivisorCount = 0;
        int intWithMaxDivisorCount = 0;

        for (int i = 1; i <= maxNumber; i++) {
            threadPool.submit(new DivisorTask(i, resultQueue));
        }

        for (int i = 1; i <= maxNumber; i++) {
            int divisors = resultQueue.take(); // Wait for results
            if (divisors > maxDivisorCount) {
                maxDivisorCount = divisors;
                intWithMaxDivisorCount = i;
            }
        }

        threadPool.shutdown();

        long endTime = System.currentTimeMillis(); // End the timer
        long elapsedTime = endTime - startTime; // Calculate elapsed time

        // Output in the specified format
        System.out.println("\nThe largest number of divisors " +
                "for numbers between 1 and " + maxNumber + " is " + maxDivisorCount);
        System.out.println("An integer with that many divisors is " +
                intWithMaxDivisorCount);
        System.out.println("Total elapsed time:  " +
                (elapsedTime / 1000.0) + " seconds.\n");
    }
}