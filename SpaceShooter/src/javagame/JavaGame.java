/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javagame;

import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Bram Esendam
 */
public class JavaGame extends JFrame
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        JavaGame frame = new JavaGame();
        frame.setSize(1280, 720);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Game");
        JPanel paneel = new Paneel();
        frame.addKeyListener((KeyListener) paneel);
        frame.setContentPane(paneel);
        frame.setVisible(true);
    }

}
