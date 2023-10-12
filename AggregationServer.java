import java.io.*;
import java.net.*;
import org.json.*;
import java.util.HashMap; 
import java.util.Map;
import java.util.Timer; 
import java.util.TimerTask;

public class AggregationServer {
    private static final int defaultPort = 4567;
    private static final Map<String, Long> stationTimestamps = new HashMap<>();
    private static final long EXPIRATION_TIME = 30 * 1000; // 30 seconds in milliseconds
    public static void main(String[] args) {
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

        // Start the station expiration timer
        startStationExpirationTimer();

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

    private static void startStationExpirationTimer() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Remove expired stations
                long currentTime = System.currentTimeMillis();
                stationTimestamps.entrySet().removeIf(entry -> (currentTime - entry.getValue()) > EXPIRATION_TIME);
            }
        }, 0, 3000);
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

                String requestString = request.toString();

                if (isValidPutRequest(requestString)) {
                    handlePutRequest(requestString, reader, writer);
                } else if (isValidGetRequest(requestString)) {
                    handleGetRequest(requestString, reader, writer);
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

        private void handlePutRequest(String request, BufferedReader reader, PrintWriter writer) throws IOException {
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
            Boolean fileCreated = file.createNewFile();
            if (fileCreated) {
                System.out.println("File created: " + file.getName());
                FileWriter fileWriter = new FileWriter("weather.json");
                fileWriter.write(jsonFormatted.toString());
                fileWriter.close();
                writer.write("HTTP/1.1 201 Created\r\n");
            } else {
                System.out.println("File already exists.");
                JSONObject weatherData = readFile("weather.json");
                weatherData.put(stationID.toString(), json);
                FileWriter fileWriter = new FileWriter("weather.json");
                fileWriter.write(weatherData.toString());
                fileWriter.close();
                writer.write("HTTP/1.1 200 OK\r\n");
            }
            writer.write("\r\n");
            writer.flush();
        }

        private static boolean isValidGetRequest(String request) {
            // Check if the request is a valid GET request with required headers
            String[] requestLines = request.split("\n");
            String firstLine = requestLines[0];
            String[] firstLineParts = firstLine.split(" ");
            return 
                    firstLineParts.length == 3 &&
                    firstLineParts[0].equals("GET") &&
                    (firstLineParts[1].equals("/weather.json") || firstLineParts[1].equals("/")) &&
                    firstLineParts[2].equals("HTTP/1.1");
        }

        private void handleGetRequest(String request, BufferedReader reader, PrintWriter writer) throws IOException {
            File file = new File("weather.json");
            if (!file.exists()) {
                writer.write("HTTP/1.1 404 Not Found\r\n");
                writer.write("\r\n");
                writer.flush();
                return;
            }
            
            // Print get request
            System.out.println("Received valid GET request:\n" + request);
            writer.write("HTTP/1.1 200 OK\r\n");
            writer.write("Content-Type: application/json\r\n");
            writer.write("\r\n");

            // Read weather.json file
            JSONObject weatherData = readFile("weather.json");
            writer.write(weatherData.toString());
            writer.write("\r\n");
            writer.flush();
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

    public static JSONObject readFile(String file_path) {
        // Read the file and return a JSONObject
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file_path));
            String line = reader.readLine();
            String json_string = "";
            while (line != null) {
                json_string += line;
                line = reader.readLine();
            }
            reader.close();
            return new JSONObject(json_string);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        // Return null if there is an error
        return null;
    }
}
