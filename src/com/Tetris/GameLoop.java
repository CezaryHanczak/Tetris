package com.Tetris;

public class GameLoop
    implements Runnable
{
    private int score;
    private int level;
    private int lines;
    private int round_time;

    public GameLoop()
    {
        this.score = 0;
        this.level = 1;
        this.lines = 0;
        this.round_time = 1000;
    }

    public void run()
    {
        while(true)
        {
            try
            {
                Thread.sleep(this.round_time);
            }
            catch (InterruptedException e)
            {
                System.out.println("Game Thread Error");
            }

        }
    }

    public int getScore()
    {
        return this.score;
    }

    public int getLevel()
    {
        return this.level;
    }

    public int getLines()
    {
        return this.level;
    }
}
