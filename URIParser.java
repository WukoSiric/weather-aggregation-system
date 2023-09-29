public class URIParser {
    // Possible formats for the server name and port number include 
    // "http://servername.domain.domain:25565"
    // "http://servername:25565"
    // "servername:25565"

    public static String[] parse(String uri) {
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
}
