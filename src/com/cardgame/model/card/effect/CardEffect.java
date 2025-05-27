package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.player.Player;

public interface CardEffect {
    
    void apply(PlayState state, Player player);
    String getDescription();
}
