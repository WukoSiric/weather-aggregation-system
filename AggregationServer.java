import java.io.*; 
import java.net.*; 

public class AggregationServer {
    private AggregationServer() {}
    
    private ServerSocket serverSocket; // Server socket to which the clients connect
    
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 4567;
        System.out.println("Starting server on port " + port);
        
        try {
            ServerSocket ss = new ServerSocket(port); 
            Socket s = ss.accept();
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String str = (String)dis.readUTF();
            System.out.println("Message: " + str);
            ss.close(); 
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
