package serversocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection {

    private final Socket socket;
    private final String ipAddress;
    private final Server server;
    private InputStream input;
    private OutputStream output;
    private Thread dataReader;

    public Connection(Socket socket, Server server) {
        this.socket = socket;
        this.ipAddress = (socket.getInetAddress() != null) ? socket.getInetAddress().toString() + ":" + String.valueOf(socket.getPort()) : "";
        this.server = server;

        try {
            socket.setReceiveBufferSize(server.getBufferSize());
            socket.setSendBufferSize(server.getBufferSize());
        } catch (SocketException ex) {
            close();
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }

        server.AddConnection(this);
        startReceivingData();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public InputStream getInput() {
        return input;
    }

    public OutputStream getOutput() {
        return output;
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public void close() {
        try {
            input.close();
            output.close();
            socket.close();
            
            if (dataReader != null) {
                dataReader.interrupt();
            }

            server.RemoveConnection(ipAddress);
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startReceivingData() {
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException ex) {
            close();
            return;
        }

        dataReader = new Thread(new DataReader(this, server.getBufferSize()));
        dataReader.start();
    }

    public synchronized void processData(String dataReceived) {
        String dataResponse = "Response: " + dataReceived;
        sendDataResponse(dataResponse);
    }

    public void sendDataResponse(String dataResponse) {
        try {
            output.write(dataResponse.getBytes(), 0, dataResponse.length());
            output.flush();
        } catch (IOException ex) {
            close();
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
