/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javagame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author Bram Esendam
 */
public class Asteroid extends Thread
{

    public int x, y, hp, rotation, rotationSpeed;
    public Image asteroidImg;

    Asteroid() throws IOException
    {
        Random rand = new Random();
        this.asteroidImg = ImageIO.read(new File("Textures/Asteroid_Medium.png"));
        this.rotation = rand.nextInt(360);
        this.rotationSpeed = rand.nextInt(5);
        this.hp = 100;
        this.x = -50;
        this.y = rand.nextInt(780) + 100;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getHp()
    {
        return hp;
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (x > 2000)
            {
                System.out.println("asteroid thread stopped.");
                this.stop();
            }
            this.rotation += rotationSpeed;
            this.x += 2;
            try
            {
                Thread.sleep(20);
            } catch (InterruptedException e)
            {
            }
        }
    }

    public void draw(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        rotate((BufferedImage) asteroidImg, g2d);
        if (hp != 100)
        {
            g.setColor(Color.black);
            g.drawRect(x + 5, y + 60, 50, 4);
            g.setColor(Color.green);
            g.fillRect(x + 6, y + 61, hp / 2, 3);
        }
    }

    public void rotate(BufferedImage inputImage, Graphics g)
    {
        double rotationRequired = Math.toRadians(this.rotation);
        double locationX = inputImage.getWidth() / 2;
        double locationY = inputImage.getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

        // Drawing the rotated image at the required drawing locations
        g.drawImage(op.filter(inputImage, null), this.x, this.y, null);
    }

    public int getRotation()
    {
        return rotation;
    }

    public int getRotationSpeed()
    {
        return rotationSpeed;
    }
}
