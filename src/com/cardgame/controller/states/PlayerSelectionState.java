package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.model.player.HumanPlayer;
import com.cardgame.model.player.Player;
import com.cardgame.model.player.PlayerManager;
import com.cardgame.view.components.ModernButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * State for selecting players before starting the game.
 * Allows adding human players and setting their names.
 */
public class PlayerSelectionState extends GameState {
    private static final int MAX_PLAYERS = 6;
    
    private PlayerManager playerManager;
    private List<String> playerNames;
    private List<Rectangle> playerNameBounds;
    private List<Rectangle> removePlayerBounds;
    
    private ModernButton addPlayerButton;
    private Rectangle addPlayerBounds;
    
    private ModernButton startGameButton;
    private Rectangle startGameBounds;
    
    private ModernButton backButton;
    private Rectangle backBounds;
    
    private int selectedPlayerIndex;
    private String currentInput;
    private boolean isInputActive;
    private boolean includeComputer = false; // Flag to indicate if we should include a computer player
    
    // Message display
    private String message = "";
    private int messageTimer = 0;
    
    /**
     * Creates a new player selection state.
     * 
     * @param game The game instance
     */
    public PlayerSelectionState(Game game) {
        super(game);
        playerManager = new PlayerManager();
        playerNames = new ArrayList<>();
        playerNameBounds = new ArrayList<>();
        removePlayerBounds = new ArrayList<>();
        selectedPlayerIndex = -1;
        currentInput = "";
        isInputActive = false;
        
        initializeButtons();
        
        // Add default players
        playerNames.add("Player 1");
        updatePlayerBounds();
    }
    
    /**
     * Initializes the buttons for adding players, starting the game, and going back.
     */
    private void initializeButtons() {
        int buttonWidth = 230;
        int buttonHeight = 50;
        int centerX = (800 - buttonWidth) / 2;
        
        addPlayerButton = new ModernButton("+ Add Player");
        addPlayerBounds = new Rectangle(centerX, 350, buttonWidth, buttonHeight);
        
        startGameButton = new ModernButton("Start Game");
        startGameBounds = new Rectangle(centerX, 420, buttonWidth, buttonHeight);
        
        backButton = new ModernButton("Back");
        backBounds = new Rectangle(centerX, 490, buttonWidth, buttonHeight);
    }
    
    /**
     * Updates the bounds for player name fields and remove buttons.
     */
    private void updatePlayerBounds() {
        int fieldWidth = 230;
        int fieldHeight = 45;
        int removeButtonSize = 30;
        int spacing = 60;
        int startY = 150;
        int centerX = (800 - fieldWidth) / 2;
        
        playerNameBounds.clear();
        removePlayerBounds.clear();
        
        for (int i = 0; i < playerNames.size(); i++) {
            int y = startY + i * spacing;
            playerNameBounds.add(new Rectangle(centerX, y, fieldWidth, fieldHeight));
            removePlayerBounds.add(new Rectangle(centerX + fieldWidth + 10, y + 7, removeButtonSize, removeButtonSize));
        }
        
        // Update button positions based on number of players
        int buttonY = startY + playerNames.size() * spacing + 20;
        addPlayerBounds.y = buttonY;
        startGameBounds.y = buttonY + 70;
        backBounds.y = buttonY + 140;
        
        // Disable add player button if max players reached
        addPlayerButton.setEnabled(playerNames.size() < MAX_PLAYERS);
    }
    
    /**
     * Adds a new player with a default name.
     */
    private void addPlayer() {
        if (playerNames.size() < MAX_PLAYERS) {
            playerNames.add("Player " + (playerNames.size() + 1));
            updatePlayerBounds();
        }
    }
    
    /**
     * Removes a player at the specified index.
     * 
     * @param index The index of the player to remove
     */
    private void removePlayer(int index) {
        if (index >= 0 && index < playerNames.size() && playerNames.size() > 1) {
            playerNames.remove(index);
            if (selectedPlayerIndex == index) {
                selectedPlayerIndex = -1;
                isInputActive = false;
                currentInput = "";
            } else if (selectedPlayerIndex > index) {
                selectedPlayerIndex--;
            }
            updatePlayerBounds();
        }
    }
    
    /**
     * Creates players from the player names and starts the game.
     */
    private void startGame() {
        // Debug message to check player count
        System.out.println("Starting game with " + playerNames.size() + " players");
        System.out.println("Include computer: " + includeComputer);
        
        // For multiplayer mode (no computer), require at least 2 players
        if (!includeComputer && playerNames.size() < 2) {
            // Show a message to add more players
            System.out.println("Need at least 2 players for multiplayer mode");
            message = "Please add at least 2 players for multiplayer mode";
            messageTimer = 180; // Show for 3 seconds at 60 FPS
            return; // Don't start the game yet
        }
        
        // Start the game with the appropriate player configuration
        game.setState(new PlayState(game, playerNames, includeComputer));
    }
    
    /**
     * Sets whether to include a computer player in the game.
     * 
     * @param includeComputer True to include a computer player, false for human-only
     */
    public void setIncludeComputer(boolean includeComputer) {
        this.includeComputer = includeComputer;
    }
    

    
    @Override
    public void tick() {
        // Update message timer
        if (messageTimer > 0) {
            messageTimer--;
        }
    }
    
    @Override
    public void render(Graphics g) {
        // Draw background - solid dark color like in the image
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dark background (almost black)
        g2d.setColor(new Color(25, 26, 31));
        g2d.fillRect(0, 0, 800, 600);
        
        // Draw title
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        String title = "Select Players";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (800 - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 80);
        
        // Draw player name fields
        for (int i = 0; i < playerNames.size(); i++) {
            Rectangle bounds = playerNameBounds.get(i);
            
            // Draw field background (dark gray)
            g2d.setColor(new Color(45, 48, 56));
            g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
            
            // Draw field border
            g2d.setColor(new Color(60, 65, 75));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
            
            // Draw player name or input
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            String text = (i == selectedPlayerIndex && isInputActive) ? currentInput + (System.currentTimeMillis() % 1000 > 500 ? "|" : "") : playerNames.get(i);
            g.drawString(text, bounds.x + 15, bounds.y + 28);
            
            // Draw remove button (X) - red circle with X
            if (playerNames.size() > 1 && i < removePlayerBounds.size()) {
                Rectangle removeBounds = removePlayerBounds.get(i);
                
                // Red circle
                g2d.setColor(new Color(220, 60, 60));
                g2d.fillOval(removeBounds.x, removeBounds.y, removeBounds.width, removeBounds.height);
                
                // X symbol
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.drawString("X", removeBounds.x + 9, removeBounds.y + 22);
            }
        }
        
        // Draw buttons directly using their render method
        addPlayerButton.render(g, addPlayerBounds.x, addPlayerBounds.y, addPlayerBounds.width, addPlayerBounds.height);
        startGameButton.render(g, startGameBounds.x, startGameBounds.y, startGameBounds.width, startGameBounds.height);
        backButton.render(g, backBounds.x, backBounds.y, backBounds.width, backBounds.height);
        
        // Draw message if timer is active
        if (messageTimer > 0) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(new Color(255, 100, 100)); // Red color for warning
            FontMetrics messageFm = g.getFontMetrics();
            int messageX = (800 - messageFm.stringWidth(message)) / 2;
            g.drawString(message, messageX, 500);
        }
    }
    
    @Override
    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();
        
        // Handle hover effects
        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            addPlayerButton.setHovered(addPlayerBounds.contains(mouse) && addPlayerButton.isEnabled());
            startGameButton.setHovered(startGameBounds.contains(mouse));
            backButton.setHovered(backBounds.contains(mouse));
            return;
        }
        
        // Handle button press effects
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            if (addPlayerBounds.contains(mouse) && addPlayerButton.isEnabled()) {
                addPlayerButton.setPressed(true);
            } else if (startGameBounds.contains(mouse)) {
                startGameButton.setPressed(true);
            } else if (backBounds.contains(mouse)) {
                backButton.setPressed(true);
            }
            
            // Check for player name field clicks
            for (int i = 0; i < playerNameBounds.size(); i++) {
                if (playerNameBounds.get(i).contains(mouse)) {
                    selectedPlayerIndex = i;
                    isInputActive = true;
                    currentInput = playerNames.get(i);
                    return;
                }
            }
            
            // Check for remove button clicks
            for (int i = 0; i < removePlayerBounds.size(); i++) {
                if (removePlayerBounds.get(i).contains(mouse) && playerNames.size() > 1) {
                    removePlayer(i);
                    return;
                }
            }
            
            // Deselect if clicked elsewhere
            selectedPlayerIndex = -1;
            isInputActive = false;
            return;
        }
        
        // Handle button release and click effects
        if (e.getID() == MouseEvent.MOUSE_RELEASED || e.getID() == MouseEvent.MOUSE_CLICKED) {
            // Reset pressed states
            addPlayerButton.setPressed(false);
            startGameButton.setPressed(false);
            backButton.setPressed(false);
            
            // Handle button actions
            if (addPlayerBounds.contains(mouse) && addPlayerButton.isEnabled()) {
                addPlayer();
            } else if (startGameBounds.contains(mouse)) {
                startGame();
            } else if (backBounds.contains(mouse)) {
                game.setState(new MenuState(game));
            }
        }
    }
    
    /**
     * Handles keyboard input for player name editing.
     * 
     * @param keyChar The character typed
     * @param keyCode The key code
     */
    public void handleKeyEvent(char keyChar, int keyCode) {
        if (!isInputActive || selectedPlayerIndex < 0) {
            return;
        }
        
        // Handle backspace
        if (keyCode == 8 && currentInput.length() > 0) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        }
        // Handle enter (confirm name)
        else if (keyCode == 10) {
            if (!currentInput.trim().isEmpty()) {
                playerNames.set(selectedPlayerIndex, currentInput.trim());
            }
            selectedPlayerIndex = -1;
            isInputActive = false;
        }
        // Handle escape (cancel editing)
        else if (keyCode == 27) {
            selectedPlayerIndex = -1;
            isInputActive = false;
        }
        // Handle regular character input
        else if (Character.isLetterOrDigit(keyChar) || Character.isSpaceChar(keyChar)) {
            if (currentInput.length() < 15) { // Limit name length
                currentInput += keyChar;
            }
        }
    }
    
    @Override
    public void onEnter() {
        // Reset button states
        addPlayerButton.setHovered(false);
        addPlayerButton.setPressed(false);
        startGameButton.setHovered(false);
        startGameButton.setPressed(false);
        backButton.setHovered(false);
        backButton.setPressed(false);
    }
    
    @Override
    public void onExit() {
        // Nothing special needed
    }
}
