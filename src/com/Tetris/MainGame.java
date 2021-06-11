package com.Tetris;

import javax.swing.*;

public class MainGame extends JFrame
{
    public MainGame()
    {
        initUI();
    }

    private void initUI()
    {
        GameLoop game = new GameLoop(12, 20);
        add(new MainWindow(game, 12, 20));
        addKeyListener(new KeysEvents(game));
        setSize(800, 800);
        setTitle("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try
        {
            ImageIcon img = new ImageIcon("img\\icon40.png");
            setIconImage(img.getImage());
        }
        catch (Exception e){}

        try
        {
            ImageIcon img = new ImageIcon(getClass().getResource("/img/icon40.png")); //IF pack in JAR
            setIconImage(img.getImage());
        }
        catch (Exception e){}
    }
}
