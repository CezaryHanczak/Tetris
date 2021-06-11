package com.Tetris;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainWindow extends JPanel
    implements Runnable
{
    private Thread animator;
    private Thread gameThread;
    private GameLoop game;

    private final int size_x;
    private final int size_y;
    private int width;
    private int center_x;
    private int center_y;
    private int arcs;
    private int matrix_block_size;
    private int matrix_height;
    private int matrix_width;

    private enum GameStatus
    {
        MENU,
        NEW_GAME,
        GAME,
        GAME_OVER,
        HIGHSCORES
    }

    private GameStatus game_status;

    public MainWindow(GameLoop game, int size_x, int size_y)
    {
        this.size_x = size_x;
        this.size_y = size_y;
        this.game_status = GameStatus.NEW_GAME;
        this.game = game;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        width = getWidth();
        center_x = width/2;
        center_y = getHeight()/2;
        arcs = (int)(getHeight() * 0.01);

        matrix_height = (int)(0.8 * getHeight());
        matrix_width = (int)(0.6 * matrix_height);

        matrix_block_size = matrix_width / size_x;

        if(this.game_status == GameStatus.MENU)
        {

        }
        else if (this.game_status == GameStatus.NEW_GAME)
        {
            this.gameThread = new Thread(game);
            this.gameThread.start();
            this.game_status = GameStatus.GAME;
        }
        else if (this.game_status == GameStatus.GAME)
        {
            drawLayout(g);
            drawBlocks(g);
            Toolkit.getDefaultToolkit().sync();

            if(!this.game.getActive())
            {
                this.game_status = GameStatus.GAME_OVER;
            }
        }
        else if (this.game_status == GameStatus.GAME_OVER)
        {

        }
    }

    private void drawBlocks(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
        RenderingHints rh2 = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh2);

        int block_pos_x = (center_x - matrix_width / 2) + matrix_block_size;
        int block_pos_y = (center_y - matrix_height / 2) + matrix_block_size;

        if (game.getActive())
        {
            Tetrimino active = game.getActive_tetrimino();
            block_pos_x += active.getX() * matrix_block_size;
            block_pos_y += active.getY() * matrix_block_size;
            float color = active.getColor();

            for (int y = 0; y < 5; y++)
            {
                for (int x = 0; x < 5; x++)
                {
                    if (active.block_matrix_cpy[x][y])
                    {
                        g2d.setColor(Color.getHSBColor(color, 1, 1));
                        g2d.fillRoundRect(block_pos_x, block_pos_y, matrix_block_size, matrix_block_size, arcs, arcs);
                        g2d.setColor(Color.getHSBColor(color, (float) 0.6, (float) 0.4));
                        g2d.drawRoundRect(block_pos_x, block_pos_y, matrix_block_size, matrix_block_size, arcs, arcs);
                    }
                    block_pos_x += matrix_block_size;
                }
                block_pos_x -= matrix_block_size * 5;
                block_pos_y += matrix_block_size;
            }
        }

        ArrayList<Tetrimino> tetriminos = game.getTetriminos();

        for (int i = 0; i < tetriminos.size(); i++)
        {
            block_pos_x = (center_x - matrix_width / 2) + matrix_block_size;
            block_pos_y = (center_y - matrix_height / 2) + matrix_block_size;

            Tetrimino temp = tetriminos.get(i);
            block_pos_x += temp.getX() * matrix_block_size;
            block_pos_y += temp.getY() * matrix_block_size;
            float color = temp.getColor();

            for (int y = 0; y < 5; y++)
            {
                for (int x = 0; x < 5; x++)
                {
                    if (temp.block_matrix_cpy[x][y])
                    {
                        g2d.setColor(Color.getHSBColor(color, 1, 1));
                        g2d.fillRoundRect(block_pos_x, block_pos_y, matrix_block_size, matrix_block_size, arcs, arcs);
                        g2d.setColor(Color.getHSBColor(color, (float) 0.6, (float) 0.4));
                        g2d.drawRoundRect(block_pos_x, block_pos_y, matrix_block_size, matrix_block_size, arcs, arcs);
                    }
                    block_pos_x += matrix_block_size;
                }
                block_pos_x -= matrix_block_size * 5;
                block_pos_y += matrix_block_size;
            }
        }

    }

    private void drawLayout(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
        RenderingHints rh2 = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh2);

        int block_pos_x = (center_x - matrix_width / 2);
        int block_pos_y = (center_y - matrix_height / 2);

        //Matrix rendering
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.black);
        g2d.fillRect(0,0, width, getHeight());

        for (int i = 0; i < size_x * 2 + size_y * 2 + 1; i++)
        {
            g2d.setColor(Color.getHSBColor((float)i / 64, 1, 1));
            g2d.fillRoundRect(block_pos_x, block_pos_y, matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.setColor(Color.getHSBColor((float)i / 64, (float)0.8, (float)0.4));
            g2d.drawRoundRect(block_pos_x, block_pos_y, matrix_block_size, matrix_block_size, arcs, arcs);

            if(i > 0 && i < size_x)
                block_pos_x += matrix_block_size;
            else if (i >= size_x && i < size_x + size_y)
                block_pos_y += matrix_block_size;
            else if (i >= size_x + size_y && i < size_x * 2 + size_y - 1)
                block_pos_x -= matrix_block_size;
            else if (i >= size_x * 2 + size_y)
                block_pos_y -= matrix_block_size;
        }

        //Fonts
        g2d.setColor(Color.GRAY);
        Font font = new Font("Bauhaus 93", 0, (int)(width * 0.022));
        g2d.setFont(font);

        //Score box text generate
        String score_text = "SCORE:  " + game.getScore();
        String level_text = "LEVEL:  " + game.getLevel();
        String lines_text = "LINES:  " + game.getLines();


        //Score box
        g2d.drawRoundRect(block_pos_x - (int)(width * 0.25), block_pos_y, (int)(width * 0.2), (int)(width * 0.2), arcs, arcs);
        g2d.drawString(score_text, block_pos_x - (int)(width * 0.22), block_pos_y + (int)(width * 0.05));
        g2d.drawString(level_text, block_pos_x - (int)(width * 0.22), block_pos_y + (int)(width * 0.11));
        g2d.drawString(lines_text, block_pos_x - (int)(width * 0.22), block_pos_y + (int)(width * 0.17));

        //Next figure box
        g2d.drawRoundRect(block_pos_x + (12 * matrix_block_size) + (int)(width * 0.05), block_pos_y, (int)(width * 0.2), (int)(width * 0.2), arcs, arcs);
        font = new Font("Bauhaus 93", 0, (int)(width * 0.06));
        g2d.setFont(font);
        g2d.drawString("T e t r i s", center_x - (int)(width * 0.11), block_pos_y + (int)(width * 0.012));
//
        font = new Font("Bauhaus 93", 0, (int)(width * 0.022));
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(block_pos_x + (12 * matrix_block_size) + (int)(width * 0.074), block_pos_y - (int)(width * 0.01), (int)(width * 0.156), (int)(width * 0.03));
        g2d.setColor(Color.GRAY);
        g2d.drawString("Next Tetrimino", block_pos_x + (12 * matrix_block_size) + (int)(width * 0.084), block_pos_y + (int)(width * 0.006));

        Tetrimino next = game.getNext_tetrimino();
        float color = next.getColor();

        float centerX = next.getCenterX();
        if(centerX == -1)
            centerX = (float)2;
        else if (centerX >= 2)
            centerX = (float)0.5;
        else
            centerX = (float)1.5;

        float centerY = next.getCenterY();
        if(centerY >= 2)
            centerY = (float)0.5;
        else if(centerY == -1)
            centerY = (float)-0.6;
        else
            centerY = 0;

        int next_block_size = (int)(width * 0.2 / 6);
        for(int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                if(next.block_matrix_cpy[x][y])
                {
                    g2d.setColor(Color.getHSBColor(color, 1, 1));
                    g2d.fillRoundRect(block_pos_x + (12 * matrix_block_size) + (int)(width * 0.05) + (int)(centerX * next_block_size), block_pos_y + (int)(width * 0.05) - (int)(centerY * next_block_size), next_block_size, next_block_size, arcs, arcs);
                    g2d.setColor(Color.getHSBColor(color, (float)0.6, (float)0.4));
                    g2d.drawRoundRect(block_pos_x + (12 * matrix_block_size) + (int)(width * 0.05) + (int)(centerX * next_block_size), block_pos_y + (int)(width * 0.05) - (int)(centerY * next_block_size), next_block_size, next_block_size, arcs, arcs);
                }
                block_pos_x += next_block_size;
            }
            block_pos_x -= next_block_size * 5;
            block_pos_y += next_block_size;
        }
    }

    @Override
    public void addNotify()
    {
        super.addNotify();
        animator = new Thread(this);
        animator.start();
    }

    @Override
    public void run()
    {
        while (true)
        {
            repaint();
            try
            {
                Thread.sleep(2);
            }
            catch (InterruptedException e)
            {
                System.out.println("Animation Thread Error");
            }
        }
    }
}