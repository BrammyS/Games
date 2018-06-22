/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javagame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 *
 * @author brame
 */
public class EnemyBullet extends Thread
{

    public int x, Xdestination, Ydestination, margin = 0, damage, bulletSize, speed;
    public double Ypath, y;
    public boolean dead = false;
    private Color color;
    private Sound spaceGun1 = new Sound("audio/space_gun1.wav");
    private Sound spaceGun2 = new Sound("audio/space_gun2.wav");
    private Random rand = new Random();

    public EnemyBullet(int X, int Y, int Ydestination, int Xdestination, int bulletSize, int damage, Color color, int speed)
    {
        this.speed = speed;
        this.x = X;
        this.y = Y;
        if (y > this.Ydestination)
        {
            margin = 15;
        }
        this.Xdestination = Xdestination;
        this.Ydestination = (int) (Ydestination + margin);
        this.color = color;
        this.damage = damage;
        this.bulletSize = bulletSize;
        this.Ypath = (this.Ydestination - this.y) / (this.Xdestination - this.x);
        System.out.println("Xdestination: " + this.Xdestination);
        System.out.println("Ydestination: " + this.Ydestination);
        System.out.println("Ypath: " + Ypath);
        if (rand.nextInt(2) + 0 == 1)
        {
            spaceGun1.play();
        } else
        {
            spaceGun2.play();
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (x > 2000)
            {
                this.stop();
            }
            x += speed;
            y += Ypath + (Ypath);
            try
            {
                Thread.sleep(7);
            } catch (InterruptedException e)
            {
            }
        }
    }

    public void draw(Graphics g)
    {
        if (!dead)
        {
            g.setColor(color);
            g.fillOval(x - 2, (int) Math.round(this.y), bulletSize, (int) Math.round(bulletSize * 0.6));
        }

    }

    public void dead()
    {
        this.dead = true;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return (int) Math.round(this.y);
    }
}
