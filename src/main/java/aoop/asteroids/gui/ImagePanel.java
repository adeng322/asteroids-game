/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aoop.asteroids.gui;

 import java.awt.Image;
 import java.awt.Graphics;
 import javax.swing.JComponent;
 /**
  * Main menu for the user
  * @author s3083691
  */
 public class ImagePanel extends JComponent {
     private Image image;
 
     public ImagePanel(Image image){
         this.image=image;
     }
 
     @Override
     public void paintComponent(Graphics g){
         super.paintComponent(g);
         g.drawImage(image,0,0,this);
     }
 } 