package com.cardgame.model.player;

/**
 * Represents a human player in the card game.
 * Extends the base Player class with human-specific functionality.
 */
public class HumanPlayer extends Player {
    
    /**
     * Creates a new human player with the specified name.
     * 
     * @param name The name of the human player
     */
    public HumanPlayer(String name) {
        super(name, false); // Human players are not computer-controlled
    }
    
    // Additional human player specific methods can be added here if needed
}
