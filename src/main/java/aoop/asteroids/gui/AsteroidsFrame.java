package aoop.asteroids.gui;

import aoop.asteroids.controller.HostController;
import aoop.asteroids.model.game.Game;
import aoop.asteroids.model.networking.Joiner;
import aoop.asteroids.model.networking.Spectator;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 *	AsteroidsFrame is a class that extends JFrame and thus provides a game 
 *	window for the Asteroids game.
 *
 *	@author Yannick Stoffers
 */
public class AsteroidsFrame extends JFrame
{

	/** serialVersionUID */
    public static final long serialVersionUID = 1L;

    /** Quit action. */
    private AbstractAction quitAction;

    /** New game action. */
    private AbstractAction newGameAction;

    private AbstractAction escape;

    /** The game model. */
    private Game game;

    /** The panel in which the game is painted. */
    private AsteroidsPanel ap;

    /** 
     *	Constructs a new Frame, requires a game model.
     *
     *	@param game game model.
     */
    public AsteroidsFrame (Game game, Object ob)
    {
        this.game = game;
        this.setTitle ("Asteroids");
        this.setSize (800, 800);
        this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        JMenuBar mb = new JMenuBar ();
        
        if(ob instanceof HostController){
            this.initActions ();
            JMenu m = new JMenu ("Game");
            mb.add (m);
            m.add (this.quitAction);
            m.add (this.newGameAction);
            this.addKeyListener ((HostController)ob);
        }else{
            this.escapeAction(ob);
            JMenu m = new JMenu ("Escape");
            mb.add (m);
            m.add (this.escape);
        }
        
        this.setJMenuBar (mb);
        this.ap = new AsteroidsPanel (this.game);
        this.setLocationRelativeTo(null);
        this.add (this.ap);
        this.setVisible (true);
    }
 
      /** Quits the old game and starts a new one. */
    private void newGame ()
    {
        this.game.abort ();
        try
        {
            Thread.sleep(50);
        }catch (InterruptedException e)
        {   
            Logger.getLogger(AsteroidsFrame.class.getName()).log(Level.SEVERE, "Could not sleep before initialing a new game.");
        }
        this.game.initGameData ();
    }

      /** Initializes the quit- and new game action. */
    private void initActions() 
    {
        // Quits the application
        this.quitAction = new AbstractAction ("Quit") 
        {
                public static final long serialVersionUID = 2L;

                @Override
                public void actionPerformed (ActionEvent arg0) 
                {   
                        System.exit(0);
                }
        };

        // Creates a new model
        this.newGameAction = new AbstractAction ("New Game") 
        {
                public static final long serialVersionUID = 3L;

                @Override
                public void actionPerformed(ActionEvent arg0) 
                {
                        AsteroidsFrame.this.newGame ();
                }
        };
    }

    private void escapeAction(Object object){
        
        this.escape = new AbstractAction ("Escape") 
        {
            public static final long serialVersionUID = 6L;

            @Override
            public void actionPerformed (ActionEvent arg0) 
            {   
                if(object instanceof Joiner){
                    Joiner j = (Joiner)object;
                    j.disconnect();
                }else if(object instanceof Spectator){
                    Spectator s = (Spectator)object; 
                    s.disconnect();
                }
                AsteroidsFrame.this.setVisible(false);
            }
        };
    }
}
