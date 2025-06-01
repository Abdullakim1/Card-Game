package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.player.Player;


public class SkipTurnEffect implements CardEffect {
    @Override
    public void apply(PlayState state, Player player) {
        state.skipNextTurn();
    }
    
    @Override
    public String getDescription() {
        return "Skip a turn";
    }
}
