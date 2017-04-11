package br.com.agiplan.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author João Paulo Cruz
 */
public class SocketConnection {

    private final Socket socket;
    private final String ipAddress;
    private final Server server;
    public InputStream input;
    public OutputStream output;
    private Thread ConnectionDataReader;

    public SocketConnection(Socket socket, Server server) {
        this.socket = socket;
        this.ipAddress = (socket.getInetAddress() != null) ? socket.getInetAddress().toString() + ":" + String.valueOf(socket.getPort()) : "";
        this.server = server;
            
        try {
//            if (socket.isConnected()) {
//                socket.setSoTimeout(1000);
//            }
            
            socket.setReceiveBufferSize(server.getReadBufferSize());
            socket.setSendBufferSize(server.getReadBufferSize());
        } catch (SocketException ex) {
            close();
            Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        server.AddConnection(this);
        startReceivingData();
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public Server getServer() {
        return server;
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public void close() {
        try {
            input.close();
            output.close();
            socket.close();
            
            server.RemoveConnection(ipAddress);
        } catch (IOException ex) {
            Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void startReceivingData()
    {
        try
        {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        }
        catch (IOException ex)
        {
            close();
            return;
        }

        ConnectionDataReader = new Thread(new ConnectionDataReader(this));
        ConnectionDataReader.start();
    }
}
