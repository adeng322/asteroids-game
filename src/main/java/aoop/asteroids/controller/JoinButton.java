package aoop.asteroids.controller;


import aoop.asteroids.gui.GameMenu;
import aoop.asteroids.model.networking.Joiner;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.*;

/**
 * Button that joins a multiplayer game. It uses the Action API to perform its action.
 * @author s3198928
 */
public class JoinButton extends JButton {

    /**
     * constructor
     */
    public JoinButton(GameMenu menu){
        super("Join Multiplayer");
        addActionListener(new JoinAction(menu));
    }

}

/**
 * Represents an action made to join a multiplayer game.
 * @author s3198928
 */
class JoinAction implements ActionListener {
    
    GameMenu menu;
   
    /**
     * constructor
     */
    public JoinAction(GameMenu menu) {
        this.menu = menu;
    }

    /**
     * asks for the user information
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.menu.setVisible(false); 
        try {
           String name = JOptionPane.showInputDialog("Enter your nickname:");
            if (name == null || " ".equals(name)) System.exit(0);
            
            String input = "";
            do {
                input = JOptionPane.showInputDialog("Connect to the ip-address and port number of yourhost", "Format = address:port");
                if (input==null) System.exit(0);
            } while (!input.contains(":"));
            String[] serverAddressPort = input.split(":");
            InetSocketAddress serverAddress = new InetSocketAddress(InetAddress.getByName(serverAddressPort[0]),Integer.parseInt(serverAddressPort[1]));
            Joiner joiner = new Joiner(serverAddress,name);
            joiner.start();
        } catch (UnknownHostException ex) {
            Logger.getLogger(JoinButton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}