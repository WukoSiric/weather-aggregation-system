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

        System.out.println("GETClient connecting to " + hostname + ":" + port);
        System.out.println("StationiD: " + stationID); 
    }
}