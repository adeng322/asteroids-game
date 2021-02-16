package aoop.asteroids.controller;

import aoop.asteroids.model.networking.Joiner;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * JoinerController is the controller class that listens to the clients input (key events) 
 *	and forwards those events to the server class. Where they will influence 
 *	the state of the game model from the server's side.
 * @author s3198928
 */
public class JoinerController implements KeyListener
{

    /** The message that is being influenced. */
    private boolean[] commands;

    /**
     * This method allows one to add a joiner to an otherwise useless object. 
     * @param player this is the client that this class will influence from 
	   * now on.
     */
    public void addJoiner (Joiner player)
    {
      this.commands = player.getCommands();
    }

    /**
     *	This method is invoked when a key is pressed and sets the
	   *  corresponding boolean in the command to true.
     *	@param e keyevent that triggered the method.
     */
     @Override
    public void keyPressed (KeyEvent e)
    {
        switch (e.getKeyCode ())
        {
            case KeyEvent.VK_UP:
                this.commands[0]= true;
                break;
            case KeyEvent.VK_LEFT:
                this.commands[1]= true;
                break;
            case KeyEvent.VK_RIGHT:
                this.commands[2] = true;
                break;
            case KeyEvent.VK_SPACE:
                this.commands[3] = true;
                break;
        }
    }

    /**
     * This method doesn't do anything.
     * @param e
     */
    @Override
    public void keyTyped (KeyEvent e) {}

    /**
     * This method is invoked when a key is released and sets the
	   * corresponding boolean in the command to false.
     * @param e
     */
    @Override
    public void keyReleased (KeyEvent e)
    {
        switch (e.getKeyCode ())
        {
            case KeyEvent.VK_UP:
                this.commands[0]= false;
                break;
            case KeyEvent.VK_LEFT:
                this.commands[1]= false;
                break;
            case KeyEvent.VK_RIGHT:
                this.commands[2]= false;;
                break;
            case KeyEvent.VK_SPACE:
                this.commands[3] = false;
                break;
        }
    }

}
