package com.cardgame.controller.states;

import com.cardgame.Game;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public abstract class GameState {
    // Information hiding: private field (step 1)
    // Instance variable (step 2)
    // Variable, not constant (step 3)
    private Game game;

    public GameState(Game game) {
        this.game = game;
    }
    
    // Getter method to provide controlled access to the game field
    protected Game getGame() {
        return game;
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