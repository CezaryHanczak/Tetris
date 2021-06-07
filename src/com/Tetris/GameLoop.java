package com.Tetris;

import java.awt.event.KeyEvent;
import java.util.Date;

public class GameLoop
    implements Runnable
{
    private final int size_x;
    private final int size_y;
    private int score;
    private int level;
    private int lines;
    private int round_time;
    private boolean is_active;
    private long last_move;
    private Tetrimino active_tetrimino;

    public GameLoop(int size_x, int size_y)
    {
        this.size_x = size_x;
        this.size_y = size_y;
        this.score = 0;
        this.level = 1;
        this.lines = 0;
        this.round_time = 1000;
        is_active = false;
    }

    public void run()
    {
        while(true)
        {
            if(!is_active)
            {
                active_tetrimino = new Tetrimino();
                is_active = true;
            }

            try
            {
                Thread.sleep(5);
            }
            catch (InterruptedException e)
            {
                System.out.println("Game Thread Error");
            }

            Date date = new Date();
            if(date.getTime() - last_move >= round_time)
            {
                moveDown();
                last_move = date.getTime();
            }
        }
    }

    private boolean checkCollision()
    {
        for(int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                if(active_tetrimino.block_matrix_cpy[x][y])
                {
                    if(active_tetrimino.getY() + y >= size_y - 2)
                        return true;
                }
            }
        }
        return false;
    }

    public void keyPressed(int keyCode)
    {
        switch (keyCode)
        {
            case KeyEvent.VK_RIGHT:
                for(int y = 0; y < 5; y++)
                {
                    for (int x = 0; x < 5; x++)
                    {
                        if(active_tetrimino.block_matrix_cpy[x][y])
                        {
                            if(active_tetrimino.getX() + x >= size_x - 3)
                               return;
                        }
                    }
                }
                active_tetrimino.moveRight();
                break;
            case KeyEvent.VK_LEFT:
                for(int y = 0; y < 5; y++)
                {
                    for (int x = 0; x < 5; x++)
                    {
                        if(active_tetrimino.block_matrix_cpy[x][y])
                        {
                            if(active_tetrimino.getX() + x <= 0)
                                return;
                        }
                    }
                }
                active_tetrimino.moveLeft();
                break;
            case KeyEvent.VK_UP:
                active_tetrimino.rotateLeft();
                for(int y = 0; y < 5; y++)
                {
                    for (int x = 0; x < 5; x++)
                    {
                        if(active_tetrimino.block_matrix_cpy[x][y])
                        {
                            if(active_tetrimino.getX() + x > size_x - 3)
                                active_tetrimino.moveLeft();
                        }
                    }
                }
                for(int y = 0; y < 5; y++)
                {
                    for (int x = 0; x < 5; x++)
                    {
                        if(active_tetrimino.block_matrix_cpy[x][y])
                        {
                            if(active_tetrimino.getX() + x <= 0)
                                active_tetrimino.moveRight();
                        }
                    }
                }
                break;
            case KeyEvent.VK_DOWN:
                moveDown();
                this.score += 2;
                break;
        }
    }

    private void moveDown()
    {
        boolean check = checkCollision();
        if(check)
        {
            is_active = false;
        }
        else
        {
            active_tetrimino.moveDown();
            last_move = new Date().getTime();
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
        return this.lines;
    }

    public Tetrimino getActive_tetrimino()
    {
        return this.active_tetrimino;
    }

    public boolean getActive()
    {
        return this.is_active;
    }
}
