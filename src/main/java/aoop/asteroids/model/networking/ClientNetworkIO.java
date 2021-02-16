
package aoop.asteroids.model.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *IO for clients, can receive and send packets and messages
 * @author group1
 */
public class ClientNetworkIO {
    private DatagramSocket clientSocket;
    private DatagramPacket packet;
    private SocketAddress ServerAddress;
    private byte[] Data;


    /**
     * constructor
     * @param address
     */
    public ClientNetworkIO(SocketAddress address) {
        this.ServerAddress = address;
        try {
            Random rand = new Random();
            clientSocket = new DatagramSocket(rand.nextInt(5000)+3000);
        } catch(SocketException e) {
            Logger.getLogger(ClientNetworkIO.class.getName()).log(Level.SEVERE,"Error: Socket could not be created");
            System.exit(1);
        }
    }


    /**
     * receive incoming message as byte[]
     * @return 
     */
    public byte[] getMessage()
    {
        Data = new byte[65507];
        packet = new DatagramPacket(Data, Data.length);
        try {
            clientSocket.receive(packet);
        } catch(IOException e) {
            Logger.getLogger(ClientNetworkIO.class.getName()).log(Level.SEVERE,"Error: error while receiving message" );
        }
        return packet.getData();
    }

    /**
     * sends datagram packet through client socket
     * @param packet
     */
    public void sendPacket(DatagramPacket packet) {
        try {
            clientSocket.send(packet);
        } catch(Exception e) {
            Logger.getLogger(ClientNetworkIO.class.getName()).log(Level.SEVERE,"Error: error while sending packet" );
        }
    }
    /**
     * sends string as datagram packet
     * @param line
     */
    public void sendMessage(String line) {
        Data = line.getBytes();
        packet = new DatagramPacket(Data, Data.length, ServerAddress);
        try {
            clientSocket.send(packet);
        } catch(IOException e) {
             Logger.getLogger(ClientNetworkIO.class.getName()).log(Level.SEVERE,"Error: error while sending message" );
        }
    }


    /**
     * receive incoming datagram packet
     * @return 
     */
    public DatagramPacket getPacket() {
        Data = new byte[65507];
        packet = new DatagramPacket(Data, Data.length);
        try {
          clientSocket.receive(packet);
        } catch(Exception e) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Error: error while recieving packet");
          return null;
        }
        return packet;
    }

    /**
     * close socket
     */
    public void disconnect(){
        clientSocket.close();
    }
    
}


