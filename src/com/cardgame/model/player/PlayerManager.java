package com.cardgame.model.player;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the players in the card game.
 * Handles adding, removing, and accessing players.
 */
public class PlayerManager {
    private List<Player> players;
    private int currentPlayerIndex;
    
    /**
     * Creates a new player manager with an empty list of players.
     */
    public PlayerManager() {
        players = new ArrayList<>();
        currentPlayerIndex = 0;
    }
    
    /**
     * Adds a player to the game.
     * 
     * @param player The player to add
     */
    public void addPlayer(Player player) {
        if (player != null) {
            players.add(player);
        }
    }
    
    /**
     * Removes a player from the game.
     * 
     * @param index The index of the player to remove
     * @return The removed player, or null if the index is invalid
     */
    public Player removePlayer(int index) {
        if (index >= 0 && index < players.size()) {
            return players.remove(index);
        }
        return null;
    }
    
    /**
     * Gets the current player.
     * 
     * @return The current player, or null if there are no players
     */
    public Player getCurrentPlayer() {
        if (players.isEmpty()) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }
    
    /**
     * Advances to the next player.
     * 
     * @return The next player, or null if there are no players
     */
    public Player nextPlayer() {
        if (players.isEmpty()) {
            return null;
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return getCurrentPlayer();
    }
    
    /**
     * Gets all players.
     * 
     * @return A list of all players
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }
    
    /**
     * Gets the number of players.
     * 
     * @return The number of players
     */
    public int getPlayerCount() {
        return players.size();
    }
    
    /**
     * Gets a player by index.
     * 
     * @param index The index of the player
     * @return The player at the specified index, or null if the index is invalid
     */
    public Player getPlayer(int index) {
        if (index >= 0 && index < players.size()) {
            return players.get(index);
        }
        return null;
    }
    
    /**
     * Gets the index of the current player.
     * 
     * @return The index of the current player
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    
    /**
     * Sets the current player index.
     * 
     * @param index The index to set
     */
    public void setCurrentPlayerIndex(int index) {
        if (index >= 0 && index < players.size()) {
            currentPlayerIndex = index;
        }
    }
    
    /**
     * Clears all players.
     */
    public void clear() {
        players.clear();
        currentPlayerIndex = 0;
    }
}
