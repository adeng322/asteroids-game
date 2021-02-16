package aoop.asteroids.gui;

import aoop.asteroids.controller.SpectButton;
import aoop.asteroids.controller.*;
import java.awt.Image;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Main menu for the user
 * @author s3198928
 */
public class GameMenu extends JFrame {
  
    /**
     * constructor
     */
    public GameMenu() {
        this.setTitle ("Asteroids");
        Image background= (new javax.swing.ImageIcon(getClass().getResource("/aoop/asteroids/gui/source.gif"))).getImage();
        this.setContentPane(new ImagePanel(background));
        initComponents();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    }

    /**
     * initialize all the buttons.
     */                 
    private void initComponents() {

        JButton singleMode = new SingleButton(this);
        JButton hostMode = new HostButton(this);
        JButton specMode = new SpectButton(this);
        JButton joinMode = new JoinButton(this);
        JButton ViewScore = new ScoreButton(this);
        JLabel jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(441, 462));
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().add(singleMode);
        singleMode.setBounds(110, 73, 199, 25);
        getContentPane().add(hostMode);
        hostMode.setBounds(110, 132, 199, 25);
        getContentPane().add(specMode);
        specMode.setBounds(110, 191, 199, 25);
        getContentPane().add(joinMode);
        joinMode.setBounds(110, 250, 199, 25);
        getContentPane().add(ViewScore);
        ViewScore.setBounds(110, 309, 199, 25);
        
        pack();
    }
}
