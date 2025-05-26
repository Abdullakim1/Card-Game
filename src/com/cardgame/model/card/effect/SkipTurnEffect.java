package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.player.Player;

/**
 * Effect that skips a player's turn
 * Demonstrates the Strategy pattern for extensibility
 */
public class SkipTurnEffect implements CardEffect {
    @Override
    public void apply(PlayState state, Player player) {
        // Skip the player's turn
        state.skipNextTurn();
    }
    
    @Override
    public String getDescription() {
        return "Skip a turn";
    }
}
