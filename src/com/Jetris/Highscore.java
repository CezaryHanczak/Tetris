package com.Jetris;

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
    private final String url = "jdbc:mysql://sql4.freesqldatabase.com:3306/sql4441640?useSSL=false&requireSSL=false&serverTimezone=UTC";
    private int score;
    private int level;
    private int lines;
    private String nickname;
    private Connection connection;
    private boolean send;
    private final ArrayList<HighscoreResults> highscoreResults;
    private final int checksum;
    private MainWindow.HighscoreMode highscoreMode;

    private final Semaphore semaphore1;

    public Highscore(Semaphore semaphore, ArrayList<HighscoreResults> highscoreResults, MainWindow.HighscoreMode highscoreMode)
    {
        this.send = false;
        this.semaphore1 = semaphore;
        this.highscoreResults = highscoreResults;
        this.score = 0;
        this.level = 0;
        this.lines = 0;
        this.checksum = 0;
        this.highscoreMode = highscoreMode;
    }

    public Highscore(Semaphore semaphore, ArrayList<HighscoreResults> highscoreResults, String nickname, int score, int level, int lines, MainWindow.HighscoreMode highscoreMode)
    {
        this.send = false;
        this.semaphore1 = semaphore;
        this.highscoreResults = highscoreResults;
        this.nickname = nickname;
        this.score = score;
        this.level = level;
        this.lines = lines;
        this.send = true;
        this.highscoreMode = highscoreMode;
        this.checksum = (this.level * this.score * this.lines + this.nickname.length()) / this.nickname.length();
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
            this.connection = DriverManager.getConnection(url, "sql4441640", "wyfLC8Qm3E");
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
            ResultSet result;
            if(this.highscoreMode == MainWindow.HighscoreMode.ALL_TIME)
            {
                result = statement.executeQuery("SELECT nickname, score, lvl, line, score_date, time FROM jetris ORDER BY score DESC LIMIT 20");
            }
            else if(this.highscoreMode == MainWindow.HighscoreMode.LAST30)
            {
                result = statement.executeQuery("SELECT nickname, score, lvl, line, score_date, time FROM jetris WHERE score_date >= date_add(curdate(), INTERVAL -1 MONTH) ORDER BY score DESC LIMIT 20");
            }
            else if(this.highscoreMode == MainWindow.HighscoreMode.LAST7)
            {
                result = statement.executeQuery("SELECT nickname, score, lvl, line, score_date, time FROM jetris WHERE score_date >= date_add(curdate(), INTERVAL -7 DAY) ORDER BY score DESC LIMIT 20");
            }
            else
            {
                result = statement.executeQuery("SELECT nickname, score, lvl, line, score_date, time FROM jetris ORDER BY score DESC LIMIT 20");
            }

            this.semaphore1.acquire();
            this.highscoreResults.clear();
            this.semaphore1.release();

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
        catch (InterruptedException e)
        {
            try
            {
                connection.close();
            }
            catch (Exception e2) { }
        }
        catch (Exception e)
        {
            this.semaphore1.release();
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
            statement.execute("INSERT INTO jetris (nickname, score, lvl, line, score_date, time) VALUES (" + values + ")");
            statement.close();
        }
        catch (Exception e) { }
    }

}
