import java.io.*;
import java.net.*;
import java.util.*;

public class CentralServer {
    private final Map<String, String> data = new HashMap<>();

    public CentralServer(Map<String, String> initialData) {
        data.putAll(initialData);
    }

    public void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Central server started on port " + port);
        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> handleRequest(socket)).start();
        }
    }

    private void handleRequest(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String key = in.readLine();
            String value = data.getOrDefault(key, "NOT_FOUND");
            out.println(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // Пример запуска: java CentralServer key1 value1 key2 value2 key3 value3 6000
        Map<String, String> initialData = new HashMap<>();
        int port = Integer.parseInt(args[args.length - 1]);
        for (int i = 0; i < args.length - 1; i += 2) {
            initialData.put(args[i], args[i + 1]);
        }
        new CentralServer(initialData).start(port);
    }
}