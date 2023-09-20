import java.net.ServerSocket;

public class AggregationServer {
    private AggregationServer() {}
    
    private ServerSocket serverSocket; // Server socket to which the clients connect
    
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 4567;
        System.out.println("Starting server on port " + port);

    }
}
