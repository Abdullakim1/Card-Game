package com.cardgame.model.card.effect;

import com.cardgame.controller.states.PlayState;
import com.cardgame.model.card.Card.CardColor;
import com.cardgame.model.player.Player;


public class WildCardEffect implements CardEffect {
    
    private CardColor chosenColor;
    
    public WildCardEffect() {
        this.chosenColor = null;
    }
    
   
    public void setChosenColor(CardColor color) {
        this.chosenColor = color;
    }
    
    @Override
    public void apply(PlayState state, Player player) {
        if (chosenColor != null) {
            state.setCurrentColor(chosenColor);
        }
        else {
            state.promptForColorSelection();
        }
    }
    
    @Override
    public String getDescription() {
        return "Change color to any color";
    }
}
