package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.view.components.ModernButton;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple state for entering a single player name before playing against the computer.
 */
public class SinglePlayerNameState extends GameState {
    private ModernButton startGameButton;
    private Rectangle startGameBounds;
    
    private ModernButton backButton;
    private Rectangle backBounds;
    
    private Rectangle nameFieldBounds;
    private String playerName = "Player 1";
    private boolean isInputActive = false;
    
    public SinglePlayerNameState(Game game) {
        super(game);
        initializeButtons();
    }
    
    private void initializeButtons() {
        // Calculate button positions
        int buttonWidth = 200;
        int buttonHeight = 50;
        
        // These will be updated in render() to use actual window dimensions
        nameFieldBounds = new Rectangle(0, 0, buttonWidth, 45);
        startGameBounds = new Rectangle(0, 0, buttonWidth, buttonHeight);
        backBounds = new Rectangle(0, 0, buttonWidth, buttonHeight);
        
        startGameButton = new ModernButton("Start Game");
        backButton = new ModernButton("Back to Menu");
    }
    
    @Override
    public void tick() {
        // No continuous updates needed
    }
    
    @Override
    public void render(Graphics g) {
        // Get current window dimensions
        int windowWidth = getGame().getWidth();
        int windowHeight = getGame().getHeight();
        
        // Draw background
        g.setColor(new Color(40, 44, 52));
        g.fillRect(0, 0, windowWidth, windowHeight);
        
        // Draw title
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.setColor(Color.WHITE);
        String title = "Enter Your Name";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (windowWidth - fm.stringWidth(title)) / 2;
        int titleY = windowHeight / 5;
        g.drawString(title, titleX, titleY);
        
        // Update positions based on current window size
        int buttonWidth = 200;
        int buttonHeight = 50;
        int centerX = (windowWidth - buttonWidth) / 2;
        int startY = titleY + 80;
        int spacing = 70;
        
        // Update bounds
        nameFieldBounds.setBounds(centerX, startY, buttonWidth, 45);
        startGameBounds.setBounds(centerX, startY + spacing, buttonWidth, buttonHeight);
        backBounds.setBounds(centerX, startY + spacing * 2, buttonWidth, buttonHeight);
        
        // Draw name field
        g.setColor(new Color(60, 64, 72));
        g.fillRoundRect(nameFieldBounds.x, nameFieldBounds.y, nameFieldBounds.width, nameFieldBounds.height, 10, 10);
        
        // Draw border
        if (isInputActive) {
            g.setColor(new Color(100, 140, 255)); // Highlight when active
        } else {
            g.setColor(Color.GRAY);
        }
        ((Graphics2D)g).setStroke(new BasicStroke(2));
        g.drawRoundRect(nameFieldBounds.x, nameFieldBounds.y, nameFieldBounds.width, nameFieldBounds.height, 10, 10);
        
        // Draw player name or input
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String text = isInputActive ? playerName + (System.currentTimeMillis() % 1000 > 500 ? "|" : "") : playerName;
        g.drawString(text, nameFieldBounds.x + 15, nameFieldBounds.y + 28);
        
        // Draw buttons
        startGameButton.render(g, startGameBounds.x, startGameBounds.y, startGameBounds.width, startGameBounds.height);
        backButton.render(g, backBounds.x, backBounds.y, backBounds.width, backBounds.height);
    }
    
    @Override
    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();
        
        // Handle hover effects
        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            startGameButton.setHovered(startGameBounds.contains(mouse));
            backButton.setHovered(backBounds.contains(mouse));
            return;
        }
        
        // Handle button press effects
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            if (startGameBounds.contains(mouse)) {
                startGameButton.setPressed(true);
            } else if (backBounds.contains(mouse)) {
                backButton.setPressed(true);
            } else if (nameFieldBounds.contains(mouse)) {
                isInputActive = true;
            } else {
                isInputActive = false;
            }
            return;
        }
        
        // Handle button release and click effects
        if (e.getID() == MouseEvent.MOUSE_RELEASED || e.getID() == MouseEvent.MOUSE_CLICKED) {
            // Reset pressed states
            startGameButton.setPressed(false);
            backButton.setPressed(false);
            
            // Handle button actions
            if (startGameBounds.contains(mouse)) {
                startGame();
            } else if (backBounds.contains(mouse)) {
                getGame().setState(new MenuState(getGame()));
            }
        }
    }
    
    /**
     * Processes keyboard input for the name field.
     * This is called from the Game class when keyboard events occur.
     * 
     * @param e The keyboard event
     */
    public void processKeyEvent(KeyEvent e) {
        if (!isInputActive) return;
        
        if (e.getID() == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            
            if (c == '\b') {
                // Backspace
                if (playerName.length() > 0) {
                    playerName = playerName.substring(0, playerName.length() - 1);
                }
            } else if (c == '\n') {
                // Enter key - start game
                startGame();
            } else if (Character.isLetterOrDigit(c) || c == ' ' || c == '_' || c == '-') {
                // Only allow letters, numbers, spaces, underscores, and hyphens
                if (playerName.length() < 20) { // Limit name length
                    playerName += c;
                }
            }
        }
    }
    
    /**
     * Starts the game with the entered player name and a computer player.
     */
    private void startGame() {
        if (playerName.trim().isEmpty()) {
            playerName = "Player 1"; // Default name if empty
        }
        
        List<String> playerNames = new ArrayList<>();
        playerNames.add(playerName);
        
        // Start a game with the player name and a computer player
        getGame().setState(new PlayState(getGame(), playerNames, true));
    }
    
    @Override
    public void onEnter() {
        // Reset button states
        startGameButton.setHovered(false);
        startGameButton.setPressed(false);
        backButton.setHovered(false);
        backButton.setPressed(false);
    }
    
    @Override
    public void onExit() {
        // Nothing to clean up
    }
}
