package com.cardgame.model.player.types;

public class HumanPlayer extends AbstractPlayer {
    public HumanPlayer(String name, int position) {
        super(name, PlayerType.HUMAN, position);
    }
}
