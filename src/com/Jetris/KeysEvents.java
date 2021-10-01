package com.Jetris;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Klasa obsługująca zdarzenia klawiatury i przekazująca zdarzenie do obiektu {@link MainWindow}
 */
public class KeysEvents implements KeyListener
{
    private final MainWindow main_window;
    public KeysEvents(MainWindow main_window)
    {
        this.main_window = main_window;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        main_window.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
