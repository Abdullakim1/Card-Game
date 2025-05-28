package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.player.Player;

/**
 * Effect that forces a player to draw cards
 * Demonstrates the Strategy pattern for extensibility
 */
public class DrawCardEffect implements CardEffect {
    // Information hiding: private fields (step 1)
    // Instance variable (step 2)
    // Constant, not variable (step 3)
    private final int cardsToDraw;
    
    public DrawCardEffect(int cardsToDraw) {
        this.cardsToDraw = cardsToDraw;
    }
    
    @Override
    public void apply(PlayState state, Player player) {
        // Draw the specified number of cards
        for (int i = 0; i < cardsToDraw; i++) {
            player.drawCard(state.getDeck());
        }
        
        // Skip the player's turn
        state.skipNextTurn();
    }
    
    @Override
    public String getDescription() {
        return "Draw " + cardsToDraw + " cards and skip a turn";
    }
}
