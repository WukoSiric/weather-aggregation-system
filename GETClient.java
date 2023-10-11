import java.io.*;
import java.net.*;
import org.json.*; 

public class GETClient {
    public static void main(String[] args) {
        // Check arguments 
        if (args.length < 1) {
            System.err.println("Usage: java GETClient servername:portnumber stationID(optional)");
            System.exit(1);
        }

        String[] hostname_port = URIParser.parse(args[0]);
        String hostname = hostname_port[0];
        int port = Integer.parseInt(hostname_port[1]);
        String stationID = null;

        if (args.length == 2) {
            stationID = args[1];
        }

        try {
            // Establish connection with the server
            Socket socket = new Socket(hostname, port);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Construct the GET request
            String request = constructGETRequest(stationID);

            // Send the GET request to the server using write
            writer.write(request);
            writer.flush(); 

            // Read and print the response from the server
            StringBuilder response = new StringBuilder();
            String line; 
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            // Close the connection
            socket.close();

            // Split response body by empty line 
            String[] responseParts = response.toString().split("\n\n");
            JSONObject receivedData = new JSONObject(responseParts[1]); 

            // Print the response body without JSON formatting
            if (stationID == null) {
                for (String key : receivedData.keySet()) {
                    System.out.println("Station " + key);
                    for (String key2 : receivedData.getJSONObject(key).keySet()) {
                        System.out.println("    " + key2 + ": " + receivedData.getJSONObject(key).get(key2));
                    }
                }

                return;
            }

            // Print specific station data
            System.out.println("Station " + stationID);
            if (receivedData.keySet().contains(stationID)) {
                for (String key : receivedData.getJSONObject(stationID).keySet()) {
                    System.out.println("    " + key + ": " + receivedData.getJSONObject(stationID).get(key));
                }
            } 
            else {
                System.out.println("    Station not found");
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String constructGETRequest(String stationID) {
        // Construct the GET request with optional stationID
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("GET /weather.json HTTP/1.1\r\n");
        requestBuilder.append("User-Agent: GETClient/1.0\r\n");

        if (stationID != null) {
            // Include the stationID in the request
            requestBuilder.append("Station-ID: " + stationID + "\r\n");
        }

        requestBuilder.append("\r\n");
        return requestBuilder.toString();
    }
}
