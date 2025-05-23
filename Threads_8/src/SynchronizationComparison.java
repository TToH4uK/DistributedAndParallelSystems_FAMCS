import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizationComparison {

    static class CoarseGrainedCounter {
        private int count = 0;

        public synchronized void increment() {
            count++;
        }

        public synchronized int getCount() {
            return count;
        }
    }

    static class FineGrainedCounter {
        private final Lock lock = new ReentrantLock();
        private int count = 0;

        public void increment() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }

        public int getCount() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int NUM_THREADS = 100;
        final int INCREMENTS = 1000;

        // Грубая синхронизация
        CoarseGrainedCounter coarseCounter = new CoarseGrainedCounter();
        long startTime = System.nanoTime();
        Thread[] coarseThreads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            coarseThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS; j++) {
                    coarseCounter.increment();
                }
            });
            coarseThreads[i].start();
        }

        for (Thread thread : coarseThreads) {
            thread.join();
        }
        long endTime = System.nanoTime();
        long coarseDuration = endTime - startTime;
        System.out.println("Coarse-grained count: " + coarseCounter.getCount());
        System.out.println("Coarse-grained time (ms): " + coarseDuration / 1_000_000.0);
        System.out.println("Coarse-grained throughput (increments/ms): " + (NUM_THREADS * INCREMENTS) / (coarseDuration / 1_000_000.0));

        // Тонкая синхронизация
        FineGrainedCounter fineCounter = new FineGrainedCounter();
        startTime = System.nanoTime();
        Thread[] fineThreads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            fineThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS; j++) {
                    fineCounter.increment();
                }
            });
            fineThreads[i].start();
        }

        for (Thread thread : fineThreads) {
            thread.join();
        }
        endTime = System.nanoTime();
        long fineDuration = endTime - startTime;
        System.out.println("Fine-grained count: " + fineCounter.getCount());
        System.out.println("Fine-grained time (ms): " + fineDuration / 1_000_000.0);
        System.out.println("Fine-grained throughput (increments/ms): " + (NUM_THREADS * INCREMENTS) / (fineDuration / 1_000_000.0));
    }
}