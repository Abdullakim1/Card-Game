package com.cardgame.controller;

import com.cardgame.Game;
import com.cardgame.controller.states.GameState;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameMouseListener implements MouseListener {

    private Game game;

    public GameMouseListener(Game game) {
        this.game = game;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GameState currentState = game.getCurrentState();
        if (currentState != null) {
            currentState.handleMouseEvent(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        GameState currentState = game.getCurrentState();
        if (currentState != null) {
            currentState.handleMouseEvent(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        GameState currentState = game.getCurrentState();
        if (currentState != null) {
            currentState.handleMouseEvent(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}