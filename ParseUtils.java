import java.io.BufferedReader;
import java.io.FileReader;

import org.json.JSONObject;

public class ParseUtils {
    // Possible formats for the server name and port number include 
    // "http://servername.domain.domain:25565"
    // "http://servername:25565"
    // "servername:25565"

    // INPUTS: uri - a string representing the server name and port number
    // OUTPUTS: a string array of length 2, where the first element is the server name and the second element is the port number
    // DESCRIPTION: parses the server name and port number from the uri
    public static String[] parseURI(String uri) {
        String[] hostname_port = new String[2];
        if (uri.startsWith("http://")) {
            uri = uri.substring(7);
        }
        if (uri.contains("/")) {
            uri = uri.substring(0, uri.indexOf("/"));
        }
        if (uri.contains(":")) {
            hostname_port[0] = uri.substring(0, uri.indexOf(":"));
            hostname_port[1] = uri.substring(uri.indexOf(":") + 1);
        } else {
            hostname_port[0] = uri;
            hostname_port[1] = "80";
        }
        return hostname_port;
    }

    // INPUTS: file_path - a string representing the path to the file
    // OUTPUTS: a JSONObject representing the contents of the file
    // DESCRIPTION: reads the contents of the file and returns a JSONObject
    static JSONObject JSONObjectFromFile(String jsonFilePath) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(jsonFilePath));
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
        return null;
    }
}
