package com.Tetris;

import com.microsoft.sqlserver.jdbc.StringUtils;

import javax.swing.*;
import javax.xml.transform.Result;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class MainWindow extends JPanel
    implements Runnable
{
    enum Menu
    {
        START,
        HIGHSCORES,
        QUIT
    }

    private Thread animator;
    private Thread gameThread;
    private GameLoop game;
    private final SoundEffects sounds;
    private final Semaphore semaphore1;

    private final int size_x;
    private final int size_y;
    private int width;
    private int center_x;
    private int center_y;
    private int arcs;
    private int matrix_block_size;
    private int matrix_height;
    private int matrix_width;
    private final float menuColor;
    private Menu mainMenuChose;

    private String nickname;

    private ArrayList<HighscoreResults> highscores;
    private boolean have_result;

    private enum GameStatus
    {
        MENU,
        NEW_GAME,
        GAME,
        GAME_OVER,
        HIGHSCORES
    }

    private GameStatus game_status;

    public MainWindow(int size_x, int size_y, SoundEffects sounds, Semaphore semaphore)
    {
        this.size_x = size_x;
        this.size_y = size_y;
        this.game_status = GameStatus.MENU;
        this.sounds = sounds;
        this.semaphore1 = semaphore;
        this.mainMenuChose = Menu.START;
        Random rand = new Random();
        this.menuColor =  rand.nextFloat();
        this.nickname = "";
        this.highscores = new ArrayList<>();
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
            drawMenu(g);
            Toolkit.getDefaultToolkit().sync();
        }
        else if (this.game_status == GameStatus.NEW_GAME)
        {
            this.mainMenuChose = Menu.START;
            this.game = new GameLoop(size_x, size_y, this.sounds, this.semaphore1);
            this.gameThread = new Thread(game);
            this.gameThread.start();
            this.game_status = GameStatus.GAME;
        }
        else if (this.game_status == GameStatus.GAME)
        {
            this.drawLayout(g);
            this.drawBlocks(g);
            Toolkit.getDefaultToolkit().sync();

            if(this.game.getGameOver())
            {
                this.game_status = GameStatus.GAME_OVER;
            }
        }
        else if (this.game_status == GameStatus.GAME_OVER)
        {
            this.drawGameOver(g);
            Toolkit.getDefaultToolkit().sync();
        }
        else if (this.game_status == GameStatus.HIGHSCORES)
        {
            this.drawHighscores(g);
            Toolkit.getDefaultToolkit().sync();
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

        try
        {
            this.semaphore1.acquire();
        }
        catch (Exception e) { return; }
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
        this.semaphore1.release();
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

        //Next figure box
        g2d.drawRoundRect(block_pos_x + (12 * matrix_block_size) + (int)(width * 0.05), block_pos_y, (int)(width * 0.2), (int)(width * 0.2), arcs, arcs);
        font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.06));
        g2d.setFont(font);
        g2d.drawString("T e t r i s", center_x - (int)(width * 0.11), block_pos_y + (int)(width * 0.012));

        font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.022));
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

    private void drawMenu(Graphics g)
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

        //Tło
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.black);
        g2d.fillRect(0,0, width, getHeight());


        //Tytul gry
        g2d.setColor(Color.GRAY);
        Font font = new Font("Bauhaus 93", Font.CENTER_BASELINE, (int)(width * 0.1));
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);  //do środkowania tekstów
        g2d.drawString("Tetris", center_x - (metrics.stringWidth("Tetris") / 2), (int)(getHeight() * 0.2));


        font = new Font("Bauhaus 93", Font.CENTER_BASELINE, (int)(width * 0.04));
        g2d.setFont(font);
        metrics = g2d.getFontMetrics(font);  //do środkowania tekstów

        //klocki wybierające z menu
        if(this.mainMenuChose == Menu.START)
        {
            g2d.setColor(Color.getHSBColor(this.menuColor, 1, 1));
            g2d.fillRoundRect((int)(center_x - (metrics.stringWidth("Start game") / 2) - width * 0.05), (int)(getHeight() * 0.4 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.fillRoundRect((int)(center_x + (metrics.stringWidth("Start game") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.4 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.setColor(Color.getHSBColor(this.menuColor, (float) 0.6, (float) 0.4));
            g2d.drawRoundRect((int)(center_x - (metrics.stringWidth("Start game") / 2) - width * 0.05), (int)(getHeight() * 0.4 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.drawRoundRect((int)(center_x + (metrics.stringWidth("Start game") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.4 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
        }
        else if(this.mainMenuChose == Menu.HIGHSCORES)
        {
            g2d.setColor(Color.getHSBColor(this.menuColor, 1, 1));
            g2d.fillRoundRect((int)(center_x - (metrics.stringWidth("Highscores") / 2) - width * 0.05), (int)(getHeight() * 0.5 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.fillRoundRect((int)(center_x + (metrics.stringWidth("Highscores") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.5 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.setColor(Color.getHSBColor(this.menuColor, (float) 0.6, (float) 0.4));
            g2d.drawRoundRect((int)(center_x - (metrics.stringWidth("Highscores") / 2) - width * 0.05), (int)(getHeight() * 0.5 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.drawRoundRect((int)(center_x + (metrics.stringWidth("Highscores") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.5 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
        }
        else if(this.mainMenuChose == Menu.QUIT)
        {
            g2d.setColor(Color.getHSBColor(this.menuColor, 1, 1));
            g2d.fillRoundRect((int)(center_x - (metrics.stringWidth("Quit") / 2) - width * 0.05), (int)(getHeight() * 0.6 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.fillRoundRect((int)(center_x + (metrics.stringWidth("Quit") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.6 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.setColor(Color.getHSBColor(this.menuColor, (float) 0.6, (float) 0.4));
            g2d.drawRoundRect((int)(center_x - (metrics.stringWidth("Quit") / 2) - width * 0.05), (int)(getHeight() * 0.6 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.drawRoundRect((int)(center_x + (metrics.stringWidth("Quit") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.6 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
        }

        //napisy w menu
        g2d.setColor(Color.GRAY);
        g2d.drawString("Start game", center_x - (metrics.stringWidth("Start game") / 2), (int)(getHeight() * 0.4));
        g2d.drawString("Highscores", center_x - (metrics.stringWidth("Highscores") / 2), (int)(getHeight() * 0.5));
        g2d.drawString("Quit", center_x - (metrics.stringWidth("Quit") / 2), (int)(getHeight() * 0.6));

    }

    private void drawHighscores(Graphics g)
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

        //Tło
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.black);
        g2d.fillRect(0,0, width, getHeight());

        if (!this.have_result)
        {
            try
            {
                Highscore highscore = new Highscore(this.semaphore1, this.highscores);
                Thread highscore_thread = new Thread(highscore);
                highscore_thread.start();
                this.have_result = true;
            }
            catch (Exception e) { }
        }
        else
        {
            try
            {
                this.semaphore1.acquire();
                for(HighscoreResults highscoreResults:this.highscores)
                {
                    System.out.println(highscoreResults.getPosition() + ". " + highscoreResults.getNickname());
                }
            }
            catch (Exception e) {}
            this.semaphore1.release();
        }
    }

    private void drawGameOver(Graphics g)
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

        //Tło
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.black);
        g2d.fillRect(0,0, width, getHeight());

        //Nagłówek
        g2d.setColor(Color.GRAY);
        Font font = new Font("Bauhaus 93", Font.CENTER_BASELINE, (int)(width * 0.07));
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);  //do środkowania tekstów
        g2d.drawString("Game Over!", center_x - (metrics.stringWidth("Game Over!") / 2), (int)(getHeight() * 0.2));


        font = new Font("Bauhaus 93", Font.CENTER_BASELINE, (int)(width * 0.04));
        g2d.setFont(font);
        metrics = g2d.getFontMetrics(font);  //do środkowania tekstów

        String score = "Your score: " + String.valueOf(this.game.getScore());
        String lines = "Lines: " + String.valueOf(this.game.getLines());
        String level = "Level: " + String.valueOf(this.game.getLevel());

        g2d.drawString(score, center_x - (metrics.stringWidth(score) / 2), (int)(getHeight() * 0.32));
        g2d.drawString(lines, center_x - (metrics.stringWidth(lines) / 2), (int)(getHeight() * 0.40));
        g2d.drawString(level, center_x - (metrics.stringWidth(level) / 2), (int)(getHeight() * 0.48));

        g2d.drawString("Your name:", center_x - (metrics.stringWidth("Your name:") / 2), (int)(getHeight() * 0.65));
        Calendar now = Calendar.getInstance();

        if(now.get(Calendar.SECOND) % 2 == 0)
        {
            if(this.nickname.equals(""))
                g2d.drawString(this.nickname + "|", center_x - (metrics.stringWidth(this.nickname + "|") / 2), (int)(getHeight() * 0.75));
            else
                g2d.drawString(this.nickname + "|", center_x - (metrics.stringWidth(this.nickname) / 2), (int)(getHeight() * 0.75));
        }
        else
        {
            g2d.drawString(this.nickname, center_x - (metrics.stringWidth(this.nickname) / 2), (int)(getHeight() * 0.75));
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
                Thread.sleep(4);
            }
            catch (InterruptedException e)
            {
                System.out.println("Animation Thread Error");
            }
        }
    }

    public void keyPressed(KeyEvent e)
    {
        if (this.game_status == GameStatus.GAME)
        {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                this.game.endGame();
                this.game_status = GameStatus.MENU;
            }
            else
                this.game.keyPressed(e.getKeyCode());
        }

        else if (this.game_status == GameStatus.MENU)
        {
            if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
            {
                if(this.mainMenuChose == Menu.START)
                    this.mainMenuChose = Menu.HIGHSCORES;
                else if(this.mainMenuChose == Menu.HIGHSCORES)
                    this.mainMenuChose = Menu.QUIT;
            }
            if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
            {
                if(this.mainMenuChose == Menu.QUIT)
                    this.mainMenuChose = Menu.HIGHSCORES;
                else if(this.mainMenuChose == Menu.HIGHSCORES)
                    this.mainMenuChose = Menu.START;
            }
            if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                if(this.mainMenuChose == Menu.QUIT)
                    System.exit(0);
                else if(this.mainMenuChose == Menu.HIGHSCORES)
                    this.game_status = GameStatus.HIGHSCORES;
                else if(this.mainMenuChose == Menu.START)
                    this.game_status = GameStatus.NEW_GAME;
            }
        }
        else if (this.game_status == GameStatus.GAME_OVER)
        {
            if(e.getKeyCode() == KeyEvent.VK_ENTER)
                this.game_status = GameStatus.HIGHSCORES;
            else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            {
                if(this.nickname.length() > 0)
                {
                    this.nickname = this.nickname.substring(0, this.nickname.length() - 1);
                }
            }
            else if (e.getKeyChar() >= 'A' && e.getKeyChar() <= 'Z' || e.getKeyChar() >= 'a' && e.getKeyChar() <= 'z' || e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == '-' || e.getKeyChar() == '_')
            {
                if(this.nickname.length() < 25)
                    this.nickname += e.getKeyChar();
            }
        }

    }
}