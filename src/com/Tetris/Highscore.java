package com.Tetris;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class Highscore
    implements Runnable
{
    private final String url = "jdbc:mysql://jetris.mysql.database.azure.com:3306/highscores?useSSL=false&requireSSL=false&serverTimezone=UTC";
    private int score;
    private int level;
    private int lines;
    private String nickname;
    private Connection connection;
    private boolean send;
    private final ArrayList<HighscoreResults> highscoreResults;
    private final int checksum;

    private final Semaphore semaphore1;

    public Highscore(Semaphore semaphore, ArrayList<HighscoreResults> highscoreResults)
    {
        this.send = false;
        this.semaphore1 = semaphore;
        this.highscoreResults = highscoreResults;
        this.score = 0;
        this.level = 0;
        this.lines = 0;
        this.checksum = 0;
    }

    public Highscore(Semaphore semaphore, ArrayList<HighscoreResults> highscoreResults, String nickname, int score, int level, int lines)
    {
        this.send = false;
        this.semaphore1 = semaphore;
        this.highscoreResults = highscoreResults;
        this.nickname = nickname;
        this.score = score;
        this.level = level;
        this.lines = lines;
        this.send = true;
        this.checksum = (this.level * this.score * this.lines + this.nickname.length()) / this.nickname.length();
        System.out.println(this.checksum);
    }

    @Override
    public void run()
    {
        boolean connect = this.connect();
        if(connect)
        {
            if(this.send)
            {
                this.sendScore();
            }
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
            ResultSet result = statement.executeQuery("SELECT nickname, score, lvl, line, score_date, time FROM highscores_table ORDER BY score DESC LIMIT 20");

            int i = 1;
            while(result.next() && i <= 10)
            {
                if((result.getInt("lvl") * result.getInt("score") * result.getInt("line") + result.getString("nickname").length()) / result.getString("nickname").length() != result.getInt("time"))
                    continue;

                HighscoreResults temp = new HighscoreResults(i, result.getString("nickname"), result.getString("score"), result.getString("lvl"), result.getString("line"), result.getString("score_date"));
                this.semaphore1.acquire();
                this.highscoreResults.add(temp);
                this.semaphore1.release();
                ++i;
            }

            statement.close();
            connection.close();
        }
        catch (Exception e)
        {
            try
            {
                connection.close();
            }
            catch (Exception e2) { }
        }
    }

    private void sendScore()
    {
        try
        {
            Calendar cal = Calendar.getInstance();
            Date date = cal.getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date_string = format.format(date);
            String values = "'" + this.nickname + "', '" + this.score + "', '" + this.level + "', '" + this.lines + "', '" + date_string + "', '" + this.checksum  + "'";

            Statement statement = this.connection.createStatement();
            statement.execute("INSERT INTO highscores_table (nickname, score, lvl, line, score_date, time) VALUES (" + values + ")");
            statement.close();
        }
        catch (Exception e) { }
    }

}
