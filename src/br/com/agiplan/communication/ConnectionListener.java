package br.com.agiplan.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author João Paulo Cruz
 */
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
                SocketConnection client = new SocketConnection(serverSocket.accept(), server);
                if (server.getConnections().size() > server.getMaxConnections()) {
                    client.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ConnectionListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
