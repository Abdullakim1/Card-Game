package com.cardgame.controller.states;

import com.cardgame.model.game.GameOutcome;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class GameOutcomeManager {
    private PlayState playState;

    public GameOutcomeManager(PlayState playState) {
        this.playState = playState;
    }

    public void showOutcomeAnimation() {
        try {
            playState.setOutcomeImage(GameOutcome.getRandomOutcomeImageStatic());
        } catch (Exception e) {
            System.err.println("Error updating animation: " + e.getMessage());
        }
    }

    public void render(Graphics g) {
        if (playState.isGameOver()) {
            renderGameOverScreen(g, playState.getGame().getWidth(), playState.getGame().getHeight());
        }
    }

    private void renderGameOverScreen(Graphics g, int windowWidth, int windowHeight) {
        if (playState.isShowOutcomeAnimation() && playState.isOutcomeInitialized() && playState.getOutcomeImage() != null) {
            int animX = (windowWidth - playState.getOutcomeImage().getWidth()) / 2;
            int animY = windowHeight / 2 - playState.getOutcomeImage().getHeight() / 2 + 70;
            g.setColor(new java.awt.Color(255, 215, 0));
            g.fillRect(animX - 5, animY - 5, playState.getOutcomeImage().getWidth() + 10, playState.getOutcomeImage().getHeight() + 10);
            g.drawImage(playState.getOutcomeImage(), animX, animY, null);
            g.setColor(java.awt.Color.RED);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
            g.drawString("LOSER'S FATE:", animX, animY - 10);
        } else if (playState.isShowOutcomeAnimation()) {
            g.setColor(java.awt.Color.RED);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
            g.drawString("Punishment loading...", windowWidth / 2 - 100, windowHeight / 2 + 80);
        }
    }
}