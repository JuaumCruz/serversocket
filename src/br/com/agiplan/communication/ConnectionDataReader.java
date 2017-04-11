package br.com.agiplan.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionDataReader implements Runnable {

    private final SocketConnection connection;
    private final byte[] dataBuffer;

    public ConnectionDataReader(SocketConnection connection, int bufferSize) {
        this.connection = connection;
        this.dataBuffer = new byte[bufferSize];
    }

    @Override
    public void run() {
        int bytesRead;
        InputStream input = connection.getInput();
        OutputStream output = connection.getOutput();
        
        while(connection.isConnected()) {
           
            try {
                bytesRead = input.read(dataBuffer);
                
                if (bytesRead > 0)
                {
                    String dataReceived = new String(dataBuffer, 0, bytesRead);
                    System.out.println("Data received: " + dataReceived);
                    System.out.println("Bytes received: " + bytesRead);
                    
                    String dataResponse = connection.sendDataResponse(dataReceived);
                    
                    output.write(dataResponse.getBytes(), 0, dataResponse.length());
                }
                else
                {
                    connection.close();
                }
            } catch (SocketException ex) {
            	connection.close();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionDataReader.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
    
    
    
}
