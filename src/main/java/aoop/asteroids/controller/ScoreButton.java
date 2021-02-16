package aoop.asteroids.controller;

import aoop.asteroids.gui.*;
import aoop.asteroids.database.ScoreDB;
import javax.swing.JButton;
import java.awt.event.*;

/**
 * Button that views our database. It uses the Action API to perform its action.
 * @author s3198928
 */
public class ScoreButton extends JButton {

    /**
     * constructor
     */
    public ScoreButton(GameMenu menu){
        super("View Scores");
        addActionListener(new ScoreAction(menu));
    }

}

/**
 * Represents an action made to view the score database.
 * @author s3198928
 */
class ScoreAction implements ActionListener {
    
    GameMenu menu;

    /**
     * constructor
     */
    public ScoreAction(GameMenu menu) {
        this.menu = menu;
    }

    /**
     * show the scorepanel
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.menu.setVisible(false);
        ScoreDB database = new ScoreDB();
        database.openDataSource();
        new ScorePanel(database);
        database.closeDataSource();
    }
}