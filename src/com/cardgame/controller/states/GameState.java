package com.cardgame.controller.states;

import com.cardgame.Game;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public abstract class GameState {
    protected Game game;

    public GameState(Game game) {
        this.game = game;
    }

    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract void onEnter();
    public abstract void onExit();
    
    public void handleMouseEvent(MouseEvent e) {
        // Default implementation does nothing
        // Subclasses should override this if they need to handle mouse events
    }
}