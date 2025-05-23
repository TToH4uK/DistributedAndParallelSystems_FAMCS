import java.util.Random;

public class MonteCarloPi {
    public static void main(String[] args) {
        int numSamples = 1000000; // Количество сгенерированных точек
        int insideCircle = 0;
        Random random = new Random();

        for (int i = 0; i < numSamples; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            if (x * x + y * y <= 1) {
                insideCircle++;
            }
        }

        double piEstimate = 4.0 * insideCircle / numSamples;
        System.out.println("Оценка числа Пи: " + piEstimate);
    }
}