import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PrimeCount {

    static class PrimeTask implements Callable<Integer> {
        private final int start;
        private final int end;

        public PrimeTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Integer call() {
            int count = 0;
            for (int i = start; i <= end; i++) {
                if (isPrime(i)) {
                    count++;
                }
            }
            return count;
        }

        private boolean isPrime(int number) {
            if (number <= 1) return false;
            for (int i = 2; i * i <= number; i++) {
                if (number % i == 0) return false;
            }
            return true;
        }
    }

    public static void main(String[] args) {
        int lowerBound = 1;  // Нижняя граница диапазона
        int upperBound = 100; // Верхняя граница диапазона
        int numberOfThreads = 4; // Количество потоков

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<Integer>> futures = new ArrayList<>();
        int rangeSize = (upperBound - lowerBound + 1) / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            int start = lowerBound + i * rangeSize;
            int end = (i == numberOfThreads - 1) ? upperBound : start + rangeSize - 1;
            futures.add(executor.submit(new PrimeTask(start, end)));
        }

        int totalPrimes = 0;
        for (Future<Integer> future : futures) {
            try {
                totalPrimes += future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error processing: " + e.getMessage());
            }
        }

        executor.shutdown();
        System.out.println("Total prime numbers between " + lowerBound + " and " + upperBound + ": " + totalPrimes);
    }
}