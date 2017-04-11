package br.com.agiplan.communication;

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

/**
 *
 * @author João Paulo Cruz
 */
public class Server {

    private final ThreadPoolExecutor threadPool;
    private final int port;
    private ServerSocket serverSocket;
    private Thread thread;
    private final InetAddress ipAddress;
    private boolean active;
    private final ConcurrentHashMap<String, SocketConnection> connections;
    private final int maxConnections;
    private final int readBufferSize;
    
    /**
     * Constructs a new Server.
     *
     * @param ipAddress is the listener's IP address.
     * @param port is the listener's tcp port.
     * @param corePoolSize is the number of threads to keep in the pool, even if they are idle.
     * @param maxPoolSize is the maximum number of threads to allow in the pool.
     * @param maxConnections is the listener's maximum allowed connections.
     * @param readBufferSize is the read buffer size for each connections in bytes.
     */
    public Server(InetAddress ipAddress, int port, int corePoolSize, int maxPoolSize, int maxConnections, int readBufferSize) {
        this.threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000, true));
        this.ipAddress = ipAddress;
        this.port = port;
        this.connections = new ConcurrentHashMap<>();
        this.maxConnections = maxConnections;
        this.readBufferSize = readBufferSize;
    }
    
    public ConcurrentHashMap<String, SocketConnection> getConnections() {
        return connections;
    }
    
    public int getMaxConnections() {
        return maxConnections;
    }
    
    public int getReadBufferSize() {
        return readBufferSize;
    }
    
    public void start() {
        stop();
        
        try {
            serverSocket = new ServerSocket(port, 100, ipAddress);
//            serverSocket.setSoTimeout(1000);
            
            active = true;
        } catch (IOException ex) {
            stop();
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        thread = new Thread(new ConnectionListener(serverSocket, this));
        thread.start();
    }
    
    public void stop() {
        try {
            active = false;

            thread.interrupt();
            
            for (Enumeration e = connections.keys(); e.hasMoreElements();) {
                SocketConnection connection = (SocketConnection) connections.get(e.nextElement());
                connection.close();
            }
            
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isActive() {
        return active;
    }
    
    public synchronized void AddConnection(SocketConnection connection) {
        if (!connections.contains(connection.getIpAddress()))
        {
            connections.put(connection.getIpAddress(), connection);
        }
    }
    
    public synchronized void RemoveConnection(String ipAddress) {
        if (connections.containsKey(ipAddress))
        {
            connections.remove(ipAddress);
        }
    }
}
