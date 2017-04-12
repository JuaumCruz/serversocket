package serversocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionListener implements Runnable {

    private final ServerSocket serverSocket;
    private final Server server;

    public ConnectionListener(ServerSocket serverSocket, Server server) {
        this.serverSocket = serverSocket;
        this.server = server;     
    }

    @Override
    public void run() {
        while(server.isActive()) {
            try {
                Connection connection = new Connection(serverSocket.accept(), server);
                if (server.getNumberOfConnections() > server.getMaxConnections()) {
                    connection.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ConnectionListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
