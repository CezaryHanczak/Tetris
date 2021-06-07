package com.Tetris;

public class Tetrimino
{
    private int[][] block_matrix;
    public int[][] block_matrix_cpy;
    private int x;
    private int y;

    public Tetrimino()
    {
        block_matrix = new int[5][5];
        block_matrix_cpy = new int[5][5];
        x = 4;
        y = 0;
        clear();
    }

    private void clear()
    {
        for (int i = 0; i < 5; i++)
        {
            for (int k = 0; k < 5; k++)
            {
                block_matrix[i][k] = 0;
                block_matrix_cpy[i][k] = 0;
            }
        }
    }
}
