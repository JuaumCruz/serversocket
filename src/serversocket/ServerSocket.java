package serversocket;

import br.com.agiplan.communication.Server;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author João Paulo Cruz
 */
public class ServerSocket {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Server server = new Server(InetAddress.getByName("127.0.0.1"), 5000, 5, 10, 5, 8192);
            server.start();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
