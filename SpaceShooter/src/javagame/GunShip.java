/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javagame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author Bram Esendam
 */
public class GunShip
{

    public ArrayList<Bullet> bullets;
    public int x, y, hp = 100, speed = 3, damage = 10;
    public Image gunShipImg;

    public GunShip(int X, int Y) throws IOException
    {
        this.gunShipImg = ImageIO.read(new File("Textures/SpaceShip.png"));
        bullets = new ArrayList<Bullet>();
        x = X;
        y = Y;
    }

    public void draw(Graphics g)
    {
        g.drawImage(gunShipImg, x, y, null);
        g.setColor(Color.white);
        g.drawRect(20, 20, 200, 20);
        g.setColor(Color.green);
        if (hp * 2 > 200)
        {
            g.fillRect(22, 22, 200 - 3, 17);
        } else
        {
            g.fillRect(22, 22, hp * 2 - 3, 17);
        }
        g.setColor(Color.white);
        g.drawString("HP: " + hp, 30, 35);
        bullets.forEach((bullet) ->
        {
            if (this.x < -200)
            {
                bullet.dead();
                bullets.remove(bullet);
            } else
            {
                bullet.draw(g);
            }
        });
    }

    public void moveDown()
    {
        if (this.y < 1040)
        {
            this.y += speed;
        }
    }

    public void moveUp()
    {
        if (this.y > 10)
        {
            this.y -= speed;
        }
    }

    public void moveRight()
    {
        if (this.x < 1880)
        {
            this.x += speed;
        }
    }

    public void moveLeft()
    {
        if (this.x > 10)
        {
            this.x -= speed;
        }
    }

    public void powerUp()
    {
        this.speed = this.speed + 1;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void shoot()
    {
        Bullet bullet = new Bullet(this.x, this.y, Color.yellow, damage);
        bullet.start();
        bullets.add(bullet);
    }
}
