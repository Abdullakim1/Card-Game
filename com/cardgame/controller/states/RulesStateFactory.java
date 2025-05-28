package com.cardgame.controller.states;

import com.cardgame.Game;

public class RulesStateFactory implements GameStateFactory {
    @Override
    public GameState createState(Game game) {
        return new RulesState(game);
    }
}
