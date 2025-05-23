import java.io.*;

public class ReverseWordsInFileSequential {
    public static void main(String[] args) {
        String inputFileName = "input.txt";
        String outputFileName = "output.txt";

        // Измерение времени начала
        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFileName))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Разделяем строку на слова
                String[] words = line.split("\\s+");
                for (String word : words) {
                    // Переворачиваем каждое слово и записываем в выходной файл
                    String reversedWord = new StringBuilder(word).reverse().toString();
                    writer.println(reversedWord);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Измерение времени окончания
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Время выполнения программы: " + duration + " миллисекунд");
    }
}