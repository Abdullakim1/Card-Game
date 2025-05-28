package com.cardgame.controller.states;

import com.cardgame.Game;

public class PlayStateFactory implements GameStateFactory {
    @Override
    public GameState createState(Game game) {
        return new PlayState(game);
    }
}
