package com.cardgame.controller.states;

import com.cardgame.Game;

public class MenuStateFactory implements GameStateFactory {
    @Override
    public GameState createState(Game game) {
        return new MenuState(game);
    }
}
