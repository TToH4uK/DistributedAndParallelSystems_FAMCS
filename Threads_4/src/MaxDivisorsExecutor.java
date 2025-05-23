import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MaxDivisorsExecutor {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Start the timer

        int maxNumber = 1_000_000; // The upper limit for our search
        int numberOfThreads = 10; // Number of threads in the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        List<Future<Result>> futures = new ArrayList<>();

        // Submit tasks to the executor
        for (int i = 1; i <= maxNumber; i++) {
            int number = i;
            futures.add(executor.submit(new Callable<Result>() {
                @Override
                public Result call() {
                    int count = countDivisors(number);
                    return new Result(number, count);
                }
            }));
        }

        // Find the maximum number of divisors and the corresponding integer
        Result maxResult = new Result(0, 0);
        try {
            for (Future<Result> future : futures) {
                Result result = future.get(); // Blocking call to get the result
                if (result.divisorCount > maxResult.divisorCount) {
                    maxResult = result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Shut down the ExecutorService
        }

        // Output the result
        System.out.println("\nThe largest number of divisors " +
                "for numbers between 1 and " + maxNumber + " is " + maxResult.divisorCount);
        System.out.println("An integer with that many divisors is " +
                maxResult.number);

        long endTime = System.currentTimeMillis(); // End the timer
        long duration = endTime - startTime; // Calculate duration
        System.out.println("Total elapsed time: " +
                (duration / 1000.0) + " seconds.\n");
    }

    // Method to count the number of divisors of a given number
    private static int countDivisors(int n) {
        int count = 0;
        for (int i = 1; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                count += (i * i == n) ? 1 : 2; // Count both divisor and complement
            }
        }
        return count;
    }

    // Class to hold the result of the divisor count
    static class Result {
        int number;
        int divisorCount;

        Result(int number, int divisorCount) {
            this.number = number;
            this.divisorCount = divisorCount;
        }
    }
}