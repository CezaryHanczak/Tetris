package com.Tetris;

import java.util.Date;
import java.util.Random;

import static java.lang.Math.abs;

public class Tetrimino
{
    private boolean[][] block_matrix;
    public boolean[][] block_matrix_cpy;
    private int x;
    private int y;
    private float color;

    private int center_x;
    private int center_y;

    Tetrimino()
    {
        block_matrix = new boolean[5][5];
        block_matrix_cpy = new boolean[5][5];
        x = 0;
        y = 0;
        clear();
    }

    Tetrimino(float color)
    {
        block_matrix = new boolean[5][5];
        block_matrix_cpy = new boolean[5][5];
        x = 0;
        y = 0;
        this.color = color;
        clear();
    }

    void tetriminoGenerate()
    {
        if(x == 0 && y == 0)
        {
            x = 4;
            generateBlock();
        }
    }

    private void clear()
    {
        for (int i = 0; i < 5; i++)
        {
            for (int k = 0; k < 5; k++)
            {
                block_matrix[i][k] = false;
            }
        }
        block_matrix_cpy = block_matrix;
    }

    private void generateBlock()
    {
        Random rand = new Random();
        Date date = new Date();
        long seed = date.getTime() * rand.nextInt();
        rand.setSeed(seed);
        switch (rand.nextInt(7))
        {
            case 0: //Tetrimino I
                block_matrix[2][0] = true;
                block_matrix[2][1] = true;
                block_matrix[2][2] = true;
                block_matrix[2][3] = true;
                center_x = 2;
                center_y = 2;
                x--;
                break;
            case 1: //Tetrimino T
                block_matrix[1][0] = true;
                block_matrix[0][1] = true;
                block_matrix[1][1] = true;
                block_matrix[2][1] = true;
                center_x = 1;
                center_y = 1;
                break;
            case 2: //Tetrimino O
                block_matrix[0][0] = true;
                block_matrix[0][1] = true;
                block_matrix[1][0] = true;
                block_matrix[1][1] = true;
                center_x = -1;
                center_y = -1;
                break;
            case 3: //Tetrimino L
                block_matrix[1][0] = true;
                block_matrix[1][1] = true;
                block_matrix[1][2] = true;
                block_matrix[2][2] = true;
                center_x = 1;
                center_y = 1;
                x--;
                break;
            case 4: //Tetrimino J
                block_matrix[1][0] = true;
                block_matrix[1][1] = true;
                block_matrix[1][2] = true;
                block_matrix[0][2] = true;
                center_x = 1;
                center_y = 1;
                x--;
                break;
            case 5: //Tetrimino S
                block_matrix[1][0] = true;
                block_matrix[2][0] = true;
                block_matrix[0][1] = true;
                block_matrix[1][1] = true;
                center_x = 1;
                center_y = 1;
                break;
            case 6: //Tetrimino Z
                block_matrix[0][0] = true;
                block_matrix[1][0] = true;
                block_matrix[1][1] = true;
                block_matrix[2][1] = true;
                center_x = 1;
                center_y = 1;
                break;
        }
        color = rand.nextFloat();
        block_matrix_cpy = block_matrix;
    }

    void moveDown()
    {
        this.y++;
    }

    void moveRight()
    {
        this.x++;
    }

    void moveLeft()
    {
        this.x--;
    }

    void rotateLeft()
    {
        boolean[][] temp = new boolean[5][5];

        if(center_y != -1 && center_x != -1)
        {
            for(int y = 0; y < 5; y++)
            {
                for(int x = 0; x < 5; x++)
                {
                    if(block_matrix[x][y])
                    {
                        if(x == center_x && y == center_y)
                            temp[x][y] = true;

                        if(x == center_x && y < center_y)
                            temp[y][center_y] = true;
                        if(x < center_x && y == center_y)
                            temp[center_x][center_y + (center_x - x)] = true;
                        if(x == center_x && y > center_y)
                            temp[center_x + abs((center_y - y))][center_y] = true;
                        if(x > center_x && y == center_y)
                            temp[center_x][center_y - abs(center_x - x)] = true;

                        if(x > center_x && y < center_y)
                            temp[center_x - abs(center_x - x)][y] = true;
                        if(x < center_x && y < center_y)
                            temp[x][center_y + abs(center_y - y)] = true;
                        if(x < center_x && y > center_y)
                            temp[center_x + abs(center_x - x)][y] = true;
                        if(x > center_x && y > center_y)
                            temp[x][center_y - abs(center_y - y)] = true;
                    }
                }
            }
            block_matrix = temp;
            block_matrix_cpy = block_matrix;
        }
    }

    public void rotateRight()
    {
        boolean[][] temp = new boolean[5][5];

        if(center_y != -1 && center_x != -1)
        {
            for(int y = 0; y < 5; y++)
            {
                for(int x = 0; x < 5; x++)
                {
                    if(block_matrix[x][y])
                    {
                        if(x == center_x && y == center_y)
                            temp[x][y] = true;

                        if(x == center_x && y < center_y)
                            temp[center_x + abs((center_y - y))][center_y] = true;
                        if(x < center_x && y == center_y)
                            temp[center_x][center_y - abs(center_x - x)] = true;
                        if(x == center_x && y > center_y)
                            temp[center_x - abs(center_y - y)][center_y] = true;
                        if(x > center_x && y == center_y)
                            temp[center_x][x] = true;

                        if(x > center_x && y < center_y)
                            temp[x][center_y + abs(center_y - y)] = true;
                        if(x < center_x && y < center_y)
                            temp[center_x + abs(center_x - x)][y] = true;
                        if(x < center_x && y > center_y)
                            temp[x][center_y - abs(center_y - y)] = true;
                        if(x > center_x && y > center_y)
                            temp[center_x - abs(center_x - x)][y] = true;
                    }
                }
            }
            block_matrix = temp;
            block_matrix_cpy = block_matrix;
        }
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public float getColor()
    {
        return color;
    }

    public int getCenterX()
    {
        return center_x;
    }

    public int getCenterY()
    {
        return center_y;
    }

}
