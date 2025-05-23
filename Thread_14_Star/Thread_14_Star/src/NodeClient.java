import java.io.*;
import java.net.*;

public class NodeClient {
    private final String centralHost;
    private final int centralPort;

    public NodeClient(String centralHost, int centralPort) {
        this.centralHost = centralHost;
        this.centralPort = centralPort;
    }

    public void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Node started on port " + port + ", connected to central server at " + centralHost + ":" + centralPort);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String key = in.readLine();
            String value = queryCentralServer(key);
            out.println(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String queryCentralServer(String key) {
        try (
                Socket socket = new Socket(centralHost, centralPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(key);
            return in.readLine();
        } catch (IOException e) {
            return "ERROR";
        }
    }

    public static void main(String[] args) throws IOException {
        // Пример запуска: java NodeClient 127.0.0.1 6000 5001
        String centralHost = args[0];
        int centralPort = Integer.parseInt(args[1]);
        int port = Integer.parseInt(args[2]);
        new NodeClient(centralHost, centralPort).start(port);
    }
}