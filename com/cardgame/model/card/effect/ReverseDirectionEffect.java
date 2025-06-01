package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.player.Player;


public class ReverseDirectionEffect implements CardEffect {
    @Override
    public void apply(PlayState state, Player player) {
        state.reverseDirection();
    }
    
    @Override
    public String getDescription() {
        return "Reverse direction of play";
    }
}
