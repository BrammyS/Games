package javagame;

import java.io.File;
import javax.sound.sampled.*;

public class Sound
{

    private Clip clip;

    Sound(String fileName)
    {
        try
        {
            File file = new File(fileName);
            AudioInputStream sound = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(sound);
        } catch (Exception e)
        {

        }
    }

    public void play()
    {
        clip.setFramePosition(0);
        clip.start();
    }

    public void playBackgroundMusic()
    {
        clip.setFramePosition(0);
        clip.start();
        System.out.println(clip.getBufferSize());
        clip.loop(10);
    }

    public void stopMusic()
    {
        clip.stop();
    }

    public boolean isPlaying()
    {
        return clip.isActive();
    }
}
