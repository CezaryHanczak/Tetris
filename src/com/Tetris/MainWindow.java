package com.Tetris;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JPanel
    implements Runnable
{
    private Timer timer;
    private Thread animator;
    private Thread gameThread;
    private GameLoop game;

    public MainWindow(GameLoop game)
    {
        this.game = game;
        this.gameThread = new Thread(this.game);
        this.gameThread.start();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        drawLayout(g);
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawLayout(Graphics g)
    {
        int width = getWidth();
        int center_x = width/2;
        int center_y = getHeight()/2;
        int arcs = (int)(getHeight() * 0.01);

        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);


        int matrix_height = (int)(0.8 * getHeight());
        int matrix_width = (int)(0.6 * matrix_height);

        int matrix_block_size = matrix_width / 12;
        int block_pos_x = (center_x - matrix_width / 2);
        int block_pos_y = (center_y - matrix_height / 2);


        //Matrix rendering
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.black);
        g2d.fillRect(0,0, width, getHeight());

        for (int i = 0; i < 65; i++)
        {
            g2d.setColor(Color.getHSBColor((float)i / 64, 1, 1));
            g2d.fillRoundRect(block_pos_x, block_pos_y, matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.setColor(Color.getHSBColor((float)i / 64, (float)0.6, (float)0.4));
            g2d.drawRoundRect(block_pos_x, block_pos_y, matrix_block_size, matrix_block_size, arcs, arcs);

            if(i > 0 && i < 12)
                block_pos_x += matrix_block_size;
            else if (i >= 12 && i < 32)
                block_pos_y += matrix_block_size;
            else if (i >= 34 && i < 45)
                block_pos_x -= matrix_block_size;
            else if (i >= 45)
                block_pos_y -= matrix_block_size;
        }

        //Fonts
        g2d.setColor(Color.GRAY);
        Font font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.022));
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

        //Next figures box
        //g2d.drawRoundRect(block_pos_x + (12 * matrix_block_size) + (int)(width * 0.25 - width * 0.2), block_pos_y, (int)(width * 0.2), (int)(getHeight() * 0.6), arcs, arcs);

        //Font font = new Font("Verdana", Font.BOLD, (int)(getHeight() * 0.1));
        //g2d.setFont(font);
        //g2d.drawString("Tetris", 40, 40);

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
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                System.out.println("Animation Thread Error");
            }
        }
    }
}
