package com.Tetris;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeysEvents implements KeyListener
{
    private GameLoop game;
    public KeysEvents(GameLoop game)
    {
        this.game = game;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        game.keyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
