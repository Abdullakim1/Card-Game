package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.player.Player;


public class DrawCardEffect implements CardEffect {
    private final int cardsToDraw;
    
    public DrawCardEffect(int cardsToDraw) {
        this.cardsToDraw = cardsToDraw;
    }
    
    @Override
    public void apply(PlayState state, Player player) {
        for (int i = 0; i < cardsToDraw; i++) {
            player.drawCard(state.getDeck());
        }
    }
    
    @Override
    public String getDescription() {
        return "Draw " + cardsToDraw + " cards.";
    }
}
