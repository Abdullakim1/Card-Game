package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.player.Player;

/**
 * Effect that reverses the direction of play
 * Demonstrates the Strategy pattern for extensibility
 */
public class ReverseDirectionEffect implements CardEffect {
    @Override
    public void apply(PlayState state, Player player) {
        // Reverse the direction of play
        state.reverseDirection();
    }
    
    @Override
    public String getDescription() {
        return "Reverse direction of play";
    }
}
