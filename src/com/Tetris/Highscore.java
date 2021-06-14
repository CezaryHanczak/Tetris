package com.Tetris;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Highscore
    implements Runnable
{
    private String url = "jdbc:mysql://jetris.mysql.database.azure.com:3306/highscores?useSSL=false&requireSSL=false&serverTimezone=UTC";
    private Connection connection;
    private ArrayList<HighscoreResults> highscoreResults;

    private Semaphore semaphore1;

    public Highscore(Semaphore semaphore, ArrayList<HighscoreResults> highscoreResults)
    {
        this.semaphore1 = semaphore;
        this.highscoreResults = highscoreResults;
    }

    @Override
    public void run()
    {
        boolean connect = this.connect();
        if(connect)
        {
            this.getHighScores();
        }
    }

    private boolean connect()
    {
        try
        {
            this.connection = DriverManager.getConnection(url, "jetris_game@jetris", "3WaFN5f^wc7R%wFViokpt24H");
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    void getHighScores()
    {
        try
        {
            Statement statement = this.connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT nickname, score, lvl, line, score_date FROM highscores_table ORDER BY score DESC LIMIT 10 ");
            //ArrayList<HighscoreResults> result_array = new ArrayList<>();

            int i = 1;
            while(result.next())
            {
                HighscoreResults temp = new HighscoreResults(i, result.getString("nickname"), result.getString("score"), result.getString("lvl"), result.getString("line"), result.getString("score_date"));
                this.semaphore1.acquire();
                this.highscoreResults.add(temp);
                this.semaphore1.release();
                ++i;
            }

            statement.close();
            connection.close();
            return;
        }
        catch (Exception e)
        {
            return;
        }
    }
}
