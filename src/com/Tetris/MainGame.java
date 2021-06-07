package com.Tetris;

import javax.swing.JFrame;
import java.awt.*;

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
        setSize(650, 650);
        setTitle("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
