package com.cardgame.model.game;

public class GameManager {
    private static GameManager instance;
    private int currentScore;

    private GameManager() {
        this.currentScore = 0;
    }
    
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void addScore(int points) {
        this.currentScore += points;
    }

    public void resetGame() {
        this.currentScore = 0;
    }
}
