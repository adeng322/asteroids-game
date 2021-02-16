package aoop.asteroids.controller;


import aoop.asteroids.gui.GameMenu;
import aoop.asteroids.model.networking.Server;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.*;

/**
 *Button that hosts a multiplayer game. It uses the Action API to perform its action
 * which means that this is merely a default configuration for this button.
 * @author group1 
 */
public class HostButton extends JButton {
    
    
    /**
     * constructor
     */
    public HostButton(GameMenu menu){
        super("Host Multiplayer");
        addActionListener(new HostAction(menu));
    }

}

/**
 * Represents an action made to host a multiplayer game.
 * @author group1
 */
class HostAction implements ActionListener {

    GameMenu menu;
    /**
     * constructor
     */
    public HostAction(GameMenu menu) {
      this.menu = menu;   
    }

    /**
     * asks for the user information
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.menu.setVisible(false);
        String name = "";
        String input = "";
        do {
            name = JOptionPane.showInputDialog("Enter your nickname:");
            if (name == null) System.exit(0);
            input = JOptionPane.showInputDialog("Give the ip-address and port number of you", "Format = address:port");
            if (input==null) System.exit(0);
        } while (!input.contains(":"));
        String[] serverAddressPort = input.split(":");
        try {
            InetSocketAddress serverAddress = new InetSocketAddress(InetAddress.getByName(serverAddressPort[0]),Integer.parseInt(serverAddressPort[1]));
            Server host = new Server(serverAddress,name);
            Thread t = new Thread(host);
            t.start();
        } catch (UnknownHostException ex) {
            Logger.getLogger(HostButton.class.getName()).log(Level.SEVERE,null, ex);
            System.exit(0);
        }
    }
}