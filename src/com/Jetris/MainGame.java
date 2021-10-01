package com.Jetris;

import javax.swing.*;
import java.util.Objects;
import java.util.concurrent.Semaphore;

/**
 * Główne okno aplikacji
 */
public class MainGame extends JFrame
{
    private final Semaphore semaphore1 = new Semaphore(1);
    boolean fullscreen;

    public MainGame()
    {
        initUI();
    }


    /**
     * Inicjalizacjia okna, stworzenie obiektów do obsługi dzwięków, stworzenie panelu do rysowania,
     * aktywacja KeyListenera, oraz ustawienie ikony
     */
    private void initUI()
    {
        SoundEffects sounds = new SoundEffects();
        MainWindow main_window = new MainWindow(12, 20, sounds, this.semaphore1, this);
        add(main_window);
        addKeyListener(new KeysEvents( main_window));
        setSize(800, 800);
        setTitle("Jetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setExtendedState(JFrame.NORMAL);
        setUndecorated(false);
        this.fullscreen = false;

        try
        {
            ImageIcon img = new ImageIcon("img\\icon40.png"); //ścieżka w IDE
            setIconImage(img.getImage());
        }
        catch (Exception e){}

        try
        {
            ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/icon40.png"))); //ścieżka bo spakowaniu do Jar
            setIconImage(img.getImage());
        }
        catch (Exception e){}
    }

    void toggleFullscreen()
    {
        if(this.fullscreen)
        {
            setExtendedState(JFrame.NORMAL);
            dispose();
            setUndecorated(false);
            setVisible(true);
            this.fullscreen = false;
        }
        else
        {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            dispose();
            setUndecorated(true);
            setSize(800, 800);
            setVisible(true);
            this.fullscreen = true;
        }
    }
}
