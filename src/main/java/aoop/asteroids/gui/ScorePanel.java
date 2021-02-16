/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aoop.asteroids.gui;

import aoop.asteroids.database.Score;
import aoop.asteroids.database.ScoreDB;
import java.awt.Image;
import java.util.Collection;
import java.util.Observable;

/**
 *
 * @author s3198928
 */
public class ScorePanel extends javax.swing.JFrame {
    
    private ScoreDB scores;

    /**
     * Creates new form ScoreFrame
     */
    public ScorePanel(ScoreDB db) {
        this.setTitle ("Asteroids");
        Image background= (new javax.swing.ImageIcon(getClass().getResource("/aoop/asteroids/gui/source.gif"))).getImage();
        this.setContentPane(new ImagePanel(background));
        this.scores = db;
        this.scores.addObserver ((Observable o, Object arg) -> {
            ScorePanel.this.drawScores();
        });
        initComponents();
        drawScores();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        title = new javax.swing.JLabel();
        scorepane = new javax.swing.JScrollPane();
        scoretext = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(527, 450));
        setResizable(false);
        getContentPane().setLayout(null);

        title.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setText("Score Panel");
        getContentPane().add(title);
        title.setBounds(189, 40, 175, 62);

        scoretext.setColumns(20);
        scoretext.setRows(5);
        scorepane.setViewportView(scoretext);

        getContentPane().add(scorepane);
        scorepane.setBounds(90, 110, 350, 200);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * paint the database
     */
    private void drawScores(){
        Collection<Score> result = scores.getAllScores();
        for(Score s : result){
            this.scoretext.append(s.toString()+"\n\n");
        }
        this.scoretext.setEditable(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scorepane;
    private javax.swing.JTextArea scoretext;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
}
