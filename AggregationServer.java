import java.io.*;
import java.net.*;

public class AggregationServer {
    public static void main(String[] args) {
        int defaultPort = 4567; // Default port number if no argument is provided
        int port;

        if (args.length > 0) {
            // Use the provided port number if an argument is provided
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port " + defaultPort);
                port = defaultPort;
            }
        } else {
            // Use the default port if no argument is provided
            port = defaultPort;
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("AggregationServer listening on port " + port);

            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();

                // Handle the connection in a separate thread
                Thread clientThread = new ContentServerHandler(clientSocket);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static class ContentServerHandler extends Thread {
        private final Socket clientSocket;

        public ContentServerHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                // Read and print the incoming PUT request
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.err.println("Error handling client request: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }
}
