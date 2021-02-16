package aoop.asteroids.gui;

import aoop.asteroids.model.networking.Server;
import aoop.asteroids.model.game.Spaceship;
import java.awt.Image;
import java.util.Observable;
import javax.swing.JOptionPane;

/**
 * all the joiners' connection a host could be able to see
 * before the game start.
 * @author s3198928
 */
public class HostFrame extends javax.swing.JFrame {
    
    Server host;
    
    /**
     * Creates new form HostFrame
     * @param host
     */
    public HostFrame(Server host) {
        Image background= (new javax.swing.ImageIcon(getClass().getResource("/aoop/asteroids/gui/source.gif"))).getImage();
        this.setContentPane(new ImagePanel(background));
        this.host = host;
        this.host.addObserver ((Observable o, Object arg) -> {
            HostFrame.this.drawConnection();
        });
        initComponents();
        drawConnection();
        this.setTitle ("Asteroids");
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     *
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        title = new javax.swing.JLabel();
        connectionpane = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        startbutton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(655, 420));
        setResizable(false);
        getContentPane().setLayout(null);

        title.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setText("Waiting for start...");
        getContentPane().add(title);
        title.setBounds(220, 20, 250, 50);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        connectionpane.setViewportView(jTextArea1);

        getContentPane().add(connectionpane);
        connectionpane.setBounds(90, 100, 280, 230);

        startbutton.setText("Start");
        startbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startbuttonActionPerformed(evt);
            }
        });
        getContentPane().add(startbutton);
        startbutton.setBounds(440, 190, 140, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * the button the host would be able to click to start the game.
     * @param evt 
     */
    private void startbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startbuttonActionPerformed

        if(this.host.getConnections().size()>1){
            this.host.setRunning();
            this.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(null, "There has to be at least 2 players","Warning", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_startbuttonActionPerformed

    private void drawConnection(){
        String connect= "";
        for(Spaceship s : this.host.getConnections()){
            connect += s.getName()+"\n\n";
        }
        this.jTextArea1.setText(connect + "\n\n");
        this.jTextArea1.setEditable(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane connectionpane;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton startbutton;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
}
