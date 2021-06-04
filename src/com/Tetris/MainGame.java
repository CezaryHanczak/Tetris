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
        add(new MainWindow());
        setSize(450, 600);
        setTitle("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
