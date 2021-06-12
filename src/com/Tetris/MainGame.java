package com.Tetris;

import javax.swing.*;
import java.util.Objects;

/**
 * Główne okno aplikacji
 */
public class MainGame extends JFrame
{

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
        MainWindow main_window = new MainWindow(12, 20, sounds);
        add(main_window);
        addKeyListener(new KeysEvents( main_window));
        setSize(800, 800);
        setTitle("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        try
        {
            ImageIcon img = new ImageIcon("img\\icon40.png"); //ścieżka w IDE
            setIconImage(img.getImage());
        }
        catch (Exception e){}

        try
        {
            ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/icon40.png"))); //ścieżka bo spakwowaniu do Jar
            setIconImage(img.getImage());
        }
        catch (Exception e){}
    }
}
