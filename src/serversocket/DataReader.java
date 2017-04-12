package serversocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataReader implements Runnable {

    private final Connection connection;
    private final byte[] dataBuffer;

    public DataReader(Connection connection, int bufferSize) {
        this.connection = connection;
        this.dataBuffer = new byte[bufferSize];
    }

    @Override
    public void run() {
        int bytesRead;
        InputStream input = connection.getInput();
        
        while(connection.isConnected()) {
            try {
                bytesRead = input.read(dataBuffer);
                
                if (bytesRead > 0)
                {
                    String dataReceived = new String(dataBuffer, 0, bytesRead);
                    System.out.println("Data received: " + dataReceived);
                    System.out.println("Bytes received: " + bytesRead);
                    connection.processData(dataReceived);
                 }
                else
                {
                    connection.close();
                }
            } catch (SocketException ex) {
            	connection.close();
            } catch (IOException ex) {
                Logger.getLogger(DataReader.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
    
    
    
}
