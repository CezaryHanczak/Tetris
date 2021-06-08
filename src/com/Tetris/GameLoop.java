package com.Tetris;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Math.abs;

public class GameLoop
    implements Runnable
{
    private final int size_x;
    private final int size_y;
    private int score;
    private int level;
    private int new_level;
    private int lines;
    private int round_time;
    private boolean is_active;
    private long last_move;

    private Tetrimino active_tetrimino;
    private Tetrimino next_tetrimino;

    private ArrayList<Tetrimino> tetriminos;

    public GameLoop(int size_x, int size_y)
    {
        this.size_x = size_x;
        this.size_y = size_y;
        this.score = 0;
        this.level = 1;
        this.new_level = 9;
        this.lines = 0;
        this.round_time = 1000;
        this.is_active = false;
        this.next_tetrimino = new Tetrimino();
        this.tetriminos = new ArrayList<>();
    }

    public void run()
    {
        while(true)
        {
            newLevel();
            if(!this.is_active)
            {
                this.active_tetrimino = this.next_tetrimino;
                this.next_tetrimino = new Tetrimino();
                this.is_active = true;
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
            if(date.getTime() - this.last_move >= this.round_time)
            {
                moveDown();
                this.last_move = date.getTime();
            }
        }
    }

    private boolean checkCollision()
    {
        for(int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                if(this.active_tetrimino.block_matrix_cpy[x][y])
                {
                    if(this.active_tetrimino.getY() + y >= this.size_y - 2)
                        return true;

                    for(int i = 0; i < this.tetriminos.size(); i++)
                    {
                        Tetrimino temp = this.tetriminos.get(i);

                        for(int y_ = 0; y_ < 5; y_++)
                        {
                            for(int x_ = 0; x_ < 5; x_++)
                            {
                                if(temp.block_matrix_cpy[x_][y_])
                                {
                                    if(x + this.active_tetrimino.getX() == x_ + temp.getX() && y + this.active_tetrimino.getY() == y_ + temp.getY()  - 1)
                                    {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean checkCollisionRight()
    {
        for(int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                if(this.active_tetrimino.block_matrix_cpy[x][y])
                {
                    if(this.active_tetrimino.getY() + y >= this.size_y - 2)
                        return true;

                    for(int i = 0; i < this.tetriminos.size(); i++)
                    {
                        Tetrimino temp = this.tetriminos.get(i);

                        for(int y_ = 0; y_ < 5; y_++)
                        {
                            for(int x_ = 0; x_ < 5; x_++)
                            {
                                if(temp.block_matrix_cpy[x_][y_])
                                {
                                    if(x + this.active_tetrimino.getX() == x_ + temp.getX() - 1 && y + this.active_tetrimino.getY() == y_ + temp.getY())
                                    {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean checkCollisionLeft()
    {
        for(int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                if(this.active_tetrimino.block_matrix_cpy[x][y])
                {
                    if(this.active_tetrimino.getY() + y >= this.size_y - 2)
                        return true;

                    for(int i = 0; i < this.tetriminos.size(); i++)
                    {
                        Tetrimino temp = this.tetriminos.get(i);

                        for(int y_ = 0; y_ < 5; y_++)
                        {
                            for(int x_ = 0; x_ < 5; x_++)
                            {
                                if(temp.block_matrix_cpy[x_][y_])
                                {
                                    if(x + this.active_tetrimino.getX() - 1 == x_ + temp.getX() && y + this.active_tetrimino.getY() == y_ + temp.getY())
                                    {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void keyPressed(int keyCode)
    {
        switch (keyCode) {
            case KeyEvent.VK_RIGHT:
                for (int y = 0; y < 5; y++)
                {
                    for (int x = 0; x < 5; x++)
                    {
                        if (active_tetrimino.block_matrix_cpy[x][y])
                        {
                            if (active_tetrimino.getX() + x >= size_x - 3 || checkCollisionRight())
                                return;
                        }
                    }
                }
                active_tetrimino.moveRight();
                break;
            case KeyEvent.VK_LEFT:
                for (int y = 0; y < 5; y++)
                {
                    for (int x = 0; x < 5; x++)
                    {
                        if (active_tetrimino.block_matrix_cpy[x][y])
                        {
                            if (active_tetrimino.getX() + x <= 0 || checkCollisionLeft())
                                return;
                        }
                    }
                }
                active_tetrimino.moveLeft();
                break;
            case KeyEvent.VK_UP:
                active_tetrimino.rotateLeft();
                for (int y = 0; y < 5; y++)
                {
                    for (int x = 0; x < 5; x++)
                    {
                        if (active_tetrimino.block_matrix_cpy[x][y])
                        {
                            if (active_tetrimino.getX() + x >= size_x - 2)
                                active_tetrimino.moveLeft();
                        }
                    }
                }
                boolean flag = false;
                for (int y = 0; y < 5; y++)
                {
                    for (int x = 4; x > 0; x--)
                    {
                        if (active_tetrimino.block_matrix_cpy[x][y])
                        {
                            if (active_tetrimino.getX() + x <= 0)
                            {

                                active_tetrimino.moveRight();
                                flag = true;
                            }
                        }
                    }
                }

                boolean flag2;
                if (flag)
                {
                    for(int x = 0; x < 5; x++)
                    {
                        flag2 = false;
                        for(int y = 0; y < 5; y++)
                        {
                            if (active_tetrimino.block_matrix_cpy[x][y])
                            {
                                flag2 = true;
                                break;
                            }
                        }
                        if(flag2)
                            break;
                        else
                            active_tetrimino.moveLeft();
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
            this.is_active = false;
            Tetrimino temp = this.active_tetrimino;
            this.tetriminos.add(temp);
        }
        else
        {
            if(this.is_active)
            {
                this.active_tetrimino.moveDown();
                this.last_move = new Date().getTime();
            }
        }
    }

    private void newLevel()
    {
        if(this.lines >= this.new_level)
        {
            this.new_level *= 2;
            this.round_time /= 2;
            this.score += 1000 * this.level;
            ++this.level;
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

    public Tetrimino getNext_tetrimino()
    {
        return this.next_tetrimino;
    }

    public boolean getActive()
    {
        return this.is_active;
    }

    public ArrayList<Tetrimino> getTetriminos()
    {
        return this.tetriminos;
    }
}
