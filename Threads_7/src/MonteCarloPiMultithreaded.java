import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MonteCarloPiMultithreaded {
    private static final int NUM_SAMPLES_PER_THREAD = 250000;
    private static final int NUM_THREADS = 4;
    private static BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(NUM_THREADS);

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < NUM_THREADS; i++) {
            new Thread(new PiCalculator()).start();
        }

        int insideCircle = 0;
        for (int i = 0; i < NUM_THREADS; i++) {
            insideCircle += queue.take();
        }

        double piEstimate = 4.0 * insideCircle / (NUM_SAMPLES_PER_THREAD * NUM_THREADS);
        System.out.println("Оценка числа Пи: " + piEstimate);
    }

    static class PiCalculator implements Runnable {
        @Override
        public void run() {
            int insideCircle = 0;
            Random random = new Random();

            for (int i = 0; i < NUM_SAMPLES_PER_THREAD; i++) {
                double x = random.nextDouble();
                double y = random.nextDouble();
                if (x * x + y * y <= 1) {
                    insideCircle++;
                }
            }

            try {
                queue.put(insideCircle);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}