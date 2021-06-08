package com.Tetris;

import javax.swing.JFrame;

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
    }
}
