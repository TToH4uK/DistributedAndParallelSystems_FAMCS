import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class WordCountInFiles {

    static class WordCountTask implements Callable<String> {
        private final String filePath;
        private final String word;

        public WordCountTask(String filePath, String word) {
            this.filePath = filePath;
            this.word = word;
        }

        @Override
        public String call() throws IOException {
            int count = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split("\\W+");
                    for (String w : words) {
                        if (w.equalsIgnoreCase(word)) {
                            count++;
                        }
                    }
                }
            }
            return filePath + ": " + count;
        }
    }

    public static void main(String[] args) {
        String[] filePaths = {
                "file1.txt",
                "file2.txt",
                "file3.txt"
        };
        String wordToFind = "example";

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<String>> futures = new CopyOnWriteArrayList<>();

        for (String filePath : filePaths) {
            futures.add(executor.submit(new WordCountTask(filePath, wordToFind)));
        }

        for (Future<String> future : futures) {
            try {
                System.out.println(future.get());
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error processing file: " + e.getMessage());
            }
        }

        executor.shutdown();
    }
}