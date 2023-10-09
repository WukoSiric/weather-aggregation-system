import java.io.*; 
import java.net.*; 
import org.json.*; 

public class ContentServer {
    private ContentServer() {}

    public static void main(String[] args) {
        if (args.length != 2) { 
            System.err.println("Usage: java ContentServer servername:portnumber file_path"); 
            System.exit(1); 
        }
        try {
            String[] hostname_port = URIParser.parse(args[0]);
            String hostname = hostname_port[0];
            int port = Integer.parseInt(hostname_port[1]);
            System.out.println("Connecting to server " + hostname + " on port " + port);
            // Socket s = new Socket("localhost", port);
            // DataOutputStream out = new DataOutputStream(s.getOutputStream());
            // out.writeUTF("Hello, server!");
            // out.flush();
            // out.close();
            // s.close();
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
