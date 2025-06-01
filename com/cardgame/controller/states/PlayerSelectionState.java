package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.model.player.Player;
import com.cardgame.view.components.ModernButton;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class PlayerSelectionState extends GameState {
    private static final int MAX_PLAYERS = 6;
    
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
    private boolean includeComputer = false; 
    
    private String message = "";
    private int messageTimer = 0;
    
    public PlayerSelectionState(Game game) {
        super(game);
        playerNames = new ArrayList<>();
        playerNameBounds = new ArrayList<>();
        removePlayerBounds = new ArrayList<>();
        selectedPlayerIndex = -1;
        currentInput = "";
        isInputActive = false;
        
        initializeButtons();
        
        playerNames.add("Player 1");
        updatePlayerBounds();
    }
    
    
    private void initializeButtons() {
        int buttonWidth = 230;
        int buttonHeight = 50;
        
        addPlayerButton = new ModernButton("+ Add Player");
        addPlayerBounds = new Rectangle(0, 0, buttonWidth, buttonHeight);
        
        startGameButton = new ModernButton("Start Game");
        startGameBounds = new Rectangle(0, 0, buttonWidth, buttonHeight);
        
        backButton = new ModernButton("Back");
        backBounds = new Rectangle(0, 0, buttonWidth, buttonHeight);
    }
    
    
    private void updatePlayerBounds() {
        int windowWidth = getGame().getWidth();
        int windowHeight = getGame().getHeight();
        
        int fieldWidth = 230;
        int fieldHeight = 45;
        int removeButtonSize = 30;
        int spacing = 60;
        int startY = windowHeight / 5;
        int centerX = (windowWidth - fieldWidth) / 2;
        
        playerNameBounds.clear();
        removePlayerBounds.clear();
        
        for (int i = 0; i < playerNames.size(); i++) {
            int y = startY + i * spacing;
            playerNameBounds.add(new Rectangle(centerX, y, fieldWidth, fieldHeight));
            removePlayerBounds.add(new Rectangle(centerX + fieldWidth + 10, y + 7, removeButtonSize, removeButtonSize));
        }
        
        int buttonY = startY + playerNames.size() * spacing + 20;
        int buttonCenterX = (windowWidth - addPlayerBounds.width) / 2;
        
        addPlayerBounds.setBounds(buttonCenterX, buttonY, addPlayerBounds.width, addPlayerBounds.height);
        startGameBounds.setBounds(buttonCenterX, buttonY + 70, startGameBounds.width, startGameBounds.height);
        backBounds.setBounds(buttonCenterX, buttonY + 140, backBounds.width, backBounds.height);
        
        addPlayerButton.setEnabled(playerNames.size() < MAX_PLAYERS);
    }
    
    
    private void addPlayer() {
        if (playerNames.size() < MAX_PLAYERS) {
            playerNames.add("Player " + (playerNames.size() + 1));
            updatePlayerBounds();
        }
    }
    
    
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
    
    
    private void startGame() {
        System.out.println("Starting game with " + playerNames.size() + " players");
        System.out.println("Include computer: " + includeComputer);
        
        if (!includeComputer && playerNames.size() < 2) {
            System.out.println("Need at least 2 players for multiplayer mode");
            message = "Please add at least 2 players for multiplayer mode";
            messageTimer = 180; 
            return; 
        }
        
        getGame().setState(new PlayState(getGame(), playerNames, includeComputer));
    }
    
    
    public void setIncludeComputer(boolean includeComputer) {
        this.includeComputer = includeComputer;
    }
    
    @Override
    public void tick() {
        if (messageTimer > 0) {
            messageTimer--;
        }
    }
    
    @Override
    public void render(Graphics g) {
        // Gets current window dimensions
        int windowWidth = getGame().getWidth();
        int windowHeight = getGame().getHeight();
        
        // Draws background - solid dark color like in the image
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(new Color(25, 26, 31));
        g2d.fillRect(0, 0, windowWidth, windowHeight);
        
        updatePlayerBounds();
        
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        String title = "Select Players";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (windowWidth - fm.stringWidth(title)) / 2;
        int titleY = windowHeight / 8;
        g.drawString(title, titleX, titleY);
        
        for (int i = 0; i < playerNames.size(); i++) {
            Rectangle bounds = playerNameBounds.get(i);
            
            if (i == selectedPlayerIndex && isInputActive) {
                g.setColor(new Color(70, 74, 82)); 
            } else {
                g.setColor(new Color(50, 54, 62));
            }
            g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
            
            if (i == selectedPlayerIndex && isInputActive) {
                g.setColor(new Color(100, 140, 255)); 
            } else {
                g.setColor(Color.GRAY);
            }
            ((Graphics2D)g).setStroke(new BasicStroke(2));
            g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            String text = (i == selectedPlayerIndex && isInputActive) 
                ? currentInput + (System.currentTimeMillis() % 1000 > 500 ? "|" : "") 
                : playerNames.get(i);
            g.drawString(text, bounds.x + 15, bounds.y + 30);
            
            if (playerNames.size() > 1) { 
                Rectangle removeBounds = removePlayerBounds.get(i);
                g.setColor(new Color(200, 80, 80));
                g.fillOval(removeBounds.x, removeBounds.y, removeBounds.width, removeBounds.height);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("X", removeBounds.x + 9, removeBounds.y + 20);
            }
        }
        
        addPlayerButton.render(g, addPlayerBounds.x, addPlayerBounds.y, addPlayerBounds.width, addPlayerBounds.height);
        startGameButton.render(g, startGameBounds.x, startGameBounds.y, startGameBounds.width, startGameBounds.height);
        backButton.render(g, backBounds.x, backBounds.y, backBounds.width, backBounds.height);
        
        if (messageTimer > 0) {
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(new Color(255, 80, 80));
            FontMetrics messageFm = g.getFontMetrics();
            int messageX = (800 - messageFm.stringWidth(message)) / 2;
            g.drawString(message, messageX, startGameBounds.y + startGameBounds.height + 30);
        }
    }
    
    @Override
    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();
        
        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            addPlayerButton.setHovered(addPlayerBounds.contains(mouse) && addPlayerButton.isEnabled());
            startGameButton.setHovered(startGameBounds.contains(mouse));
            backButton.setHovered(backBounds.contains(mouse));
            return;
        }
        
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            if (addPlayerBounds.contains(mouse) && addPlayerButton.isEnabled()) {
                addPlayerButton.setPressed(true);
            } else if (startGameBounds.contains(mouse)) {
                startGameButton.setPressed(true);
            } else if (backBounds.contains(mouse)) {
                backButton.setPressed(true);
            }
            
            for (int i = 0; i < playerNameBounds.size(); i++) {
                if (playerNameBounds.get(i).contains(mouse)) {
                    selectedPlayerIndex = i;
                    isInputActive = true;
                    currentInput = playerNames.get(i);
                    return;
                }
            }
            
            for (int i = 0; i < removePlayerBounds.size(); i++) {
                if (removePlayerBounds.get(i).contains(mouse) && playerNames.size() > 1) {
                    removePlayer(i);
                    return;
                }
            }
            
            isInputActive = false;
            if (selectedPlayerIndex >= 0) {
                playerNames.set(selectedPlayerIndex, currentInput);
                selectedPlayerIndex = -1;
            }
            
            return;
        }
        
        if (e.getID() == MouseEvent.MOUSE_RELEASED || e.getID() == MouseEvent.MOUSE_CLICKED) {
            addPlayerButton.setPressed(false);
            startGameButton.setPressed(false);
            backButton.setPressed(false);
            
            if (addPlayerBounds.contains(mouse) && addPlayerButton.isEnabled()) {
                addPlayer();
            } else if (startGameBounds.contains(mouse)) {
                startGame();
            } else if (backBounds.contains(mouse)) {
                getGame().setState(new MenuState(getGame()));
            }
        }
    }
    
    
    public void processKeyEvent(KeyEvent e) {
        if (!isInputActive || selectedPlayerIndex < 0) return;
        
        if (e.getID() == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            
            if (c == '\b') {
                if (currentInput.length() > 0) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                }
            } else if (c == '\n') {
                if (!currentInput.trim().isEmpty()) {
                    playerNames.set(selectedPlayerIndex, currentInput);
                }
                isInputActive = false;
                selectedPlayerIndex = -1;
            } else if (Character.isLetterOrDigit(c) || c == ' ' || c == '_' || c == '-') {
                if (currentInput.length() < 20) {
                    currentInput += c;
                }
            }
        }
    }
    
    @Override
    public void onEnter() {
        addPlayerButton.setHovered(false);
        addPlayerButton.setPressed(false);
        startGameButton.setHovered(false);
        startGameButton.setPressed(false);
        backButton.setHovered(false);
        backButton.setPressed(false);
    }
    
    @Override
    public void onExit() {
    }
}
