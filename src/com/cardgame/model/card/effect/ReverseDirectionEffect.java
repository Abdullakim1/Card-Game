package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.player.Player;

/**
 * Effect that reverses the direction of play
 * Demonstrates the Strategy pattern for extensibility
 */
public class ReverseDirectionEffect implements CardEffect {
    
    private final int timesToReverse;
    
    /**
     * Constructor with default value of reversing once
     */
    public ReverseDirectionEffect() {
        this.timesToReverse = 1;
    }
    
    /**
     * Constructor that allows specifying how many times to reverse
     * @param timesToReverse Number of times to reverse direction
     */
    public ReverseDirectionEffect(int timesToReverse) {
        this.timesToReverse = timesToReverse;
    }
    
    @Override
    public void apply(PlayState state, Player player) {
        // Reverse the direction of play the specified number of times
        for (int i = 0; i < timesToReverse; i++) {
            state.reverseDirection();
        }
    }
    
    @Override
    public String getDescription() {
        if (timesToReverse == 1) {
            return "Reverse direction of play";
        } else {
            return "Reverse direction of play " + timesToReverse + " times";
        }
    }
}
