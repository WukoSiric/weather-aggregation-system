import java.io.*; 
import java.net.*; 

public class ContentServer {
    private ContentServer() {}

    public static void main(String[] args) {
        if (args.length != 1) { 
            System.err.println("Usage: java ContentServer <port number>"); 
            System.exit(1); 
        }
        int port = Integer.parseInt(args[0]);
        try {
            Socket s = new Socket("localhost", port);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF("Hello, server!");
            out.flush();
            out.close();
            s.close();
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
