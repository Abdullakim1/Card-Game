package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.player.Player;

/**
 * Effect that skips the next player's turn
 * Demonstrates the Strategy pattern for extensibility
 */
public class SkipTurnEffect implements CardEffect {
    
    private final int turnsToSkip;
    
    /**
     * Constructor with default value of skipping one turn
     */
    public SkipTurnEffect() {
        this.turnsToSkip = 1;
    }
    
    /**
     * Constructor that allows specifying how many turns to skip
     * @param turnsToSkip Number of turns to skip
     */
    public SkipTurnEffect(int turnsToSkip) {
        this.turnsToSkip = turnsToSkip;
    }
    
    @Override
    public void apply(PlayState state, Player player) {
        // Skip the specified number of turns
        for (int i = 0; i < turnsToSkip; i++) {
            state.skipNextTurn();
        }
    }
    
    @Override
    public String getDescription() {
        if (turnsToSkip == 1) {
            return "Skip a turn";
        } else {
            return "Skip " + turnsToSkip + " turns";
        }
    }
}
