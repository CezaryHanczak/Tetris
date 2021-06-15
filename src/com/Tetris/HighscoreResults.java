package com.Tetris;

public class HighscoreResults
{
    private final int position;
    private final String nickname;
    private final String score;
    private final String level;
    private final String lines;
    private final String date;

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
