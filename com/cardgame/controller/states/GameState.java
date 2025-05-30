package com.cardgame.controller.states;

import com.cardgame.Game;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public abstract class GameState {

    private Game game;

    public GameState(Game game) {
        this.game = game;
    }
    
    public Game getGame() {
        return game;
    }

    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract void onEnter();
    public abstract void onExit();
    
    public void handleMouseEvent(MouseEvent e) {

    }
}