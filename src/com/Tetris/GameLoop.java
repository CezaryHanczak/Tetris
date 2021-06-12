package com.Tetris;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;

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
    private boolean gameOver;
    private long last_move;

    private Tetrimino active_tetrimino;
    private Tetrimino next_tetrimino;
    private SoundEffects sounds;

    private ArrayList<Tetrimino> tetriminos;

    public GameLoop(int size_x, int size_y, SoundEffects sounds)
    {
        this.size_x = size_x;
        this.size_y = size_y;
        this.score = 0;
        this.level = 1;
        this.new_level = 9;
        this.lines = 0;
        this.round_time = 1000;
        this.is_active = false;
        this.gameOver = false;
        this.next_tetrimino = new Tetrimino();
        this.tetriminos = new ArrayList<>();    //lista bloków na planszy
        this.sounds = sounds;

        sounds.playBackgroundMusic();
    }

    /**
     * Wątek główny gry, sprawdza odpowiednie zależności, generuje nowe Tetrimino oraz wykonuje samoczynne ruchy Tetrimino
     * Po przegraniu kończy pętlę i grę oraz ustawia flagę <bold>gameOver</bold> na wartość <bold>true</bold>
     */
    public void run()
    {
        while(true)
        {
            newLevel(); //sprawdzenie czy można zwiększyć poziom
            if(!this.is_active) //jeśli poprzedni blok został ustawiony zmiana na nowy
            {
                this.active_tetrimino = this.next_tetrimino;
                this.next_tetrimino = new Tetrimino();
                this.is_active = true;
                if(checkCollision())  //sprawdzenie końca gry
                {
                    this.is_active = false;
                    this.gameOver = true;
                    this.sounds.stopBackgroundMusic();
                    this.sounds.game_over();
                    return;
                }
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
            if(date.getTime() - this.last_move >= this.round_time)  //przesuwanie Tetrimino w dół co określony czas rundy
            {
                moveDown();
                this.last_move = date.getTime();
            }
        }
    }

    private void checkLines()
    {
        int x = this.size_x - 1; //sprawdzanie od dołu



    }

    /**
     * Funkcja sprawdza czy pod jakimkolwiek elemencie z obecnego Tetrimino jest spód planszy lub część innego Tetrimino.
     * Zwraca <code>true</code> jeśli tak, <code>false</code> w przeciwnym wypadku
     */
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

    /**
     Funkcja sprawdza czy po <bold>prawej</bold> stronie jakiegokolwiek elementu z obecnego Tetrimino jest prawa koniec planszy lub część innego Tetrimino.
     Zwraca <code>true</code> jeśli tak, <code>false</code> w przeciwnym wypadku
     */
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

    /**
     * Funkcja sprawdza czy po <bold>lewej</bold> stronie jakiegokolwiek elementu z obecnego Tetrimino jest prawa koniec planszy lub część innego Tetrimino.
     * Zwraca <code>true</code> jeśli tak, <code>false</code> w przeciwnym wypadku
     */
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

    /**
     * Funkcja sprawdza czy po <bold>obróceniu</bold> obecnego Tetrimino występuje jakakolwiek kolizja.
     * Jeśli Tetrimino jest poza planszą, jest on przesuwany w odpowiednią stronę,
     * jeśli jednak występuje kolizja z innym blokiem zwracana jest wartość <code>true</code> i blok nie jest przesuwany.
     */
    private boolean checkCollisionRotate()
    {
        //sprawdzenie czy blok jest poza planszą z prawej strony i ewentualne przesunięcie
        for (int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                if (active_tetrimino.block_matrix_cpy[x][y])
                {
                    if (active_tetrimino.getX() + x >= size_x - 2)
                    {
                        active_tetrimino.moveLeft();
                    }

                }
            }
        }

        //sprawdzenie czy blok jest poza planszą z lewej strony i ewentualne przesunięcie
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

        //Sprawdzenie czy jest wolny blok po obróceniu (dla Tetrimino I) i wyrównanie do ściany
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


        //sprawdzenie kolizji z innymi Tetrimino
        for(Tetrimino tetrimino:tetriminos)
        {
            for (int y = 0; y < 5; y++)
            {
                for (int x = 0; x < 5; x++)
                {
                    for (int yy = 0; yy < 5; yy++)
                    {
                        for (int xx = 0; xx < 5; xx++)
                        {
                            if (active_tetrimino.block_matrix_cpy[x][y] && tetrimino.block_matrix_cpy[xx][yy])
                            {
                                if (active_tetrimino.getX() + x == tetrimino.getX() + xx && active_tetrimino.getY() + y == tetrimino.getY() + yy)
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Funkcja do obsługi zdarzeń przycisków,
     * Dba również o odpowienie przesuwanie Tetrimino przy próbie obrotów jeśli jest na to miejsce,
     * a także dodawania punktów przy pojedyńczym przyśpieszaniu ruchu Tetrimino
     * @param keyCode Kod znaku pobierany z {@link java.awt.event.KeyEvent}
     */
    public void keyPressed(int keyCode)
    {
        switch (keyCode) {
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                moveRight();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                moveLeft();
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_Q:
                active_tetrimino.rotateLeft();
                int left = 0;
                int right = 0;
                int x = active_tetrimino.getX();
                if(checkCollisionRotate() && !checkCollision())
                {
                    active_tetrimino.rotateRight();
                    if(!checkCollisionLeft())
                        moveLeft();
                    active_tetrimino.rotateLeft();
                    if(checkCollisionRotate() || checkCollision())
                    {
                        active_tetrimino.rotateRight();
                        if(!checkCollisionLeft())
                            moveLeft();
                        active_tetrimino.rotateLeft();
                        if(checkCollisionRotate() || checkCollision())
                        {
                            active_tetrimino.rotateRight();
                            if(!checkCollisionLeft())
                                moveLeft();
                            active_tetrimino.rotateLeft();
                            if(checkCollisionRotate() || checkCollision())
                            {
                                active_tetrimino.rotateRight();
                                if(!checkCollisionRight())
                                    moveRight();
                                active_tetrimino.rotateLeft();
                                if(checkCollisionRotate() || checkCollision())
                                {
                                    active_tetrimino.rotateRight();
                                    if(!checkCollisionRight())
                                        moveRight();
                                    active_tetrimino.rotateLeft();
                                    if(checkCollisionRotate() || checkCollision())
                                    {
                                        active_tetrimino.rotateRight();
                                        if(!checkCollisionRight())
                                            moveRight();
                                        active_tetrimino.rotateLeft();
                                        if(checkCollisionRotate() || checkCollision())
                                        {
                                            active_tetrimino.rotateRight();
                                            while(active_tetrimino.getX() - x != 0)
                                            {
                                                if(active_tetrimino.getX() < x)
                                                    moveRight();
                                                else if (active_tetrimino.getX() > x)
                                                    moveLeft();
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //Jeśli jest kolizja z Tetrimino poniżej lub końcem planszy to anulowanie obrotu
                if(checkCollision())
                {
                    active_tetrimino.rotateRight();
                    break;
                }
                this.sounds.rotate();
                break;
            case KeyEvent.VK_E:
                active_tetrimino.rotateRight();
                int right1 = 0;
                int left1 = 0;
                int x2 = active_tetrimino.getX();
                //Sprawdzanie czy można zmiecić Tetrimino po obrocie i przesunięcie w lewo lub prawo bez innych kolizji
                if(checkCollisionRotate())
                {
                    active_tetrimino.rotateLeft();
                    if(!checkCollisionLeft())
                        moveLeft();
                    active_tetrimino.rotateRight();
                    if(checkCollisionRotate() || checkCollision())
                    {
                        active_tetrimino.rotateLeft();
                        if(!checkCollisionLeft())
                            moveLeft();
                        active_tetrimino.rotateRight();
                        if(checkCollisionRotate() || checkCollision())
                        {
                            active_tetrimino.rotateLeft();
                            if(!checkCollisionLeft())
                                moveLeft();
                            active_tetrimino.rotateRight();
                            if(checkCollisionRotate() || checkCollision())
                            {
                                active_tetrimino.rotateLeft();
                                if(!checkCollisionRight())
                                    moveRight();
                                active_tetrimino.rotateRight();
                                if(checkCollisionRotate() || checkCollision())
                                {
                                    active_tetrimino.rotateLeft();
                                    if(!checkCollisionRight())
                                        moveRight();
                                    active_tetrimino.rotateRight();
                                    if(checkCollisionRotate() || checkCollision())
                                    {
                                        active_tetrimino.rotateLeft();
                                        if(!checkCollisionRight())
                                            moveRight();
                                        active_tetrimino.rotateRight();
                                        if(checkCollisionRotate() || checkCollision())
                                        {
                                            active_tetrimino.rotateLeft();
                                            while(active_tetrimino.getX() - x2 != 0)
                                            {
                                                if(active_tetrimino.getX() < x2)
                                                    moveRight();
                                                else if (active_tetrimino.getX() > x2)
                                                    moveLeft();
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //Jeśli jest kolizja z Tetrimino poniżej lub końcem planszy to anulowanie obrotu
                if(checkCollision())
                {
                    active_tetrimino.rotateLeft();
                    break;
                }
                this.sounds.rotate();
                break;
            case KeyEvent.VK_SPACE:
                moveDownAtOnce();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                moveDown();
                this.score += 2;
                break;
        }
    }

    private void moveLeft()
    {
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
    }

    private void moveRight()
    {
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
    }

    private void moveDown()
    {
        if(checkCollision())
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

    private void moveDownAtOnce()
    {
        while(!checkCollision())
            moveDown();
        this.sounds.moveDown();
        this.last_move = 0;
        this.score += 100;
    }

    private void newLevel()
    {
        if(this.lines >= this.new_level)
        {
            this.new_level *= 2;
            this.round_time /= 2;
            this.score += 1000 * this.level;
            ++this.level;
            this.sounds.levelUp();
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

    public boolean getGameOver()
    {
        return this.gameOver;
    }

    public ArrayList<Tetrimino> getTetriminos()
    {
        return this.tetriminos;
    }
}
