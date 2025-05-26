package com.cardgame.controller;

import com.cardgame.Game;
import com.cardgame.controller.states.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages game states using the Factory pattern for better extensibility
 * This class demonstrates the Open/Closed Principle by allowing new states
 * to be added without modifying existing code
 */
public class StateManager {
    // Information hiding: private fields (step 1)
    // Instance variables (step 2)
    // Some constant, some variable (step 3)
    private final Game game;
    private final Map<String, GameStateFactory> stateFactories;
    private GameState currentState;
    
    public StateManager(Game game) {
        this.game = game;
        this.stateFactories = new HashMap<>();
        
        // Register default state factories
        registerStateFactory("menu", new MenuStateFactory());
        registerStateFactory("play", new PlayStateFactory());
        registerStateFactory("rules", new RulesStateFactory());
    }
    
    /**
     * Registers a new state factory, allowing for extensibility
     * New game states can be added without modifying this class
     */
    public void registerStateFactory(String stateId, GameStateFactory factory) {
        stateFactories.put(stateId, factory);
    }
    
    /**
     * Changes to a different game state using the appropriate factory
     * @param stateId The identifier for the state to change to
     * @return true if the state was changed, false if the state ID was not found
     */
    public boolean changeState(String stateId) {
        GameStateFactory factory = stateFactories.get(stateId);
        if (factory == null) {
            return false;
        }
        
        // Create the new state using the factory
        GameState newState = factory.createState(game);
        
        // Exit the current state if it exists
        if (currentState != null) {
            currentState.onExit();
        }
        
        // Set and enter the new state
        currentState = newState;
        currentState.onEnter();
        
        return true;
    }
    
    /**
     * Gets the current game state
     */
    public GameState getCurrentState() {
        return currentState;
    }
    
    /**
     * Initializes the state manager with a default state
     */
    public void initialize(String initialStateId) {
        changeState(initialStateId);
    }
}
