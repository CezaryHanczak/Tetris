package com.Jetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class MainWindow extends JPanel
    implements Runnable
{
    enum Menu
    {
        START,
        HIGHSCORES,
        QUIT,
        INFO
    }

    private enum GameStatus
    {
        MENU,
        NEW_GAME,
        GAME,
        GAME_OVER,
        HIGHSCORES,
        INFO
    }

    enum HighscoreMode
    {
        LAST7,
        LAST30,
        ALL_TIME
    }

    private Thread animator;
    private Thread gameThread;
    private GameLoop game;
    private final SoundEffects sounds;
    private final Semaphore semaphore1;
    private MainGame mainFrame;

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
    HighscoreMode highscoreMode;

    private String nickname;

    private ArrayList<HighscoreResults> highscores;
    private boolean have_result;

    private GameStatus game_status;

    /**
     * Inizjalizacja głównego panelu aplikacji
     * @param size_x szerokość pola do układania
     * @param size_y wysokość pola do układania
     * @param sounds obiekt do obsługi dzwięków
     * @param semaphore blokowanie dostępu do tablicy tetrimino
     */
    public MainWindow(int size_x, int size_y, SoundEffects sounds, Semaphore semaphore, MainGame mainFrame)
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
        this.mainFrame = mainFrame;
        this.highscoreMode = HighscoreMode.LAST30;
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
        matrix_width = (int)(0.6 * (matrix_height + size_x));

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
        else if (this.game_status == GameStatus.INFO)
        {
            this.drawInfo(g);
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
        g2d.setColor(Color.LIGHT_GRAY);
        Font font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.022));
        g2d.setFont(font);

        //Music play info
        String music = "";
        if(this.sounds.bck_music_plays)
            music = "Music: ON";
        else
            music = "Music: OFF";

        g2d.drawString(music, block_pos_x + (size_x * matrix_block_size) + (int)(width * 0.10), block_pos_y + (int)(width * 0.22));

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
        g2d.drawRoundRect(block_pos_x + (size_x * matrix_block_size) + (int)(width * 0.05), block_pos_y, (int)(width * 0.2), (int)(width * 0.2), arcs, arcs);
        font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.06));
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);
        g2d.drawString("J e t r i s", (int)(center_x - (metrics.stringWidth("J e t r i s") / 2)), block_pos_y + (int)(width * 0.012));

        font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.022));
        g2d.setFont(font);
        metrics = g2d.getFontMetrics(font);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(block_pos_x + (size_x * matrix_block_size) + (int)(width * 0.15) - metrics.stringWidth("Next Tetrimino") / 2, block_pos_y - (int)(width * 0.01), metrics.stringWidth("Next Tetrimino") + 10, (int)(width * 0.03));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString("Next Tetrimino", block_pos_x + (size_x * matrix_block_size) + (int)(width * 0.15) - ( metrics.stringWidth("Next Tetrimino") - 10) / 2, block_pos_y + (int)(width * 0.006));

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
                    g2d.fillRoundRect(block_pos_x + (size_x * matrix_block_size) + (int)(width * 0.05) + (int)(centerX * next_block_size), block_pos_y + (int)(width * 0.05) - (int)(centerY * next_block_size), next_block_size, next_block_size, arcs, arcs);
                    g2d.setColor(Color.getHSBColor(color, (float)0.6, (float)0.4));
                    g2d.drawRoundRect(block_pos_x + (size_x * matrix_block_size) + (int)(width * 0.05) + (int)(centerX * next_block_size), block_pos_y + (int)(width * 0.05) - (int)(centerY * next_block_size), next_block_size, next_block_size, arcs, arcs);
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
        g2d.setColor(Color.LIGHT_GRAY);
        Font font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.1));
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);  //do środkowania tekstów
        g2d.drawString("Jetris", center_x - (metrics.stringWidth("Jetris") / 2), (int)(getHeight() * 0.2));


        font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.04));
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
        else if(this.mainMenuChose == Menu.INFO)
        {
            g2d.setColor(Color.getHSBColor(this.menuColor, 1, 1));
            g2d.fillRoundRect((int)(center_x - (metrics.stringWidth("Info") / 2) - width * 0.05), (int)(getHeight() * 0.6 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.fillRoundRect((int)(center_x + (metrics.stringWidth("Info") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.6 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.setColor(Color.getHSBColor(this.menuColor, (float) 0.6, (float) 0.4));
            g2d.drawRoundRect((int)(center_x - (metrics.stringWidth("Info") / 2) - width * 0.05), (int)(getHeight() * 0.6 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.drawRoundRect((int)(center_x + (metrics.stringWidth("Info") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.6 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
        }
        else if(this.mainMenuChose == Menu.QUIT)
        {
            g2d.setColor(Color.getHSBColor(this.menuColor, 1, 1));
            g2d.fillRoundRect((int)(center_x - (metrics.stringWidth("Quit") / 2) - width * 0.05), (int)(getHeight() * 0.7 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.fillRoundRect((int)(center_x + (metrics.stringWidth("Quit") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.7 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.setColor(Color.getHSBColor(this.menuColor, (float) 0.6, (float) 0.4));
            g2d.drawRoundRect((int)(center_x - (metrics.stringWidth("Quit") / 2) - width * 0.05), (int)(getHeight() * 0.7 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            g2d.drawRoundRect((int)(center_x + (metrics.stringWidth("Quit") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.7 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
        }

        //napisy w menu
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString("Start game", center_x - (metrics.stringWidth("Start game") / 2), (int)(getHeight() * 0.4));
        g2d.drawString("Highscores", center_x - (metrics.stringWidth("Highscores") / 2), (int)(getHeight() * 0.5));
        g2d.drawString("Info", center_x - (metrics.stringWidth("Info") / 2), (int)(getHeight() * 0.6));
        g2d.drawString("Quit", center_x - (metrics.stringWidth("Quit") / 2), (int)(getHeight() * 0.7));

        font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.02));
        g2d.setFont(font);
        metrics = g2d.getFontMetrics(font);  //do środkowania tekstów
        g2d.drawString("Created by: Cezary Hanczak", center_x - (metrics.stringWidth("Created by: Cezary Hanczak") / 2), (int)(getHeight() * 0.9));

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

        //Nagłówek
        g2d.setColor(Color.LIGHT_GRAY);
        Font font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.1));
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);  //do środkowania tekstów
        g2d.drawString("Highscores", center_x - (metrics.stringWidth("Highscores") / 2), (int)(getHeight() * 0.2));



        font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.025));
        g2d.setFont(font);
        metrics = g2d.getFontMetrics(font);  //do środkowania tekstów

        if (!this.have_result)
        {
            try
            {
                Highscore highscore = new Highscore(this.semaphore1, this.highscores, this.highscoreMode);
                Thread highscore_thread = new Thread(highscore);
                highscore_thread.start();
                this.have_result = true;
            }
            catch (Exception e) { }
        }
        else
        {
            g2d.drawString( "Last 7 days", (int) (center_x - (metrics.stringWidth("Last 7 days") / 2) - (width * 0.2)), (int)(getHeight() * (0.30)));
            g2d.drawString( "Last 30 days", (int) (center_x - (metrics.stringWidth("Last 30 days") / 2)), (int)(getHeight() * (0.30)));
            g2d.drawString( "All time", (int) (center_x - (metrics.stringWidth("All time") / 2) + (width * 0.2)), (int)(getHeight() * (0.30)));

            if (this.highscoreMode == HighscoreMode.LAST30)
            {
                g2d.setColor(Color.getHSBColor(this.menuColor, 1, 1));
                g2d.fillRoundRect((int)(center_x - (metrics.stringWidth("Last 30 days") / 2) - width * 0.05), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
                g2d.fillRoundRect((int)(center_x + (metrics.stringWidth("Last 30 days") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
                g2d.setColor(Color.getHSBColor(this.menuColor, (float) 0.6, (float) 0.4));
                g2d.drawRoundRect((int)(center_x - (metrics.stringWidth("Last 30 days") / 2) - width * 0.05), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
                g2d.drawRoundRect((int)(center_x + (metrics.stringWidth("Last 30 days") / 2) + width * 0.05 - matrix_block_size), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            }
            else if (this.highscoreMode == HighscoreMode.LAST7)
            {
                g2d.setColor(Color.getHSBColor(this.menuColor, 1, 1));
                g2d.fillRoundRect((int)(center_x - (metrics.stringWidth("Last 7 days") / 2) - width * 0.05 - (width * 0.2)), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
                g2d.fillRoundRect((int)(center_x + (metrics.stringWidth("Last 7 days") / 2) + width * 0.05 - matrix_block_size - (width * 0.2)), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
                g2d.setColor(Color.getHSBColor(this.menuColor, (float) 0.6, (float) 0.4));
                g2d.drawRoundRect((int)(center_x - (metrics.stringWidth("Last 7 days") / 2) - width * 0.05 - (width * 0.2)), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
                g2d.drawRoundRect((int)(center_x + (metrics.stringWidth("Last 7 days") / 2) + width * 0.05 - matrix_block_size - (width * 0.2)), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            }
            else if (this.highscoreMode == HighscoreMode.ALL_TIME)
            {
                g2d.setColor(Color.getHSBColor(this.menuColor, 1, 1));
                g2d.fillRoundRect((int)(center_x - (metrics.stringWidth("All time") / 2) - width * 0.05 + (width * 0.2)), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
                g2d.fillRoundRect((int)(center_x + (metrics.stringWidth("All time") / 2) + width * 0.05 - matrix_block_size + (width * 0.2)), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
                g2d.setColor(Color.getHSBColor(this.menuColor, (float) 0.6, (float) 0.4));
                g2d.drawRoundRect((int)(center_x - (metrics.stringWidth("All time") / 2) - width * 0.05 + (width * 0.2)), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
                g2d.drawRoundRect((int)(center_x + (metrics.stringWidth("All time") / 2) + width * 0.05 - matrix_block_size + (width * 0.2)), (int)(getHeight() * 0.3 - metrics.getHeight() / 4 - matrix_block_size / 2), matrix_block_size, matrix_block_size, arcs, arcs);
            }


            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString( "Position", (int) (center_x - (metrics.stringWidth("Position") / 2) - (width * 0.4)), (int)(getHeight() * (0.38)));
            g2d.drawString("Nickname", (int) (center_x - (metrics.stringWidth("Nickname") / 2) - (width * 0.20)), (int)(getHeight() * (0.38)));
            g2d.drawString("Score", (int) (center_x - (metrics.stringWidth("Score") / 2) + (width * 0.1)), (int)(getHeight() * (0.38)));
            g2d.drawString("Level", (int) (center_x - (metrics.stringWidth("Level") / 2) + (width * 0.24)), (int)(getHeight() * (0.38)));
            g2d.drawString("Lines", (int) (center_x - (metrics.stringWidth("Lines") / 2) + (width * 0.3)), (int)(getHeight() * (0.38)));
            g2d.drawString("Date", (int) (center_x - (metrics.stringWidth("Date") / 2) + (width * 0.4)), (int)(getHeight() * (0.38)));

            font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.015));
            g2d.setFont(font);
            metrics = g2d.getFontMetrics(font);  //do środkowania tekstów
            try
            {
                this.semaphore1.acquire();
                float i = (float)0.08;
                int counter = 1;

                for(HighscoreResults highscoreResults:this.highscores)
                {
                    String position = highscoreResults.getPosition() + ".";
                    g2d.drawString(position, (int) (center_x - (metrics.stringWidth(position) / 2) - (width * 0.4)), (int)(getHeight() * (0.35 + i)));

                    g2d.drawString(highscoreResults.getNickname(), (int) (center_x - (metrics.stringWidth(highscoreResults.getNickname()) / 2) - (width * 0.20)), (int)(getHeight() * (0.35 + i)));
                    g2d.drawString(highscoreResults.getScore(), (int) (center_x - (metrics.stringWidth(highscoreResults.getScore()) / 2) + (width * 0.1)), (int)(getHeight() * (0.35 + i)));
                    g2d.drawString(highscoreResults.getLevel(), (int) (center_x - (metrics.stringWidth(highscoreResults.getLevel()) / 2) + (width * 0.24)), (int)(getHeight() * (0.35 + i)));
                    g2d.drawString(highscoreResults.getLines(), (int) (center_x - (metrics.stringWidth(highscoreResults.getLines()) / 2) + (width * 0.3)), (int)(getHeight() * (0.35 + i)));
                    g2d.drawString(highscoreResults.getDate(), (int) (center_x - (metrics.stringWidth(highscoreResults.getDate()) / 2) + (width * 0.4)), (int)(getHeight() * (0.35 + i)));

                    i += 0.06;
                    counter++;
                }

                while (counter <= 10)
                {
                    g2d.drawString(counter + ".", (int) (center_x - (metrics.stringWidth(counter + ".") / 2) - (width * 0.4)), (int)(getHeight() * (0.35 + i)));

                    g2d.drawString("...", (int) (center_x - (metrics.stringWidth("...") / 2) - (width * 0.20)), (int)(getHeight() * (0.35 + i)));
                    g2d.drawString("...", (int) (center_x - (metrics.stringWidth("...") / 2) + (width * 0.1)), (int)(getHeight() * (0.35 + i)));
                    g2d.drawString("...", (int) (center_x - (metrics.stringWidth("...") / 2) + (width * 0.24)), (int)(getHeight() * (0.35 + i)));
                    g2d.drawString("...", (int) (center_x - (metrics.stringWidth("...") / 2) + (width * 0.3)), (int)(getHeight() * (0.35 + i)));
                    g2d.drawString("...", (int) (center_x - (metrics.stringWidth("...") / 2) + (width * 0.4)), (int)(getHeight() * (0.35 + i)));
                    i += 0.06;
                    counter++;
                }
            }
            catch (InterruptedException e)
            {
                return;
            }
            catch (Exception e)
            {
                this.semaphore1.release();
            }
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
        g2d.setColor(Color.LIGHT_GRAY);
        Font font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.07));
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);  //do środkowania tekstów
        g2d.drawString("Game Over!", center_x - (metrics.stringWidth("Game Over!") / 2), (int)(getHeight() * 0.2));


        font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.04));
        g2d.setFont(font);
        metrics = g2d.getFontMetrics(font);  //do środkowania tekstów

        g2d.setColor(Color.LIGHT_GRAY);
        String score = "Your score: " + this.game.getScore();
        String lines = "Lines: " + this.game.getLines();
        String level = "Level: " + this.game.getLevel();

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

    private void drawInfo(Graphics g)
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
        g2d.setColor(Color.LIGHT_GRAY);
        Font font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.07));
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);  //do środkowania tekstów
        g2d.drawString("Info", center_x - (metrics.stringWidth("Info") / 2), (int)(getHeight() * 0.12));


        font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.03));
        g2d.setFont(font);
        metrics = g2d.getFontMetrics(font);  //do środkowania tekstów
        g2d.drawString("Keys:", center_x - (metrics.stringWidth("Keys") / 2), (int)(getHeight() * 0.25));

        font = new Font("Bauhaus 93", Font.PLAIN, (int)(width * 0.02));
        g2d.setFont(font);
        metrics = g2d.getFontMetrics(font);  //do środkowania tekstów

        g2d.drawString("Tetrimino move:     'A' and 'D' or left and right arrows", center_x - (metrics.stringWidth("Tetrimino move:     'A' and 'D' or left and right arrows") / 2), (int)(getHeight() * 0.35));
        g2d.drawString("Tetrimino rotate:     'Q' and 'E' or up arrow", center_x - (metrics.stringWidth("Tetrimino rotate:     'Q' and 'E' or up arrow") / 2), (int)(getHeight() * 0.40));
        g2d.drawString("Tetrimino speed up:     'S' or down arrow", center_x - (metrics.stringWidth("Tetrimino speed up:     'S' or down arrow") / 2), (int)(getHeight() * 0.45));
        g2d.drawString("Tetrimino drop:     'space bar'", center_x - (metrics.stringWidth("Tetrimino drop:     'space bar'") / 2), (int)(getHeight() * 0.50));
        g2d.drawString("Music pause/play:     'M'", center_x - (metrics.stringWidth("Music pause/play:     'M'") / 2), (int)(getHeight() * 0.55));
        g2d.drawString("Quit:     'ESC'", center_x - (metrics.stringWidth("Quit:     'ESC'") / 2), (int)(getHeight() * 0.60));
        g2d.drawString("Accept:     'Enter'", center_x - (metrics.stringWidth("Accept:     'Enter'") / 2), (int)(getHeight() * 0.65));
        g2d.drawString("Fullscrenn toggle:     'F'", center_x - (metrics.stringWidth("Fullscrenn toggle:     'F'") / 2), (int)(getHeight() * 0.70));


        g2d.drawString("Game created by Cezary Hanczak", center_x - (metrics.stringWidth("Game created by Cezary Hanczak") / 2), (int)(getHeight() * 0.80));
        g2d.drawString("Thanks to rap2h, JonnyRuss01, myfox14, mrickey13, ryanharding95, ScreamStudio and InspectorJ", center_x - (metrics.stringWidth("Thanks to rap2h, JonnyRuss01, myfox14, mrickey13, ryanharding95, ScreamStudio and InspectorJ") / 2), (int)(getHeight() * 0.85));
        g2d.drawString("for the sounds effects", center_x - (metrics.stringWidth("for the sounds effects") / 2), (int)(getHeight() * 0.90));
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
                Thread.sleep(5);
            }
            catch (InterruptedException e)
            {
                System.out.println("Animation Thread Error");
            }
        }
    }

    public void keyPressed(KeyEvent e)
    {
        //Fullscreen toggle
        if(e.getKeyCode() == KeyEvent.VK_F)
        {
            this.mainFrame.toggleFullscreen();
        }

        if (this.game_status == GameStatus.GAME)
        {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                this.sounds.click();
                this.game.endGame();
                this.game_status = GameStatus.MENU;
            }
            else if (e.getKeyCode() == KeyEvent.VK_M)
            {
                this.sounds.switchBackgroundMusic();
            }
            else
                this.game.keyPressed(e.getKeyCode());
        }

        else if (this.game_status == GameStatus.MENU)
        {
            if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
            {
                this.sounds.click();
                if(this.mainMenuChose == Menu.START)
                    this.mainMenuChose = Menu.HIGHSCORES;
                else if(this.mainMenuChose == Menu.HIGHSCORES)
                    this.mainMenuChose = Menu.INFO;
                else if(this.mainMenuChose == Menu.INFO)
                    this.mainMenuChose = Menu.QUIT;
            }
            if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
            {
                this.sounds.click();
                if(this.mainMenuChose == Menu.QUIT)
                    this.mainMenuChose = Menu.INFO;
                else if(this.mainMenuChose == Menu.INFO)
                    this.mainMenuChose = Menu.HIGHSCORES;
                else if(this.mainMenuChose == Menu.HIGHSCORES)
                    this.mainMenuChose = Menu.START;
            }
            if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                this.sounds.click();
                if(this.mainMenuChose == Menu.QUIT)
                    System.exit(0);
                else if(this.mainMenuChose == Menu.HIGHSCORES)
                    this.game_status = GameStatus.HIGHSCORES;
                else if(this.mainMenuChose == Menu.START)
                    this.game_status = GameStatus.NEW_GAME;
                else if(this.mainMenuChose == Menu.INFO)
                    this.game_status = GameStatus.INFO;
            }
        }
        else if (this.game_status == GameStatus.GAME_OVER)
        {
            if(e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                try
                {
                    Highscore highscore = new Highscore(this.semaphore1, this.highscores, this.nickname, this.game.getScore(), this.game.getLevel(), this.game.getLines(), this.highscoreMode);
                    Thread highscore_thread = new Thread(highscore);
                    highscore_thread.start();
                    this.have_result = true;
                    this.highscoreMode = HighscoreMode.LAST30;
                }
                catch (Exception e2) { }

                this.sounds.click();
                this.game_status = GameStatus.HIGHSCORES;
            }
            else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            {
                if(this.nickname.length() > 0)
                {
                    this.sounds.click();
                    this.nickname = this.nickname.substring(0, this.nickname.length() - 1);
                }
            }
            else if (e.getKeyChar() >= 'A' && e.getKeyChar() <= 'Z' || e.getKeyChar() >= 'a' && e.getKeyChar() <= 'z' || e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == '-' || e.getKeyChar() == '_')
            {
                if(this.nickname.length() < 25)
                {
                    this.sounds.click();
                    this.nickname += e.getKeyChar();
                }
            }
        }
        else if (this.game_status == GameStatus.HIGHSCORES)
        {
            if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                this.sounds.click();
                this.have_result = false;
                this.game_status = GameStatus.MENU;
            }
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            {
                this.sounds.click();
                this.have_result = false;
                if(this.highscoreMode == HighscoreMode.LAST30)
                    this.highscoreMode = HighscoreMode.ALL_TIME;
                else if (this.highscoreMode == HighscoreMode.ALL_TIME)
                    this.highscoreMode = HighscoreMode.LAST7;
                else if (this.highscoreMode == HighscoreMode.LAST7)
                    this.highscoreMode = HighscoreMode.LAST30;
            }
            else if (e.getKeyCode() == KeyEvent.VK_LEFT)
            {
                this.sounds.click();
                this.have_result = false;
                if(this.highscoreMode == HighscoreMode.LAST30)
                    this.highscoreMode = HighscoreMode.LAST7;
                else if (this.highscoreMode == HighscoreMode.LAST7)
                    this.highscoreMode = HighscoreMode.ALL_TIME;
                else if (this.highscoreMode == HighscoreMode.ALL_TIME)
                    this.highscoreMode = HighscoreMode.LAST30;
            }
        }
        else if (this.game_status == GameStatus.INFO)
        {
            if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                this.sounds.click();
                this.game_status = GameStatus.MENU;
            }
        }

    }
}