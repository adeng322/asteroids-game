/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aoop.asteroids.model.networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *IO class for the server, sends and receives DatagramPacket
 * @author group1
 */
public class ServerNetworkIO {
    
  private DatagramSocket serverSocket;
	private int port;
	private DatagramPacket packet;
	private byte[] data;

	/**
	 * constructor
     * @param port
	 */
	public ServerNetworkIO(int port) {
		this.port = port;
		try {
			serverSocket = new DatagramSocket(port);
		} catch(Exception e) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
			System.exit(1);
		}
	}

	/**
	 * receive incoming packet
     * @return 
	 */
	public DatagramPacket getPacket() {
		data = new byte[65507];
		packet = new DatagramPacket(data, data.length);
		try {
			serverSocket.receive(packet);
		} catch(Exception e) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Error: error while recieving packet");
			return null;
		}
		return packet;
	}

	/**
	 * sends a packet through the server socket
	 * @param packet
	 */
	public void sendPacket(DatagramPacket packet) {
		try {
			serverSocket.send(packet);
		} catch(Exception e) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Error: error while sending packet");
		}
	}
 
}
