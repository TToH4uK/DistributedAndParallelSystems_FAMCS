import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        // Пример запуска: java Client 127.0.0.1 5001 key1
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String key = args[2];

        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(key);
            String response = in.readLine();
            System.out.println("Response: " + response);
        }
    }
}