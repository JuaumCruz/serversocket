package serversocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private final int port;
    private ServerSocket serverSocket;
    private Thread connectionListener;
    private final InetAddress ipAddress;
    private boolean active;
    private final ConcurrentHashMap<String, Connection> connections;
    private final int maxConnections;
    private final int bufferSize;
    
    /**
     * Constructs a new Server.
     *
     * @param ipAddress is the ServerSocket IP address.
     * @param port is ServerSocket port.
     * @param maxConnections is the Server maximum allowed connections.
     * @param bufferSize is the buffer size for each connections in bytes.
     */
    public Server(InetAddress ipAddress, int port, int maxConnections, int bufferSize) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.connections = new ConcurrentHashMap<>();
        this.maxConnections = maxConnections;
        this.bufferSize = bufferSize;
    }
    
    public int getNumberOfConnections() {
        return connections.size();
    }
    
    public int getMaxConnections() {
        return maxConnections;
    }
    
    public int getBufferSize() {
        return bufferSize;
    }
    
    public void start() {
        stop();
        
        try {
            serverSocket = new ServerSocket(port, 100, ipAddress);
            
            active = true;
        } catch (IOException ex) {
            stop();
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        connectionListener = new Thread(new ConnectionListener(serverSocket, this));
        connectionListener.start();
    }
    
    public void stop() {
        try {
            active = false;

            if (connectionListener != null) {
            	connectionListener.interrupt();
            }
            
            for (Enumeration<String> e = connections.keys(); e.hasMoreElements();) {
                Connection connection = (Connection) connections.get(e.nextElement());
                connection.close();
            }
            
            if (serverSocket != null) {
            	serverSocket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isActive() {
        return active;
    }
    
    public synchronized void AddConnection(Connection connection) {
        if (!connections.contains(connection.getIpAddress()))
        {
            connections.put(connection.getIpAddress(), connection);
            System.out.println(connection.getIpAddress() + " connected!");
        }
    }
    
    public synchronized void RemoveConnection(String ipAddress) {
        if (connections.containsKey(ipAddress))
        {
            connections.remove(ipAddress);
            System.out.println(ipAddress + " disconnected!");
        }
    }
}
