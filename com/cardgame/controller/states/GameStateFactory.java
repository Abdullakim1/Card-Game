package com.cardgame.controller.states;

import com.cardgame.Game;

public interface GameStateFactory {
    GameState createState(Game game);
}
