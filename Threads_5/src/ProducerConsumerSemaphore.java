import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

class ProducerConsumerSemaphore {
    private static final int BUFFER_SIZE = 5;
    private static final Queue<String> buffer = new LinkedList<>();
    private static final Semaphore empty = new Semaphore(BUFFER_SIZE);
    private static final Semaphore full = new Semaphore(0);

    public static void main(String[] args) {
        String inputFileName = "input.txt";
        String outputFileName = "output.txt";

        // Измерение времени начала
        long startTime = System.currentTimeMillis();

        Thread producer = new Thread(new Producer(inputFileName));
        Thread consumer = new Thread(new Consumer(outputFileName));

        producer.start();
        consumer.start();

        try {
            producer.join(); // Ожидание завершения потока producer
            consumer.join(); // Ожидание завершения потока consumer
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Измерение времени окончания
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Время выполнения программы: " + duration + " миллисекунд");
    }

    static class Producer implements Runnable {
        private String inputFileName;

        public Producer(String inputFileName) {
            this.inputFileName = inputFileName;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split("\\s+");
                    for (String word : words) {
                        empty.acquire(); // Ожидание свободного места
                        buffer.add(word);
                        full.release(); // Увеличение счетчика заполненных мест
                    }
                }
                // Завершение работы
                empty.acquire(); // Для завершения
                buffer.add(null); // Отправка сигнала завершения
                full.release();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Consumer implements Runnable {
        private String outputFileName;

        public Consumer(String outputFileName) {
            this.outputFileName = outputFileName;
        }

        @Override
        public void run() {
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFileName))) {
                while (true) {
                    full.acquire(); // Ожидание заполненного места
                    String word = buffer.poll();
                    empty.release(); // Увеличение счетчика свободных мест

                    if (word == null) { // Проверка на завершение
                        break;
                    }

                    String reversedWord = new StringBuilder(word).reverse().toString();
                    writer.println(reversedWord);
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}