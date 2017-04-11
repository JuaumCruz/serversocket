package br.com.agiplan.communication;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author João Paulo Cruz
 */
public class ConnectionDataReader implements Runnable {

    private final SocketConnection connection;
    private final byte[] readBuffer;

    public ConnectionDataReader(SocketConnection connection) {
        this.connection = connection;
        this.readBuffer = new byte[connection.getServer().getReadBufferSize()];
    }

    @Override
    public void run() {
        int bytesRead;
        
        while(connection.getSocket().isConnected()) {
           
            try {
                bytesRead = connection.input.read(readBuffer);
                
                if (bytesRead > 0)
                {
                    String dataReceived = new String(readBuffer);
                    System.out.println("Data received: " + dataReceived);
                }
                else
                {
                    connection.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ConnectionDataReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
}
