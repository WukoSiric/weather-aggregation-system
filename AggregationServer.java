import java.io.*;
import java.net.*;
import org.json.*;

public class AggregationServer {
    public static void main(String[] args) {
        final int defaultPort = 4567; // Default port number if no argument is provided
        int port;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port " + defaultPort);
                port = defaultPort;
            }
        } else {
            port = defaultPort;
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("AggregationServer listening on port " + port);
            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
                // Handle the connection in a separate thread
                Thread clientThread = new ClientHandler(clientSocket);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                // Parse entire request including headers
                StringBuilder request = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    request.append(line).append("\n");
                }

                // Check if the request is a valid PUT request with required headers
                if (isValidPutRequest(request.toString())) {
                    // Extract content length
                    int contentLength = extractContentLength(request.toString());

                    // Read the request body based on content length
                    StringBuilder requestBody = new StringBuilder();
                    char[] buffer = new char[1024];
                    int bytesRead;
                    while (contentLength > 0 && (bytesRead = reader.read(buffer, 0, Math.min(contentLength, buffer.length))) != -1) {
                        requestBody.append(buffer, 0, bytesRead);
                        contentLength -= bytesRead;
                    }

                    // Now you can process the request body in 'requestBody' variable
                    System.out.println("Received valid PUT request:\n" + request);
                    System.out.println("Request Body:\n" + requestBody.toString());

                    JSONObject json = new JSONObject(requestBody.toString());
                    Object stationID = json.get("id"); 

                    // Format JSON so ID is key and rest of data is value
                    JSONObject jsonFormatted = new JSONObject();
                    jsonFormatted.put(stationID.toString(), json);
                    
                    // Create / Update weather.json file 
                    File file = new File("weather.json");
                    if (file.createNewFile()) {
                        System.out.println("File created: " + file.getName());
                    } else {
                        System.out.println("File already exists.");
                    } 

                    // Write to weather.json file
                    FileWriter fileWriter = new FileWriter("weather.json");
                    fileWriter.write(jsonFormatted.toString());
                    fileWriter.close();
                    
                    // Respond with a 200 OK
                    writer.write("HTTP/1.1 200 OK\r\n");
                    writer.write("\r\n");
                    writer.flush();
                } else if (isValidGetRequest(request.toString())) {
                    writer.write("HTTP/1.1 200 OK\r\n");
                    writer.write("\r\n");
                    writer.flush();
                }
                else {
                    // Respond with a 400 Bad Request
                    writer.write("HTTP/1.1 400 Bad Request\r\n");
                    writer.write("\r\n");
                    writer.flush();
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

        private static boolean isValidPutRequest(String request) {
            // Check if the request is a valid PUT request with required headers
            return 
                    request.startsWith("PUT") &&
                    request.contains("Content-Type: application/json") &&
                    request.contains("Content-Length: ");
        }

        private static boolean isValidGetRequest(String request) {
            // Check if the request is a valid GET request with required headers
            return request.startsWith("GET");
        }

        private static int extractContentLength(String request) {
            // Extract and parse the "Content-Length" header value
            int startIndex = request.indexOf("Content-Length: ");
            if (startIndex != -1) {
                int endIndex = request.indexOf("\n", startIndex);
                String lengthStr = request.substring(startIndex + "Content-Length: ".length(), endIndex).trim();
                try {
                    return Integer.parseInt(lengthStr);
                } catch (NumberFormatException e) {
                    // Handle parsing error if necessary
                }
            }
            return -1; // Return -1 if header is not found or parsing fails
        }
    }
}
