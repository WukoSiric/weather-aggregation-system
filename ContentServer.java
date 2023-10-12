import java.io.*; 
import java.net.*; 
import org.json.*; 
import java.util.*;

public class ContentServer {
    public ContentServer() {}

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java ContentServer servername:portnumber file_path");
            System.exit(1);
        }
    
        try {
            // Parse the hostname and port number
            String[] hostname_port = ParseUtils.parseURI(args[0]);
            String hostname = hostname_port[0];
            int port = Integer.parseInt(hostname_port[1]);
            String file_path = args[1];
    
            // Read the file
            ContentServer server = new ContentServer();
            JSONObject json = ParseUtils.JSONObjectFromFile(file_path);
            if (json == null) {
                System.out.println("Error: File not found");
                System.exit(1);
            }
    
            String request = server.constructPUTRequest(json);
    
            // Attempt to connect to the server
            int maxRetryAttempts = 3;
            int retryCount = 0;
            boolean success = false;
            while (retryCount < maxRetryAttempts) {
                try {
                    Socket socket = new Socket(hostname, port);
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
                    // Send request to server
                    writer.write(request);
                    writer.flush();
    
                    // Read response from server
                    String line = reader.readLine();
                    StringBuilder responseBuilder = new StringBuilder();
                    while (line != null) {
                        System.out.println(line);
                        responseBuilder.append(line);
                        line = reader.readLine();
                    }
                    socket.close();

                    if (checkIfSuccessful(responseBuilder.toString())) {
                        success = true;
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("Error connecting to the server: " + e.getMessage());
                }
    
                retryCount++;
                Thread.sleep(1000);
            }
    
            if (!success) {
                System.out.println("Failed to connect after " + maxRetryAttempts + " attempts.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
    
    // INPUT: HTTP response
    // OUTPUT: true if the response is successful, false otherwise
    // DESCRIPTION: Checks if the response is successful
    static boolean checkIfSuccessful(String response) {
        if (response.startsWith("HTTP/1.1 2")) {
            return true;
        }
        return false; 
    }

    // INPUT: JSON object
    // OUTPUT: properly formatted PUT request
    // DESCRIPTION: Constructs a PUT request with the JSON object as the body
    String constructPUTRequest(JSONObject json) {
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("PUT /weather.json HTTP/1.1\r\n");
        requestBuilder.append("User-Agent: ATOMClient/1/0\r\n");
        requestBuilder.append("Content-Type: application/json\r\n");
    
        String jsonStr = formatJsonObject(json);
    
        requestBuilder.append("Content-Length: " + jsonStr.length() + "\r\n");
        requestBuilder.append("\r\n");
        requestBuilder.append(jsonStr);
        return requestBuilder.toString();
    }
    
    // INPUT: JSON object
    // OUTPUT: properly formatted JSON string
    // DESCRIPTION: Formats a JSON object as a string for sending over the network
    String formatJsonObject(JSONObject json) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\r\n");
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            jsonBuilder.append("    \"" + key + "\": ");
            Object value = json.get(key);
            if (value instanceof String) {
                jsonBuilder.append("\"" + value + "\"");
            } else {
                jsonBuilder.append(value.toString());
            }
            if (keys.hasNext()) {
                jsonBuilder.append(",\r\n");
            } else {
                jsonBuilder.append("\r\n");
            }
        }
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}