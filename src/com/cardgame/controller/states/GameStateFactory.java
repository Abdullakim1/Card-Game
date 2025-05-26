package com.cardgame.controller.states;

import com.cardgame.Game;

public interface GameStateFactory {
    GameState createState(Game game);
}

class MenuStateFactory implements GameStateFactory {
    @Override
    public GameState createState(Game game) {
        return new MenuState(game);
    }
}

class PlayStateFactory implements GameStateFactory {
    @Override
    public GameState createState(Game game) {
        return new PlayState(game);
    }
}

class RulesStateFactory implements GameStateFactory {
    @Override
    public GameState createState(Game game) {
        return new RulesState(game);
    }
}
