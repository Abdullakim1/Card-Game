package com.cardgame.controller;

import com.cardgame.Game;
import com.cardgame.controller.states.GameState;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class GameMouseMotionListener implements MouseMotionListener {

    private Game game;

    public GameMouseMotionListener(Game game) {
        this.game = game;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        GameState currentState = game.getCurrentState();
        if (currentState != null) {
            currentState.handleMouseEvent(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        GameState currentState = game.getCurrentState();
        if (currentState != null) {
            currentState.handleMouseEvent(e);
        }
    }
}