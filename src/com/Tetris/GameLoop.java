package com.Tetris;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;

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
    private final SoundEffects sounds;
    private final Semaphore semaphore1;

    private ArrayList<Tetrimino> tetriminos;

    public GameLoop(int size_x, int size_y, SoundEffects sounds, Semaphore semaphore)
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
        this.next_tetrimino.tetriminoGenerate();
        this.tetriminos = new ArrayList<>();    //lista bloków na planszy
        this.sounds = sounds;
        this.semaphore1 = semaphore;

        CheckLines checkLines = new CheckLines(this.size_x, this.size_y, this, this.sounds, this.semaphore1);
        Thread checkLinesThread = new Thread(checkLines);
        checkLinesThread.start();

        sounds.playBackgroundMusic();
    }

    /**
     * Wątek główny gry, sprawdza odpowiednie zależności, generuje nowe Tetrimino oraz wykonuje samoczynne ruchy Tetrimino
     * Po przegraniu kończy pętlę i grę oraz ustawia flagę <bold>gameOver</bold> na wartość <bold>true</bold>
     */
    @Override
    public void run()
    {
        while(true)
        {
            this.newLevel(); //sprawdzenie czy można zwiększyć poziom
            if(!this.is_active) //jeśli poprzedni blok został ustawiony zmiana na nowy
            {
                this.active_tetrimino = this.next_tetrimino;
                this.next_tetrimino = new Tetrimino();
                this.next_tetrimino.tetriminoGenerate();
                this.is_active = true;
                if(this.checkCollision())  //sprawdzenie końca gry
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
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                System.out.println("Game Thread Error");
            }

            //przesuwanie Tetrimino w dół co określony czas rundy
            Date date = new Date();
            if(date.getTime() - this.last_move >= this.round_time)
            {
                this.moveDown();
                this.last_move = date.getTime();
                this.clearTetriminos();
            }

            //kończenie gry
            if(this.gameOver)
            {
                this.is_active = false;
                this.sounds.stopBackgroundMusic();
                break;
            }
        }
    }

    private void clearTetriminos()
    {
        try
        {
            this.semaphore1.acquire();
        }
        catch (Exception e)
        {
            return;
        }
        for(int i = 0; i < tetriminos.size(); i++)
        {
            boolean is_clear = true;
            for(int y = 0; y < 5; y++)
            {
                for(int x = 0; x < 5; x++)
                {
                    if (tetriminos.get(i).block_matrix_cpy[x][y])
                    {
                        is_clear = false;
                        break;
                    }
                }
            }
            if(is_clear)
                tetriminos.remove(i);
        }
        this.semaphore1.release();
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
                try
                {
                    this.semaphore1.acquire();
                }
                catch (Exception e)
                {
                    return false;
                }

                if(this.active_tetrimino.block_matrix_cpy[x][y])
                {
                    if(this.active_tetrimino.getY() + y >= this.size_y - 2)
                    {
                        this.semaphore1.release();
                        return true;
                    }

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
                                        this.semaphore1.release();
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                this.semaphore1.release();
            }
        }
        return false;
    }

    /**
     * Funkcja sprawdza czy pod jakimkolwiek elemencie z <bold>podanego w argumencie</bold>> jest spód planszy lub część innego Tetrimino.
     * Zwraca <code>true</code> jeśli tak, <code>false</code> w przeciwnym wypadku
     */
    private boolean checkCollision(Tetrimino tetrimino)
    {

        for(int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                try
                {
                    this.semaphore1.acquire();
                }
                catch (Exception e)
                {
                    return false;
                }

                if(tetrimino.block_matrix_cpy[x][y])
                {
                    if(tetrimino.getY() + y >= this.size_y - 2)
                    {
                        this.semaphore1.release();
                        return true;
                    }

                    for(int i = 0; i < this.tetriminos.size(); i++)
                    {
                        Tetrimino temp = this.tetriminos.get(i);
                        if(temp.getY() == tetrimino.getY() && temp.getX() == tetrimino.getX() && temp.getColor() == tetrimino.getColor())
                            break;
                        for(int y_ = 0; y_ < 5; y_++)
                        {
                            for(int x_ = 0; x_ < 5; x_++)
                            {
                                if(temp.block_matrix_cpy[x_][y_])
                                {
                                    if(x + tetrimino.getX() == x_ + temp.getX() && y + tetrimino.getY() == y_ + temp.getY()  - 1)
                                    {
                                        this.semaphore1.release();
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                this.semaphore1.release();
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
                try
                {
                    this.semaphore1.acquire();
                }
                catch (Exception e)
                {
                    return false;
                }

                if(this.active_tetrimino.block_matrix_cpy[x][y])
                {
                    if(this.active_tetrimino.getY() + y > this.size_y - 2)
                    {
                        this.semaphore1.release();
                        return true;
                    }


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
                                        this.semaphore1.release();
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                this.semaphore1.release();
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
                try
                {
                    this.semaphore1.acquire();
                }
                catch (Exception e)
                {
                    return false;
                }

                if(this.active_tetrimino.block_matrix_cpy[x][y])
                {
                    if(this.active_tetrimino.getY() + y > this.size_y - 2)
                    {
                        this.semaphore1.release();
                        return true;
                    }



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
                                        this.semaphore1.release();
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                this.semaphore1.release();
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
        try
        {
            this.semaphore1.acquire();
        }
        catch (Exception e)
        {
            return false;
        }
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
                                    this.semaphore1.release();
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        semaphore1.release();
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
        switch (keyCode)
        {
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
                this.addScore(2);
                break;
        }
    }

    private void moveLeft()
    {
        if(!this.is_active)
            return;
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
        if(!this.is_active)
            return;
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
        if(this.checkCollision())
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

    boolean moveDown(Tetrimino tetrimino)
    {
        if(!this.checkCollision(tetrimino))
        {
            tetrimino.moveDown();
            return true;
        }
        return false;
    }

    private void moveDownAtOnce()
    {
        while(!checkCollision())
            moveDown();
        this.sounds.moveDown();
        this.last_move = 0;
        this.addScore(100);
    }

    private void newLevel()
    {
        if(this.lines >= this.new_level)
        {
            this.new_level *= 1.5;
            this.round_time -= this.round_time/(this.level + 2);
            this.score += 1000 * this.level;
            ++this.level;
            this.sounds.levelUp();
        }
    }

    void endGame()
    {
        this.gameOver = true;
    }

    int getScore()
    {
        return this.score;
    }

    int getLevel()
    {
        return this.level;
    }

    int getLines()
    {
        return this.lines;
    }

    Tetrimino getActive_tetrimino()
    {
        return this.active_tetrimino;
    }

    Tetrimino getNext_tetrimino()
    {
        return this.next_tetrimino;
    }

    boolean getActive()
    {
        return this.is_active;
    }

    int getRoundTime()
    {
        return this.round_time;
    }

    boolean getGameOver()
    {
        return this.gameOver;
    }

    ArrayList<Tetrimino> getTetriminos()
    {
        return this.tetriminos;
    }

    void addScore(int score)
    {
        this.score += score;
    }

    void addLines(int lines)
    {
        this.lines += lines;
    }
}
