package aoop.asteroids.model.networking;

import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.model.game.Game;
import java.io.EOFException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import java.net.SocketAddress;

/**
 *Thread for users to spectate multiplayer mode game
 * @author group1
 */
public class Spectator extends Thread {

    private Game game;
    private ClientNetworkIO IO;

    /**
     * constructor
     * @param address
     */
    public Spectator(SocketAddress address) {
        this.game = new Game();
        IO = new ClientNetworkIO(address);
    }

    /**
     * thread run method
     */
    @Override
    public void run() {
        try {
            IO.sendMessage("hello");
            AsteroidsFrame frame = new AsteroidsFrame(game, this);
            while (true) {
                try {
                    Game receivedModel = new Game().deserialize(IO.getMessage());
                    game.update(receivedModel);
                    SwingUtilities.updateComponentTreeUI(frame);
                } catch (EOFException e) {
                    this.game.update();
                    Logger.getLogger(Spectator.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Spectator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * disconnect from the spectated server and game
     */
    public void disconnect() {
        this.IO.sendMessage("Byebye");
        this.IO.disconnect();
        this.stop();
    }
}
      

