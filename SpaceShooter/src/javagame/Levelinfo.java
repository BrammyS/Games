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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Bram Esendam
 */
public class Levelinfo
{

    //images
    private Image level1Text, level2Text, level3Text, bossFightText, endlessModeText;
    private ArrayList<Image> numbers;
    public int level = 1, x = 710, y = 10, scoreListY = 400, totalTries = 0, asteroidDeathCount = 000;
    public boolean isBossDead = false, gameOver = false;
    public Date timeStarted, timeStoped;
    public long totalElapsedTime;
    public ArrayList<scoreData> scores;
    public int index = 0;

    public Levelinfo()
    {
        try
        {
            scores = new ArrayList<scoreData>();
            numbers = new ArrayList<Image>();
            this.level1Text = ImageIO.read(new File("Textures/level-1.png"));
            this.level2Text = ImageIO.read(new File("Textures/level-2.png"));
            this.level3Text = ImageIO.read(new File("Textures/level-3.png"));
            this.bossFightText = ImageIO.read(new File("Textures/Boss-Fight.png"));
            this.endlessModeText = ImageIO.read(new File("Textures/Endless-mode.png"));
            for (int i = 0; i <= 9; i++)
            {
                numbers.add(ImageIO.read(new File("Textures/" + i + ".png")));
                System.out.println(i);
            }
        } catch (IOException ex)
        {
            Logger.getLogger(Levelinfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void draw(Graphics g)
    {
        drawLevelInfo(g);
        drawScore(g);
    }

    public void drawScore(Graphics g)
    {
        boolean secondDigitIsSet = false, thirdDigitIsSet = false, fourthDigitIsSet = false;
        int firstDigit = Integer.parseInt(Integer.toString(asteroidDeathCount).substring(0, 1));
        int secondDigit = 0;
        int thirdDigit = 0;
        int fourthDigit = 0;

        try
        {
            secondDigit = Integer.parseInt(Integer.toString(asteroidDeathCount).substring(1, 2));
            secondDigitIsSet = true;
        } catch (StringIndexOutOfBoundsException e)
        {

        }
        try
        {
            thirdDigit = Integer.parseInt(Integer.toString(asteroidDeathCount).substring(2, 3));
            thirdDigitIsSet = true;
        } catch (StringIndexOutOfBoundsException e)
        {

        }
        try
        {
            fourthDigit = Integer.parseInt(Integer.toString(asteroidDeathCount).substring(3, 4));
            fourthDigitIsSet = true;
        } catch (StringIndexOutOfBoundsException e)
        {
        }
        if (secondDigitIsSet)
        {
            if (fourthDigitIsSet)
            {
                g.drawImage(numbers.get(fourthDigit), 1850, 13, null);
                g.drawImage(numbers.get(thirdDigit), 1800, 13, null);
                g.drawImage(numbers.get(secondDigit), 1750, 13, null);
                g.drawImage(numbers.get(firstDigit), 1700, 13, null);
            } else if (thirdDigitIsSet)
            {
                g.drawImage(numbers.get(thirdDigit), 1850, 13, null);
                g.drawImage(numbers.get(secondDigit), 1800, 13, null);
                g.drawImage(numbers.get(firstDigit), 1750, 13, null);
            } else
            {
                g.drawImage(numbers.get(secondDigit), 1850, 13, null);
                g.drawImage(numbers.get(firstDigit), 1800, 13, null);
            }
        } else
        {
            g.drawImage(numbers.get(firstDigit), 1850, 13, null);
        }
    }

    public void drawLevelInfo(Graphics g)
    {
        if (level == 1)
        {
            g.drawImage(level1Text, x, y, null);

        } else if (level == 2)
        {
            g.drawImage(level2Text, x, y, null);

        } else if (level == 3)
        {
            g.drawImage(level3Text, x, y, null);

        } else if (level == 4)
        {
            g.drawImage(bossFightText, x - 150, y, null);

        } else if (level == 5)
        {
            g.drawImage(endlessModeText, x - 150, y, null);
        }
    }

    public void saveHighScore(String name, int tempScore)
    {
        this.totalElapsedTime = (this.timeStoped.getTime() - this.timeStarted.getTime()) / 1000;
        String fileName = "";
        for (int i = 0; i < 10000; i++)
        {
            File checkFile = new File("HighScores/score" + i + ".json");
            if (!checkFile.exists())
            {
                fileName = "HighScores/score" + i + ".json";
                i = 10001;
                //System.out.println(fileName + " doesn't exists");
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("Name", name);
        obj.put("Score", tempScore);
        obj.put("elapsedTime", this.totalElapsedTime);
        try (FileWriter file = new FileWriter(fileName))
        {
            file.write(obj.toJSONString());
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void sort()
    {
        int n = scores.size();
        scoreData temp;
        for (int i = 0; i < n; i++)
        {
            for (int a = 1; a < (n - i); a++)
            {
                if (scores.get(a - 1).heighScore < scores.get(a).heighScore)
                {
                    temp = scores.get(a - 1);
                    scores.remove(scores.get(a - 1));
                    scores.add(a, temp);
                }
            }
        }
    }

    public void loadHighScores()
    {
        scoreLoader loader = new scoreLoader();
        loader.start();
    }

    public Image getLevel1Text()
    {
        return level1Text;
    }

    public Image getLevel2Text()
    {
        return level2Text;
    }

    public Image getLevel3Text()
    {
        return level3Text;
    }

    public Image getBossFightText()
    {
        return bossFightText;
    }

    public int getLevel()
    {
        return level;
    }

    public int getTotalTries()
    {
        return totalTries;
    }

    public int getAsteroidDeathCount()
    {
        return asteroidDeathCount;
    }

    public boolean isIsBossDead()
    {
        return isBossDead;
    }

    void drawDeadMenu(Graphics g, Image youLoseImg)
    {
        scoreListY = 450;
        g.drawImage(youLoseImg, 730, 350, null);
        g.setColor(Color.red);
        g.drawString("Press F1 to respawn!", 875, 525);
        g.setColor(Color.lightGray);
        g.drawString("HighScores", 550, scoreListY -20);

        index = 0;
        scores.forEach((score) ->
        {
            if (index < 10)
            {
                g.drawString("Name: " + score.name + "...." + score.heighScore, 550, scoreListY);
                scoreListY += 20;
            }
            index++;
        });
    }

    class scoreLoader extends Thread
    {

        scoreLoader()
        {
            scores.clear();
        }

        @Override
        public void run()
        {
            load();
        }

        public void load()
        {

            String fileName = "";
            for (int i = 0; i < 10000; i++)
            {
                File checkFile = new File("HighScores/score" + i + ".json");
                if (checkFile.exists())
                {
                    System.out.println("loading: " + "HighScores/score" + i + ".json");
                    JSONParser parser = new JSONParser();
                    try
                    {
                        Object obj = parser.parse(new FileReader("HighScores/score" + i + ".json"));
                        JSONObject jsonObject = (JSONObject) obj;
                        String name = (String) jsonObject.get("Name");
                        long score = (long) jsonObject.get("Score");
                        long elapsedTime = (long) jsonObject.get("elapsedTime");
                        System.out.println("LOADED Name: " + name);
                        System.out.println("LOADED Score: " + score);
                        System.out.println("LOADED elapsedTime: " + elapsedTime);
                        scoreData scoreObj = new scoreData();
                        scoreObj.name = name;
                        scoreObj.heighScore = score;
                        scoreObj.elapsedTime = elapsedTime;
                        scores.add(scoreObj);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    fileName = "HighScores/score" + i + ".json";
                } else
                {
                    this.stop();
                }
            }
        }
    }

    class scoreData
    {

        public String name;
        public long heighScore;
        public long elapsedTime;
    }
}
