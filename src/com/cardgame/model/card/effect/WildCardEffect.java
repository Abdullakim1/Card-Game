package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.card.Card.CardColor;
import com.cardgame.model.player.Player;

/**
 * Effect that allows changing the current color
 * Demonstrates the Strategy pattern for extensibility
 */
public class WildCardEffect implements CardEffect {
    // Information hiding: private fields (step 1)
    // Instance variable (step 2)
    // Variable, not constant (step 3)
    private CardColor chosenColor;
    
    public WildCardEffect() {
        // Default to a neutral color until chosen
        this.chosenColor = null;
    }
    
    /**
     * Sets the color chosen by the player
     * @param color The new color
     */
    public void setChosenColor(CardColor color) {
        this.chosenColor = color;
    }
    
    @Override
    public void apply(PlayState state, Player player) {
        // If color has been chosen, change the current color
        if (chosenColor != null) {
            state.setCurrentColor(chosenColor);
        }
        // Otherwise, prompt for color selection
        else {
            state.promptForColorSelection();
        }
    }
    
    @Override
    public String getDescription() {
        return "Change color to any color";
    }
}
