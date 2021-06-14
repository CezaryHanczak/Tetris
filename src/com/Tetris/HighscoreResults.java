package com.Tetris;

public class HighscoreResults
{
    private int position;
    private String nickname;
    private String score;
    private String level;
    private String lines;
    private String date;

    HighscoreResults(int position, String nickname, String score, String level, String lines, String date)
    {
        this.position = position;
        this.nickname = nickname;
        this.score = score;
        this.level = level;
        this.lines = lines;
        this.date = date;
    }

    int getPosition()
    {
        return this.position;
    }

    String getNickname()
    {
        return this.nickname;
    }

    String getScore()
    {
        return this.score;
    }

    String getLevel()
    {
        return this.level;
    }

    String getLines()
    {
        return this.lines;
    }

    String getDate()
    {
        return this.date;
    }
}
