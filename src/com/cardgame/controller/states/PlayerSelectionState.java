package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.model.player.types.AbstractPlayer;
import com.cardgame.model.player.types.HumanPlayer;
import com.cardgame.model.player.types.ComputerPlayer;
import com.cardgame.view.components.ModernButton;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PlayerSelectionState extends GameState {
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;
    
    private ModernButton[] playerCountButtons;
    private ModernButton[] playerTypeButtons;
    private ModernButton startButton;
    private ModernButton backButton;
    private Rectangle[] playerCountBounds;
    private Rectangle[] playerTypeBounds;
    private Rectangle startBounds;
    private Rectangle backBounds;
    
    private int selectedPlayerCount;
    private boolean[] isHuman;  // true for human, false for computer
    private String message;

    public PlayerSelectionState(Game game) {
        super(game);
        initializeUI();
    }

    private void initializeUI() {
        selectedPlayerCount = MIN_PLAYERS;
        isHuman = new boolean[MAX_PLAYERS];
        isHuman[0] = true;  // First player is human by default
        
        // Initialize buttons
        playerCountButtons = new ModernButton[MAX_PLAYERS - MIN_PLAYERS + 1];
        playerCountBounds = new Rectangle[MAX_PLAYERS - MIN_PLAYERS + 1];
        for (int i = 0; i < playerCountButtons.length; i++) {
            playerCountButtons[i] = new ModernButton((i + MIN_PLAYERS) + " Players");
            playerCountBounds[i] = new Rectangle(300, 150 + i * 60, 200, 40);
        }
        
        playerTypeButtons = new ModernButton[MAX_PLAYERS];
        playerTypeBounds = new Rectangle[MAX_PLAYERS];
        for (int i = 0; i < MAX_PLAYERS; i++) {
            playerTypeButtons[i] = new ModernButton("Player " + (i + 1) + ": " + (isHuman[i] ? "Human" : "Computer"));
            playerTypeBounds[i] = new Rectangle(300, 300 + i * 60, 200, 40);
        }
        
        startButton = new ModernButton("Start Game");
        startBounds = new Rectangle(300, 550, 200, 40);
        
        backButton = new ModernButton("Back");
        backBounds = new Rectangle(50, 550, 100, 40);
        
        message = "Select number of players and their types";
    }

    @Override
    public void render(Graphics g) {
        // Draw background
        g.setColor(new Color(40, 44, 52));
        g.fillRect(0, 0, 800, 600);
        
        // Draw title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Player Selection", 300, 100);
        
        // Draw message
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(message, 300, 130);
        
        // Draw player count buttons
        for (int i = 0; i < playerCountButtons.length; i++) {
            playerCountButtons[i].render(g, playerCountBounds[i], 
                selectedPlayerCount == (i + MIN_PLAYERS));
        }
        
        // Draw player type buttons (only show active ones)
        for (int i = 0; i < selectedPlayerCount; i++) {
            playerTypeButtons[i].render(g, playerTypeBounds[i], false);
        }
        
        // Draw control buttons
        startButton.render(g, startBounds, false);
        backButton.render(g, backBounds, false);
    }

    @Override
    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();
        
        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            // Handle hover states
            for (int i = 0; i < playerCountButtons.length; i++) {
                playerCountButtons[i].setHovered(playerCountBounds[i].contains(mouse));
            }
            for (int i = 0; i < selectedPlayerCount; i++) {
                playerTypeButtons[i].setHovered(playerTypeBounds[i].contains(mouse));
            }
            startButton.setHovered(startBounds.contains(mouse));
            backButton.setHovered(backBounds.contains(mouse));
            return;
        }

        if (e.getID() == MouseEvent.MOUSE_RELEASED) {
            // Handle player count selection
            for (int i = 0; i < playerCountButtons.length; i++) {
                if (playerCountBounds[i].contains(mouse)) {
                    selectedPlayerCount = i + MIN_PLAYERS;
                    return;
                }
            }
            
            // Handle player type selection
            for (int i = 0; i < selectedPlayerCount; i++) {
                if (playerTypeBounds[i].contains(mouse)) {
                    isHuman[i] = !isHuman[i];
                    playerTypeButtons[i].setText("Player " + (i + 1) + ": " + 
                        (isHuman[i] ? "Human" : "Computer"));
                    return;
                }
            }
            
            // Handle control buttons
            if (startBounds.contains(mouse)) {
                List<AbstractPlayer> players = new ArrayList<>();
                for (int i = 0; i < selectedPlayerCount; i++) {
                    if (isHuman[i]) {
                        players.add(new HumanPlayer("Player " + (i + 1), i));
                    } else {
                        players.add(new ComputerPlayer("Computer " + (i + 1), i));
                    }
                }
                game.setState(new PlayState(game, players));
                return;
            }
            
            if (backBounds.contains(mouse)) {
                game.setState(new MenuState(game));
                return;
            }
        }
    }

    @Override
    public void tick() {
        // No animation updates needed
    }

    @Override
    public void onEnter() {
        // Reset button states
        for (ModernButton btn : playerCountButtons) {
            btn.setHovered(false);
            btn.setPressed(false);
        }
        for (ModernButton btn : playerTypeButtons) {
            btn.setHovered(false);
            btn.setPressed(false);
        }
        startButton.setHovered(false);
        startButton.setPressed(false);
        backButton.setHovered(false);
        backButton.setPressed(false);
    }

    @Override
    public void onExit() {
        // Nothing special needed
    }
}
