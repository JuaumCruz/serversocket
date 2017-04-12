package serversocket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartServer {
	private final static String ipAddress = "127.0.0.1";
	private final static int portNumber = 5000;
	private final static int maxConnections = 10;
	private final static int bufferSize = 4096;
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Server server = new Server(InetAddress.getByName(ipAddress), portNumber, maxConnections, bufferSize);
            server.start();
        } catch (UnknownHostException ex) {
            Logger.getLogger(StartServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
