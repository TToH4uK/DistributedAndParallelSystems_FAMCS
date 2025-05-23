import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

class ProducerConsumerMutex {
    private static final int BUFFER_SIZE = 5;
    private static final Queue<String> buffer = new LinkedList<>();
    private static final Object lock = new Object();
    private static int itemCount = 0;

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
                        synchronized (lock) {
                            while (itemCount == BUFFER_SIZE) {
                                lock.wait(); // Ожидание свободного места
                            }
                            buffer.add(word);
                            itemCount++;
                            lock.notify(); // Уведомление потребителя
                        }
                    }
                }
                // Завершение работы
                synchronized (lock) {
                    while (itemCount == BUFFER_SIZE) {
                        lock.wait(); // Ожидание свободного места
                    }
                    buffer.add(null); // Отправка сигнала завершения
                    itemCount++;
                    lock.notify();
                }
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
                    String word;
                    synchronized (lock) {
                        while (itemCount == 0) {
                            lock.wait(); // Ожидание заполненного места
                        }
                        word = buffer.poll();
                        if (word == null) {
                            break; // Проверка на завершение
                        }
                        itemCount--;
                        lock.notify(); // Уведомление производителя
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