/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javagame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Bram Esendam
 */
public class Paneel extends JPanel implements KeyListener
{

    private GunShip gunShip;
    private BossGunShip bossShip;
    private int asteroidSpawnTime, repaintTime = 22, bossStage = 1;
    private Levelinfo levelinfo = new Levelinfo();
    private boolean shot = false, wIsadded = false, dIsadded = false, bossFight = false,
            sIsadded = false, aIsadded = false, spaceIsadded = false;

    //ArrayLists
    private ArrayList<String> keys;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<EnemyGunShip> enemyGunShips;
    public ArrayList<PowerUp> powerUps;

    //Sounds && music
    private final Sound levelMusic = new Sound("audio/music/level1music.wav");
    private final Sound explosion = new Sound("audio/explosion.wav");
    private final Sound gameOverMusic = new Sound("audio/music/gameOver.wav");
    private final Sound hurtSound = new Sound("audio/hurt_sound.wav");
    private final Sound hitRock = new Sound("audio/hit_rock.wav");
    private final Sound boostSound = new Sound("audio/boost.wav");
    private final Sound bossStage1Music = new Sound("audio/music/bossStage1.wav");
    private final Sound bossStage2Music = new Sound("audio/music/bossStage2.wav");
    private final Sound bossStage3Music = new Sound("audio/music/bossStage3.wav");
    private final Sound bossFinalStageMusic = new Sound("audio/music/bossFinalStage.wav");
    private final Sound bossDefeated = new Sound("audio/music/bossDefeated.wav");
    private final Sound endlessModeMusic = new Sound("audio/music/endlessMode.wav");
    private final Sound damage = new Sound("audio/damage.wav");

    //Timers
    private Timer paintTimer, bulletLimiter, enemyGunShipTimer, powerUpTimer;

    //images
    private final Image backGroundImg = ImageIO.read(new File("Textures/Background.png"));
    private final Image youLoseImg = ImageIO.read(new File("Textures/GameOver.png"));

    public Paneel() throws IOException
    {
        levelinfo.timeStarted = new Date();
        levelMusic.playBackgroundMusic();
        gunShip = new GunShip(1150, 360);
        powerUps = new ArrayList<>();
        bossShip = new BossGunShip();
        asteroids = new ArrayList<>();
        keys = new ArrayList<>();
        enemyGunShips = new ArrayList<>();
        asteroidSpawnTime = 3500;

        //Timers && Threads
        paintTimer = new Timer(repaintTime, new paintTimerHandler());
        paintTimer.start();
        enemyGunShipTimer = new Timer(9333, new enemyGunShipTimerHandler());
        enemyGunShipTimer.start();
        bulletLimiter = new Timer(211, new bulletLimitHandler());
        bulletLimiter.start();
        powerUpTimer = new Timer(60000, new powerUpHandler());
        powerUpTimer.start();
        new moveHandler().start();
        new levelHandler().start();
        new asteroidHandler().start();
        new hitRegistration().start();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(backGroundImg, 0, 0, null);
        levelinfo.draw(g);
        try
        {
            if (gunShip.hp <= 0)
            {
                levelinfo.drawDeadMenu(g, youLoseImg);
                if (!levelinfo.gameOver)
                {
                    removeKeys();
                    levelinfo.loadHighScores();
                    levelMusic.stopMusic();
                    bossStage1Music.stopMusic();
                    bossStage2Music.stopMusic();
                    bossStage3Music.stopMusic();
                    bossFinalStageMusic.stopMusic();
                    bossDefeated.stopMusic();
                    endlessModeMusic.stopMusic();
                    gameOverMusic.playBackgroundMusic();
                    levelinfo.gameOver = true;
                    levelinfo.level = 1;
                    bossShip.stopShootTimers();
                    levelinfo.totalTries++;
                    enemyGunShips.forEach((EnemyGunShip enemyGunShip) ->
                    {
                        enemyGunShip.stopShootTimers();
                    });
                    int temp = levelinfo.asteroidDeathCount;
                    levelinfo.asteroidDeathCount = 0;
                    try
                    {
                        String name = JOptionPane.showInputDialog("Please Enter your name:");
                        levelinfo.timeStoped = new Date();
                        levelinfo.saveHighScore(name, temp);
                        levelinfo.loadHighScores();
                    } catch (Exception e)
                    {
                        System.out.println(e);
                    }
                }
            } else
            {
                gunShip.draw(g);
                powerUps.forEach((powerUp) ->
                {
                    powerUp.draw(g);
                });
            }
            enemyGunShips.forEach((EnemyGunShip enemyGunShip) ->
            {
                if (enemyGunShip.hp > 0 && enemyGunShip.isAlive())
                {
                    enemyGunShip.draw(g);
                    enemyGunShip.Ydestination = gunShip.getY();
                    enemyGunShip.Xdestination = gunShip.getX();
                } else
                {
                    enemyGunShip.stopShootTimers();
                    enemyGunShip.stop();
                    enemyGunShips.remove(enemyGunShip);
                }

            });
            asteroids.forEach((Asteroid asteroid) ->
            {
                if (asteroid.isAlive())
                {
                    asteroid.draw(g);
                }
            });
            if (bossFight)
            {
                if (!bossShip.isAlive() && !levelinfo.isBossDead)
                {
                    bossShip.start();
                }
                bossShip.Ydestination = gunShip.getY();
                bossShip.Xdestination = gunShip.getX();
                bossShip.draw(g);
            }
        } catch (ConcurrentModificationException e)
        {
        }
    }

    @Override
    public void keyTyped(KeyEvent e
    )
    {
    }

    @Override
    public void keyPressed(KeyEvent e
    )
    {
        if (e.getKeyCode() == KeyEvent.VK_D && !dIsadded)
        {
            keys.add("d");
            dIsadded = true;

        } else if (e.getKeyCode() == KeyEvent.VK_A && !aIsadded)
        {
            keys.add("a");
            aIsadded = true;

        } else if (e.getKeyCode() == KeyEvent.VK_W && !wIsadded)
        {
            keys.add("w");
            wIsadded = true;
        } else if (e.getKeyCode() == KeyEvent.VK_S && !sIsadded)
        {
            keys.add("s");
            sIsadded = true;

        } else if (e.getKeyCode() == KeyEvent.VK_SPACE && !spaceIsadded && gunShip.hp > 0)
        {
            keys.add("space");
            spaceIsadded = true;
        } else if (e.getKeyCode() == KeyEvent.VK_T)
        {
            hurtSound.play();
            gunShip.hp -= 20;
        } else if (e.getKeyCode() == KeyEvent.VK_F1)
        {
            restartGame();
        } else if (e.getKeyCode() == KeyEvent.VK_F5)
        {
            levelinfo.asteroidDeathCount = 75;
            bossStage = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_F6)
        {
            levelinfo.asteroidDeathCount = 75;
            bossShip.hp = (bossShip.totalHp / 4) * 3;
            bossStage = 2;
        } else if (e.getKeyCode() == KeyEvent.VK_F7)
        {
            levelinfo.asteroidDeathCount = 75;
            bossShip.hp = (bossShip.totalHp / 4) * 2;
            bossStage = 3;
        } else if (e.getKeyCode() == KeyEvent.VK_F8)
        {
            levelinfo.asteroidDeathCount = 75;
            bossShip.hp = (bossShip.totalHp / 4) * 1;
            bossStage = 4;
        } else if (e.getKeyCode() == KeyEvent.VK_F9)
        {
            levelinfo.asteroidDeathCount++;
        }
    }

    @Override
    public void keyReleased(KeyEvent e
    )
    {
        try
        {
            keys.forEach((key) ->
            {
                System.out.println(key);
                String keyChar = "" + e.getKeyChar();
                System.out.println(e.getKeyCode());
                if (key.equals(keyChar) && key.equals("w"))
                {
                    System.out.println("Remove W");
                    keys.remove("w");
                    wIsadded = false;
                } else if (key.equals(keyChar) && key.equals("s"))
                {
                    System.out.println("Remove S");
                    keys.remove("s");
                    sIsadded = false;
                } else if (key.equals(keyChar) && key.equals("d"))
                {
                    System.out.println("Remove D");
                    keys.remove("d");
                    dIsadded = false;
                } else if (key.equals(keyChar) && key.equals("a"))
                {
                    System.out.println("Remove A");
                    keys.remove("a");
                    aIsadded = false;
                } else if (e.getKeyCode() == 32 && key.equals("space"))
                {
                    System.out.println("Remove space");
                    keys.remove("space");
                    spaceIsadded = false;
                }
            });
        } catch (ConcurrentModificationException ex)
        {
        }
    }

    private class hitRegistration extends Thread
    {

        @Override
        public void run()
        {
            while (true)
            {
                registrateHits();
                trySleep(40);
            }
        }

        public void registrateHits()
        {
            try
            {
                //looping trough all the Astroids on the screen
                if (!bossFight)
                {
                    asteroids.forEach((Asteroid asteroid) ->
                    {
                        //checking if the gunship is getting hit by a asteroid
                        if (gunShip.hp > 0 && gunShip.getX() < asteroid.getX() + 5 && gunShip.getX() > asteroid.getX()
                                && gunShip.getY() < asteroid.getY() + 50 && gunShip.getY() > asteroid.getY())
                        {
                            gunShip.hp -= 10;
                            hurtSound.play();
                        }
                        //checking if the bullets are hitting a asteroid
                        gunShip.bullets.forEach((bullet) ->
                        {
                            if (!bullet.dead && bullet.isAlive())
                            {
                                if (bullet.getX() < asteroid.getX() + 56 && bullet.getX() > asteroid.getX()
                                        && bullet.getY() < asteroid.getY() + 56 && bullet.getY() > asteroid.getY())
                                {
                                    bullet.dead();//setting dead to true
                                    bullet.stop();//stoping bullet thread
                                    asteroid.hp -= gunShip.damage;
                                    hitRock.play();
                                }
                                enemyGunShips.forEach((EnemyGunShip enemyGunShip) ->
                                {
                                    if (bullet.getX() < enemyGunShip.getX() + 30 && bullet.getX() > enemyGunShip.getX()
                                            && bullet.getY() < enemyGunShip.getY() + enemyGunShip.height && bullet.getY() > enemyGunShip.getY() - 20)
                                    {
                                        bullet.dead();//setting dead to true
                                        bullet.stop();//stoping bullet thread
                                        enemyGunShip.hp -= gunShip.damage;
                                    }
                                });
                            } else
                            {
                                gunShip.bullets.remove(bullet);
                            }
                        });
                        //removing a asteroid if hp is below 0
                        if (asteroid.hp < 0)
                        {
                            asteroid.stop();
                        }
                        if (!asteroid.isAlive())
                        {
                            if (asteroid.hp <= 0)
                            {
                                explosion.play();
                                levelinfo.asteroidDeathCount++;
                            }
                            asteroids.remove(asteroid);
                        }
                    });

                    //checking if the gunship is getting hit by a power up
                    powerUps.forEach((powerUp) ->
                    {
                        if (gunShip.hp > 0 && gunShip.getX() < powerUp.getX() + 25 && gunShip.getX() > powerUp.getX()
                                && gunShip.getY() < powerUp.getY() + 25 && gunShip.getY() > powerUp.getY()
                                || gunShip.hp > 0 && gunShip.getX() < powerUp.getX() && gunShip.getX() + 25 > powerUp.getX()
                                && gunShip.getY() < powerUp.getY() && gunShip.getY() + 25 > powerUp.getY())
                        {
                            gunShip.speed += powerUp.speedBoost();
                            gunShip.hp += powerUp.healthPack();
                            gunShip.damage += powerUp.damagePack();
                            System.out.println("Speed: " + powerUp.speedBoost());
                            System.out.println("hp: " + powerUp.healthPack());
                            System.out.println("damage: " + powerUp.damagePack());
                            powerUps.remove(powerUp);
                            boostSound.play();
                            if (gunShip.hp > 100)
                            {
                                gunShip.hp = 100;
                            }
                        }
                    });

                    //looping trough all the enemyGunShips on the screen
                    enemyGunShips.forEach((EnemyGunShip enemyGunShip) ->
                    {
                        //checking if the ennemy's bullets are hitting your gunship
                        enemyGunShip.bullets.forEach((EnemyBullet bullet) ->
                        {
                            if (!bullet.dead && bullet.isAlive())
                            {
                                if (bullet.getX() < gunShip.getX() + 28 && bullet.getX() > gunShip.getX()
                                        && bullet.getY() < gunShip.getY() + 35 && bullet.getY() > gunShip.getY())
                                {
                                    damage.play();
                                    bullet.dead();//setting dead to true
                                    bullet.stop();//stoping bullet thread
                                    gunShip.hp -= bullet.damage;
                                    if (gunShip.hp > 0)
                                    {
                                        hurtSound.play();
                                    }
                                }
                            } else
                            {
                                gunShip.bullets.remove(bullet);
                            }
                        });
                    });
                } else
                {
                    //checking if the bullets are hitting a enemy's
                    gunShip.bullets.forEach((bullet) ->
                    {
                        if (!bullet.dead && bullet.isAlive())
                        {
                            enemyGunShips.forEach((EnemyGunShip enemyGunShip) ->
                            {
                                if (bullet.getX() < enemyGunShip.getX() + 15 && bullet.getX() > enemyGunShip.getX()
                                        && bullet.getY() < enemyGunShip.getY() + 50 && bullet.getY() > enemyGunShip.getY() - 15)
                                {
                                    bullet.dead();//setting dead to true
                                    bullet.stop();//stoping bullet thread
                                    enemyGunShip.hp -= gunShip.damage;
                                }
                            });
                            if (bullet.getX() < bossShip.getX() + 150 && bullet.getX() > bossShip.getX()
                                    && bullet.getY() < bossShip.getY() + 367 && bullet.getY() > bossShip.getY() - 15)
                            {
                                bullet.dead();//setting dead to true
                                bullet.stop();//stoping bullet thread
                                bossShip.hp -= gunShip.damage;
                            } else if (bullet.getX() < bossShip.getX() + 340 && bullet.getX() > bossShip.getX()
                                    && bullet.getY() < bossShip.getY() + 244 && bullet.getY() > bossShip.getY() + 120)
                            {
                                bullet.dead();//setting dead to true
                                bullet.stop();//stoping bullet thread
                                bossShip.hp -= gunShip.damage;
                            }
                        } else
                        {
                            gunShip.bullets.remove(bullet);
                        }
                    });

                    //checking if the ennemy's bullets are hitting your gunship
                    bossShip.bullets.forEach((EnemyBullet bullet) ->
                    {
                        if (!bullet.dead && bullet.isAlive())
                        {
                            if (bullet.getX() < gunShip.getX() + 28 && bullet.getX() > gunShip.getX()
                                    && bullet.getY() < gunShip.getY() + 35 && bullet.getY() > gunShip.getY())
                            {
                                bullet.dead();//setting dead to true
                                bullet.stop();//stoping bullet thread
                                gunShip.hp -= bullet.damage;
                                if (gunShip.hp > 0)
                                {
                                    hurtSound.play();
                                }
                            }
                        } else
                        {
                            gunShip.bullets.remove(bullet);
                        }
                    });
                    //checking if the gunship is getting hit by a power up
                    powerUps.forEach((powerUp) ->
                    {
                        if (gunShip.hp > 0 && gunShip.getX() < powerUp.getX() + 40 && gunShip.getX() > powerUp.getX()
                                && gunShip.getY() < powerUp.getY() + 40 && gunShip.getY() > powerUp.getY()
                                || gunShip.hp > 0 && gunShip.getX() < powerUp.getX() && gunShip.getX() + 40 > powerUp.getX()
                                && gunShip.getY() < powerUp.getY() && gunShip.getY() + 40 > powerUp.getY())
                        {
                            gunShip.speed += powerUp.speedBoost();
                            gunShip.hp += powerUp.healthPack();
                            gunShip.damage += powerUp.damagePack();
                            System.out.println("Speed: " + powerUp.speedBoost());
                            System.out.println("hp: " + powerUp.healthPack());
                            System.out.println("damage: " + powerUp.damagePack());
                            powerUps.remove(powerUp);
                            boostSound.play();
                            if (gunShip.hp > 100)
                            {
                                gunShip.hp = 100;
                            }
                        }
                    });
                }
            } catch (ConcurrentModificationException ex)
            {
            }
        }
    }

    public void restartGame()
    {
        System.out.println("Game has been reset!");
        asteroids.forEach((asteroid) ->
        {
            asteroid.stop();
        });
        enemyGunShips.forEach((enemyGunShip) ->
        {
            enemyGunShip.bullets.forEach((bullet) ->
            {
                bullet.stop();
            });
            enemyGunShip.bullets.clear();
            enemyGunShip.stopShootTimers();
            enemyGunShip.stop();
        });
        powerUps.clear();
        levelinfo.timeStarted = new Date();
        enemyGunShips.clear();
        levelinfo.level = 1;
        asteroids.clear();
        gunShip.hp = 100;
        gunShip.damage = 10;
        gunShip.x = 1150;
        gunShip.y = 360;
        gunShip.speed = 3;
        try
        {
            bossFight = false;
            bossShip.hp = bossShip.totalHp;
            bossStage = 1;
            bossShip = new BossGunShip();
        } catch (IOException ex)
        {
            Logger.getLogger(Paneel.class.getName()).log(Level.SEVERE, null, ex);
        }
        gameOverMusic.stopMusic();
        levelMusic.playBackgroundMusic();
        levelinfo.gameOver = false;
    }

    public void trySleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        } catch (InterruptedException e)
        {
            System.out.println(e);
        }
    }

    private class levelHandler extends Thread
    {

        @Override
        public void run()
        {
            while (true)
            {
                if (levelinfo.asteroidDeathCount < 25)
                {
                    asteroidSpawnTime = 2500;
                    levelinfo.level = 1;
                } else if (levelinfo.asteroidDeathCount >= 25 && levelinfo.asteroidDeathCount < 50)
                {
                    asteroidSpawnTime = 2000;
                    levelinfo.level = 2;
                } else if (levelinfo.asteroidDeathCount >= 50 && levelinfo.asteroidDeathCount < 75)
                {
                    asteroidSpawnTime = 1000;
                    levelinfo.level = 3;
                } else if (levelinfo.asteroidDeathCount >= 75 && !levelinfo.isBossDead)
                {
                    levelinfo.level = 4;
                    bossFight = true;
                    levelMusic.stopMusic();
                    if (!bossStage1Music.isPlaying() && bossShip.hp > (bossShip.totalHp / 4) * 3)
                    {
                        System.out.println("stage 1");
                        bossStage1Music.playBackgroundMusic();
                        bossShip.stage1();
                    } else if (!bossStage2Music.isPlaying() && bossShip.hp <= (bossShip.totalHp / 4) * 3 && bossShip.hp > (bossShip.totalHp / 4) * 2)
                    {
                        bossStage1Music.stopMusic();
                        System.out.println("stage 2");
                        bossStage2Music.playBackgroundMusic();
                        bossShip.stage2();
                    } else if (!bossStage3Music.isPlaying() && bossShip.hp <= (bossShip.totalHp / 4) * 2 && bossShip.hp > (bossShip.totalHp / 4) * 1)
                    {
                        bossStage2Music.stopMusic();
                        System.out.println("stage 3");
                        bossStage3Music.playBackgroundMusic();
                        bossShip.stage3();
                    } else if (!bossFinalStageMusic.isPlaying() && bossShip.hp <= (bossShip.totalHp / 4) * 1 && bossShip.hp > 0)
                    {
                        bossStage3Music.stopMusic();
                        System.out.println("stage 4");
                        bossFinalStageMusic.playBackgroundMusic();
                        bossShip.stage4();
                    } else if (bossShip.hp <= 0)
                    {
                        levelinfo.isBossDead = true;
                        bossFinalStageMusic.stopMusic();
                        bossDefeated.play();
                        bossShip.stopShootTimers();
                        bossShip.stop();
                    }
                } else if (levelinfo.isBossDead)
                {
                    bossFight = false;
                    asteroidSpawnTime = 800;
                    if (!endlessModeMusic.isPlaying())
                    {
                        trySleep(5300);
                        endlessModeMusic.playBackgroundMusic();
                    }
                    levelinfo.level = 5;
                }
                trySleep(150);
            }
        }
    }

    private class moveHandler extends Thread
    {

        @Override
        public void run()
        {
            while (true)
            {
                levelinfo.sort();
                move();
                trySleep(repaintTime);
            }
        }

        public void move()
        {
            try
            {
                keys.forEach((key) ->
                {
                    if (key.equals("w"))
                    {
                        gunShip.moveUp();
                    } else if (key.equals("s"))
                    {
                        gunShip.moveDown();

                    } else if (key.equals("d"))
                    {
                        gunShip.moveRight();

                    } else if (key.equals("a"))
                    {
                        gunShip.moveLeft();

                    } else if (key.equals("space"))
                    {
                        if (!shot)
                        {
                            gunShip.shoot();
                            shot = true;
                        }
                    }
                });
            } catch (ConcurrentModificationException ex)
            {

            }
        }
    }

    class asteroidHandler extends Thread
    {

        @Override
        public void run()
        {
            trySleep(4000);
            while (true)
            {
                if (!bossFight)
                {
                    addAsteroid();
                }
                trySleep(asteroidSpawnTime);
            }
        }

        public void addAsteroid()
        {
            Asteroid asteroid = null;
            try
            {
                asteroid = new Asteroid();
            } catch (IOException ex)
            {
            }
            asteroid.start();
            asteroids.add(asteroid);
            System.out.println("asteroid added");
        }
    }

    class paintTimerHandler implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            repaint();
        }
    }

    class bulletLimitHandler implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (shot)
            {
                shot = false;
            }
        }
    }

    class enemyGunShipTimerHandler implements ActionListener
    {

        private Random rand = new Random();

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (!bossFight && !levelinfo.gameOver)
            {
                try
                {
                    EnemyGunShip enemyGunShip = new EnemyGunShip(rand.nextInt(780) + 100, levelinfo.level);
                    enemyGunShip.start();
                    enemyGunShips.add(enemyGunShip);
                    System.out.println("Number of enemy's: " + enemyGunShips.size());
                } catch (IOException ex)
                {
                }
            } else
            {
                enemyGunShips.forEach((enemy) ->
                {
                    enemy.stopShootTimers();
                    enemy.stop();
                });
                enemyGunShips.clear();
            }
        }
    }

    private class powerUpHandler implements ActionListener
    {

        private Random rand = new Random();

        public powerUpHandler()
        {
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                if (powerUps.size() < 3)
                {
                    powerUps.add(new PowerUp(rand.nextInt(1100) + 1, rand.nextInt(1020) + 1, levelinfo.isBossDead));
                }
            } catch (IOException ex)
            {
                Logger.getLogger(Paneel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void removeKeys()
    {
        keys.forEach((key) ->
        {
            if (key.equals("w"))
            {
                System.out.println("Remove W");
                keys.remove("w");
                wIsadded = false;
            } else if (key.equals("s"))
            {
                System.out.println("Remove S");
                keys.remove("s");
                sIsadded = false;
            } else if (key.equals("d"))
            {
                System.out.println("Remove D");
                keys.remove("d");
                dIsadded = false;
            } else if (key.equals("a"))
            {
                System.out.println("Remove A");
                keys.remove("a");
                aIsadded = false;
            } else if (key.equals("space"))
            {
                System.out.println("Remove space");
                keys.remove("space");
                spaceIsadded = false;
            }
        });
    }
}
