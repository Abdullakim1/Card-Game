package com.cardgame.controller;

import com.cardgame.Game;
import com.cardgame.controller.states.GameState;
import com.cardgame.controller.states.PlayerSelectionState;
import com.cardgame.controller.states.SinglePlayerNameState;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameKeyListener implements KeyListener {

    private Game game;

    public GameKeyListener(Game game) {
        this.game = game;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        GameState currentState = game.getCurrentState();
        if (currentState instanceof SinglePlayerNameState) {
            ((SinglePlayerNameState) currentState).processKeyEvent(e);
        } else if (currentState instanceof PlayerSelectionState) {
            ((PlayerSelectionState) currentState).processKeyEvent(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}