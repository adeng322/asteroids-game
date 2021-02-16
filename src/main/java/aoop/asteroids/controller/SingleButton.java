package aoop.asteroids.controller;


import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.gui.GameMenu;
import aoop.asteroids.model.game.Game;
import aoop.asteroids.model.game.Spaceship;
import javax.swing.JButton;
import java.awt.event.*;

/**
 * Button that plays single mode game. It uses the Action API to perform its action.
 * @author s3198928
 */
public class SingleButton extends JButton {

    /**
     * constructor
     * @param menu
     */
    public SingleButton(GameMenu menu){
        super("Single Player");
        addActionListener(new SingleAction(menu));
    }

}

/**
 * Represents an action made to start a single mode game.
 * @author s3198928
 */
class SingleAction implements ActionListener {
    
    GameMenu menu;

    /**
     * constructor
     */
    public SingleAction(GameMenu menu) {
        this.menu = menu;
    }

    /**
     * to start the game
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.menu.setVisible(false);
        String name = "single";
        HostController player = new HostController ();
        Game game = new Game ();
        game.setSingleMode();
        game.addShip(new Spaceship(name));
        game.linkController (player,0);
        AsteroidsFrame frame = new AsteroidsFrame (game, player);
        Thread t = new Thread (game);
        t.start ();
    }
}