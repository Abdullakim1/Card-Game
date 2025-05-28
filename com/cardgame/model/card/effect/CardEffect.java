package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.player.Player;

/**
 * Interface for card effects demonstrating the Strategy pattern
 * This provides extensibility by allowing new effects to be added
 * without modifying existing code (Open/Closed Principle)
 */
public interface CardEffect {
    /**
     * Applies the effect of a card
     * @param state The current play state
     * @param player The player affected by this effect
     */
    void apply(PlayState state, Player player);
    
    /**
     * Gets a description of this effect
     * @return A human-readable description
     */
    String getDescription();
}
