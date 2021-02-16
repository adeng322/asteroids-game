package aoop.asteroids.controller;


import aoop.asteroids.gui.GameMenu;
import aoop.asteroids.model.networking.Spectator;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.*;

/**
 * Button that sepectate a multiplayer game. It uses the Action API to perform its action.
 * @author s3198928
 */
public class SpectButton extends JButton {

    /**
     * constructor
     * @param menu
     */
    public SpectButton(GameMenu menu){
        super("Spectate");
        addActionListener(new SpectAction(menu));
    }

}

/**
 * Represents an action made to start to sepctate the game.
 * @author s3198928
 */
class SpectAction implements ActionListener {
    
    GameMenu menu;

    /**
     * constructor
     */
    public SpectAction(GameMenu menu) {
        this.menu = menu;
    }

    /**
     * ask for user information
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.menu.setVisible(false);
        try {
            String input = "";
            do {
                input = JOptionPane.showInputDialog("Connect to the ip-address and port number of yourhost", "Format = address:port");
                if (input==null) System.exit(0);
            } while (!input.contains(":"));
            String[] serverAddressPort = input.split(":");
            InetSocketAddress newSpectator = new InetSocketAddress(InetAddress.getByName(serverAddressPort[0]),Integer.parseInt(serverAddressPort[1]));
            Spectator spectator = new Spectator(newSpectator);
            spectator.start();
        } catch (UnknownHostException ex) {
            Logger.getLogger(SpectAction.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }
}