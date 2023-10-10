import java.io.*; 
import java.net.*; 
import org.json.*; 
import java.util.*;

public class ContentServer {
    private ContentServer() {}

    public static void main(String[] args) {
        if (args.length != 2) { 
            System.err.println("Usage: java ContentServer servername:portnumber file_path"); 
            System.exit(1); 
        }
        try {
            // Parse the servername, port and file path from the command line
            String[] hostname_port = URIParser.parse(args[0]);
            String hostname = hostname_port[0];
            int port = Integer.parseInt(hostname_port[1]);
            String file_path = args[1];
            
            // Read the file
            ContentServer server = new ContentServer();
            JSONObject json = server.readFile(file_path);
            if (json == null) {
                System.out.println("Error: File not found");
                System.exit(1);
            }

            // Construct put request
            String request = server.constructPUTRequest(json);

            //  Establish connection with server
            Socket socket = new Socket(hostname, port);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send request to server
            writer.write(request);
            writer.flush();

            // Read response from server
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }

            // Close the connection
            socket.close();
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public JSONObject readFile(String file_path) {
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

    private String constructPUTRequest(JSONObject json) {
        // Construct the PUT request with JSON data
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("PUT /weather.json HTTP/1.1\r\n");
        requestBuilder.append("User-Agent: ATOMClient/1/0\r\n");
        requestBuilder.append("Content-Type: application/json\r\n");
    
        // Convert the JSON object to a properly formatted string
        String jsonStr = formatJsonObject(json);
    
        requestBuilder.append("Content-Length: " + jsonStr.length() + "\r\n");
        requestBuilder.append("\r\n"); // Blank line separating headers from the body
        requestBuilder.append(jsonStr);
        return requestBuilder.toString();
    }
    
    private String formatJsonObject(JSONObject json) {
        // Format the JSON object as a string
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