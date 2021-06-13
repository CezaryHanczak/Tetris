package com.Tetris;

import java.util.ArrayList;

/**
 * Klasa sprawdzająca linie możliwe do usunięcia,
 * jeśli takie są, usuwa je i dodaje punkty oraz ilość zniszczonych linii.
 * Po usunięciu linii opuszcza bloki znajdujące się nad nią
 */
public class CheckLines
    implements Runnable
{
    private final SoundEffects sounds;
    private final GameLoop game;
    private final int size_y;
    private final int size_x;
    private final ArrayList<Tetrimino> tetriminos;


    public CheckLines(ArrayList<Tetrimino> tetriminos, int size_x, int size_y, GameLoop game, SoundEffects sounds)
    {
        this.tetriminos = tetriminos;
        this.size_x = size_x;
        this.size_y = size_y;
        this.game = game;
        this.sounds = sounds;
    }

    @Override
    public void run()
    {
        int y = this.size_y - 1; //sprawdzanie od dołu

        int blocks = 0;
        //wyszukanie pełnej linii
        for(; y > 0; y--)
        {
            for(int x = 0; x < this.size_x - 1; x++)
            {
                for(Tetrimino tetrimino:this.tetriminos)
                {
                    for(int yy = 0; yy < 5; yy++)
                    {
                        if(tetrimino.getY() + yy == y)
                        {
                            for(int xx = 0; xx < 5; xx++)
                            {
                                if(tetrimino.block_matrix_cpy[xx][yy] && x == tetrimino.getX() + xx)
                                {
                                    blocks++;
                                }
                            }
                        }
                    }
                }
            }

            //usunięcie bloków na linii
            if(blocks == this.size_x - 2)
            {
                this.game.addLines(1);
                this.game.addScore(200);
                int size = tetriminos.size();
                for(int i = 0; i < size; i++)
                {
                    Tetrimino tetrimino = tetriminos.get(i);
                    if (tetrimino.getY() <= y)
                    {
                        boolean seperate = false;
                        for(int yy = 0; yy < 5; yy++)
                        {
                            for(int x = 0; x < 5; x++)
                            {
                                if(tetrimino.getY() + yy == y)
                                {
                                    tetrimino.block_matrix_cpy[x][yy] = false;
                                    if(yy > 0 && yy < 4)
                                    {
                                        if(tetrimino.block_matrix_cpy[x][yy+1] && tetrimino.block_matrix_cpy[x][yy-1])
                                            seperate = true;
                                    }
                                }
                            }
                        }

                        //Rozdzielenie bloku na dwa osobne bloki
                        if(seperate)
                        {
                            //zdublowanie bloku
                            Tetrimino temp = new Tetrimino(tetrimino.getColor());

                            while(temp.getY() != tetrimino.getY())
                                temp.moveDown();

                            while(temp.getX() != tetrimino.getX())
                                temp.moveRight();

                            //wyczyszczenie górnej części starego bloku i przepisanie jej do nowego

                            for(int temp_y = 0; temp.getY() + temp_y < y; temp_y++)
                            {
                                for(int x = 0; x < 5; x++)
                                {
                                    temp.block_matrix_cpy [x][temp_y] = tetrimino.block_matrix_cpy[x][temp_y];
                                    tetrimino.block_matrix_cpy[x][temp_y] = false;
                                }
                            }

                            tetriminos.add(temp);
                            this.game.moveDown(temp);
                        }
                    }
                    this.game.moveDown(tetrimino);
                }
                this.sounds.line();
                try
                {
                    Thread.sleep(this.game.getRoundTime());
                }catch (Exception e) {}
            }
            blocks = 0;
        }

        for(Tetrimino tetrimino:tetriminos)
            this.game.moveDown(tetrimino);
    }
}
