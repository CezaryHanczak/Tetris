package com.Jetris;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

/**
 * Klasa do obsługi efektów dzwiękowych i muzyki
 */
public class SoundEffects
{
    private Clip bck_music_clip;
    boolean bck_music_plays;
    private Clip game_over_clip;
    private Clip new_level_clip;
    private Clip line_clip;
    private Clip rotate_clip;
    private Clip move_down_clip;
    private Clip menu_click_clip;


    /**
     * Inizjalizacji i utworzenie klipów dzwiękowych do efektów jeśli to możliwe
     */
    public SoundEffects()
    {
        try
        {
            this.game_over_clip = open_clip("game_over.wav");
        }
        catch (Exception e)
        {
            System.out.println("Game over file error");
        }

        try
        {
            this.new_level_clip = open_clip("levelup.wav");
        }
        catch (Exception e)
        {
            System.out.println("Level up file error");
        }

        try
        {
            this.line_clip = open_clip("line.wav");
        }
        catch (Exception e)
        {
            System.out.println("Line file error");
        }

        try
        {
            this.menu_click_clip = open_clip("click.wav");
        }
        catch (Exception e)
        {
            System.out.println("Click file error");
        }

        try
        {
            this.rotate_clip = open_clip("rotate.wav");
        }
        catch (Exception e)
        {
            System.out.println("Rotate file error");
        }

        try
        {
            this.move_down_clip = open_clip("movedown.wav");
        }
        catch (Exception e)
        {
            System.out.println("Movedown file error");
        }

    }

    /**
     * Funkcja otwiera plik dzwiękowy i zwraca utworzony obiekt Clip
     *
     * @param name Nazwa pliku dzwiękowego
     * @return Obiekt typu <bold>Clip</bold> z otwrtym efektem dzwiękowym
     * @throws Exception wyjątek w przypadku niepomyślnego otwarcia pliku dzwiękowego
     */
    private Clip open_clip(String name) throws Exception
    {
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/" + name)); //Otwarcie pliku w przypadku spakowania aplikacji do JAR
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            return clip;
        }
        catch (Exception e)
        {
            try
            {
                File bck_music = new File("sounds//" + name);  //Otwarcie pliku w przypadku korzystania z IDE
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(bck_music);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                return clip;
            }
            catch (Exception e2)
            {
                throw new Exception(e);
            }
        }
    }

    /**
     * Funkcja otwiera i zapętla plik dzwiękowy aż do zastopowania go funkcją <bold>SoundEffects.stopBackgroundMusic()</bold>
     */
    void playBackgroundMusic()
    {
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/bck_music.wav")); //Otwarcie pliku w przypadku spakowania aplikacji do JAR
            this.bck_music_clip = AudioSystem.getClip();
            this.bck_music_clip.open(audioInput);
            FloatControl volume = (FloatControl) this.bck_music_clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue((float)-10);
            this.bck_music_clip.start();
            this.bck_music_clip.loop(Clip.LOOP_CONTINUOUSLY);
            this.bck_music_plays = true;
        }
        catch (Exception e)
        {
            try
            {
                File bck_music = new File("sounds//bck_music.wav"); //Otwarcie pliku w przypadku korzystania z IDE
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(bck_music);
                this.bck_music_clip = AudioSystem.getClip();
                this.bck_music_clip.open(audioInput);
                FloatControl volume = (FloatControl) this.bck_music_clip.getControl(FloatControl.Type.MASTER_GAIN);
                volume.setValue((float)-10);
                this.bck_music_clip.start();
                this.bck_music_clip.loop(Clip.LOOP_CONTINUOUSLY);
                this.bck_music_plays = true;
            }
            catch (Exception e2)
            {
                System.out.println("Music error");
            }
        }
    }

    /**
     * Funkcja zatrzymuje muzykę
     */
    void stopBackgroundMusic()
    {
        try
        {
            this.bck_music_clip.stop();
            this.game_over_clip.setFramePosition(0);
            this.bck_music_clip.close();
            this.bck_music_plays = false;
        }
        catch (Exception e) {}
    }

    void switchBackgroundMusic()
    {
        if(this.bck_music_plays)
        {
            this.bck_music_clip.stop();
            this.bck_music_plays = false;
        }
        else
        {
            this.bck_music_clip.start();
            this.bck_music_plays = true;
        }
    }

    /**
     * Funkcja odtawrza jednokrotnie efekt dzwiękowy
     */
    void game_over()
    {
        try
        {
            this.game_over_clip.stop();
            this.game_over_clip.setFramePosition(0);
            this.game_over_clip.start();
        }
        catch (Exception e)
        {
            System.out.println("Game over clip error");
        }
    }

    /**
     * Funkcja odtawrza jednokrotnie efekt dzwiękowy
     */
    void click()
    {
        try
        {
            this.menu_click_clip.stop();
            this.menu_click_clip.setFramePosition(0);
            this.menu_click_clip.start();
        }
        catch (Exception e)
        {
            System.out.println("Click clip error");
        }
    }

    /**
     * Funkcja odtawrza jednokrotnie efekt dzwiękowy
     */
    void rotate()
    {
        try
        {
            this.rotate_clip.setFramePosition(0);
            this.rotate_clip.stop();
            this.rotate_clip.start();
        }
        catch (Exception e)
        {
            System.out.println("Rotate clip error");
        }
    }

    /**
     * Funkcja odtawrza jednokrotnie efekt dzwiękowy
     */
    void levelUp()
    {
        try
        {
            this.new_level_clip.stop();
            this.new_level_clip.setFramePosition(0);
            this.new_level_clip.start();
        }
        catch (Exception e)
        {
            System.out.println("New level clip error");
        }
    }

    /**
     * Funkcja odtawrza jednokrotnie efekt dzwiękowy
     */
    void line()
    {
        try
        {
            this.line_clip.stop();
            this.line_clip.setFramePosition(0);
            this.line_clip.start();
        }
        catch (Exception e)
        {
            System.out.println("Line clip error");
        }
    }

    /**
     * Funkcja odtawrza jednokrotnie efekt dzwiękowy
     */
    void moveDown()
    {
        try
        {
            this.move_down_clip.stop();
            this.move_down_clip.setFramePosition(0);
            this.move_down_clip.start();
        }
        catch (Exception e)
        {
            System.out.println("Move down clip error");
        }
    }
}
