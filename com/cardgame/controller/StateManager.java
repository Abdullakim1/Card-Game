package com.cardgame.controller;

import com.cardgame.Game;
import com.cardgame.controller.states.*;
import com.cardgame.controller.states.MenuStateFactory;
import com.cardgame.controller.states.PlayStateFactory;
import com.cardgame.controller.states.RulesStateFactory;
import java.util.HashMap;
import java.util.Map;

public class StateManager {
    private final Game game;
    private final Map<String, GameStateFactory> stateFactories;
    private GameState currentState;
    
    public StateManager(Game game) {
        this.game = game;   //needs to be clarified
        this.stateFactories = new HashMap<>();
        
        registerStateFactory("menu", new MenuStateFactory());
        registerStateFactory("play", new PlayStateFactory());
        registerStateFactory("rules", new RulesStateFactory());
    }
    
    public void registerStateFactory(String stateId, GameStateFactory factory) {
        stateFactories.put(stateId, factory);
    }
    
    public boolean changeState(String stateId) {
        GameStateFactory factory = stateFactories.get(stateId);
        if (factory == null) {
            return false;
        }
        
        GameState newState = factory.createState(game);
        
        if (currentState != null) {
            currentState.onExit();
        }
        
        currentState = newState;
        currentState.onEnter();
        
        return true;
    }
    
    public GameState getCurrentState() {
        return currentState;
    }
    
    public void initialize(String initialStateId) {
        changeState(initialStateId);
    }
}
