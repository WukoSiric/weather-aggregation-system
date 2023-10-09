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
            System.out.println("Connecting to server " + hostname + " on port " + port + " with file path " + file_path);
            
            // Read the file
            ContentServer server = new ContentServer();
            JSONObject json = server.readFile(file_path);
            if (json == null) {
                System.out.println("Error: File not found");
                System.exit(1);
            }

            // Construct put request
            String request = server.constructPUTRequest(json);

            // Print request to console
            System.out.println(request);
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

    public String constructPUTRequest(JSONObject json) {
        // Construct the PUT request with JSON data
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("PUT /weather.json HTTP/1.1\r\n");
        requestBuilder.append("User-Agent: ATOMClient/1/0\r\n");
        requestBuilder.append("Content-Type: application/json\r\n");
        requestBuilder.append("Content-Length: " + json.toString().length() + "\r\n");
        requestBuilder.append("\r\n"); // Blank line separating headers from the body
        requestBuilder.append("{\r\n");
        // Append each key-value pair to the request
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            requestBuilder.append("    \"" + key + "\": \"" + json.get(key) + "\"");
            if (keys.hasNext()) {
                requestBuilder.append(",\r\n");
            }
        }
        requestBuilder.append("\r\n}");
        return requestBuilder.toString();
    }
}
