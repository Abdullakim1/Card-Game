package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.model.player.types.AbstractPlayer;
import com.cardgame.model.player.types.HumanPlayer;
import com.cardgame.model.player.types.ComputerPlayer;
import com.cardgame.view.components.ModernButton;
import com.cardgame.view.components.ModernTextField;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PlayerSetupState extends GameState {
    private ModernButton startButton;
    private ModernButton backButton;
    private Rectangle startBounds;
    private Rectangle backBounds;
    private List<ModernTextField> nameFields;
    private List<Rectangle> nameFieldBounds;
    private boolean isVsComputer;
    private String errorMessage;
    private int activeField;

    public PlayerSetupState(Game game, boolean isVsComputer) {
        super(game);
        this.isVsComputer = isVsComputer;
        this.nameFields = new ArrayList<>();
        this.nameFieldBounds = new ArrayList<>();
        this.errorMessage = "";
        this.activeField = -1;
        initializeComponents();
    }

    private void initializeComponents() {
        int buttonWidth = 200;
        int buttonHeight = 50;
        int fieldWidth = 300;
        int fieldHeight = 40;
        int spacing = 20;
        int startY = 200;
        int centerX = (800 - buttonWidth) / 2;

        // Create text fields for player names
        if (isVsComputer) {
            nameFields.add(new ModernTextField("Enter your name"));
            nameFieldBounds.add(new Rectangle((800 - fieldWidth) / 2, startY, fieldWidth, fieldHeight));
        } else {
            nameFields.add(new ModernTextField("Enter Player 1 name"));
            nameFields.add(new ModernTextField("Enter Player 2 name"));
            nameFieldBounds.add(new Rectangle((800 - fieldWidth) / 2, startY, fieldWidth, fieldHeight));
            nameFieldBounds.add(new Rectangle((800 - fieldWidth) / 2, startY + fieldHeight + spacing, fieldWidth, fieldHeight));
        }

        // Create buttons
        startButton = new ModernButton("Start Game");
        backButton = new ModernButton("Back");
        
        int buttonsY = startY + (isVsComputer ? fieldHeight + spacing : 2 * fieldHeight + 2 * spacing);
        startBounds = new Rectangle(centerX, buttonsY, buttonWidth, buttonHeight);
        backBounds = new Rectangle(centerX, buttonsY + buttonHeight + spacing, buttonWidth, buttonHeight);
    }

    @Override
    public void render(Graphics g) {
        // Draw background
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(40, 44, 52),
            0, 600, new Color(24, 26, 31)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 800, 600);

        // Draw title
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        String title = isVsComputer ? "Player Setup" : "Two Player Setup";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (800 - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 120);

        // Draw text fields
        for (int i = 0; i < nameFields.size(); i++) {
            nameFields.get(i).render(g, nameFieldBounds.get(i));
        }

        // Draw buttons
        startButton.render(g, startBounds.x, startBounds.y, startBounds.width, startBounds.height);
        backButton.render(g, backBounds.x, backBounds.y, backBounds.width, backBounds.height);

        // Draw error message if any
        if (!errorMessage.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.setColor(Color.RED);
            g.drawString(errorMessage, (800 - fm.stringWidth(errorMessage)) / 2, startBounds.y - 20);
        }
    }

    @Override
    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();

        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            startButton.setHovered(startBounds.contains(mouse));
            backButton.setHovered(backBounds.contains(mouse));
            return;
        }

        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            // Handle text field focus
            activeField = -1;
            for (int i = 0; i < nameFieldBounds.size(); i++) {
                if (nameFieldBounds.get(i).contains(mouse)) {
                    activeField = i;
                    nameFields.get(i).setFocused(true);
                } else {
                    nameFields.get(i).setFocused(false);
                }
            }

            // Handle button presses
            if (startBounds.contains(mouse)) {
                startButton.setPressed(true);
            } else if (backBounds.contains(mouse)) {
                backButton.setPressed(true);
            }
            return;
        }

        if (e.getID() == MouseEvent.MOUSE_RELEASED) {
            startButton.setPressed(false);
            backButton.setPressed(false);

            if (startBounds.contains(mouse)) {
                startGame();
            } else if (backBounds.contains(mouse)) {
                game.setState(new MenuState(game));
            }
        }
    }

    public void handleKeyEvent(KeyEvent e) {
        if (activeField != -1 && activeField < nameFields.size()) {
            ModernTextField activeTextField = nameFields.get(activeField);
            
            if (e.getID() == KeyEvent.KEY_TYPED) {
                char c = e.getKeyChar();
                if (c == '\b') {
                    // Handle backspace
                    String text = activeTextField.getText();
                    if (!text.isEmpty() && !text.equals(activeTextField.getPlaceholder())) {
                        activeTextField.setText(text.substring(0, text.length() - 1));
                    }
                } else if (Character.isLetterOrDigit(c) || c == ' ') {
                    // Handle regular character input
                    String currentText = activeTextField.getText();
                    if (currentText.equals(activeTextField.getPlaceholder())) {
                        activeTextField.setText(String.valueOf(c));
                    } else if (currentText.length() < 20) { // Limit name length
                        activeTextField.setText(currentText + c);
                    }
                }
            }
        }
    }

    private void startGame() {
        // Validate names
        for (ModernTextField field : nameFields) {
            String name = field.getText();
            if (name.isEmpty() || name.equals(field.getPlaceholder())) {
                errorMessage = "Please enter names for all players";
                return;
            }
        }

        // Create players with custom names
        List<AbstractPlayer> players = new ArrayList<>();
        if (isVsComputer) {
            players.add(new HumanPlayer(nameFields.get(0).getText(), 0));
            players.add(new ComputerPlayer("Computer", 1));
        } else {
            players.add(new HumanPlayer(nameFields.get(0).getText(), 0));
            players.add(new HumanPlayer(nameFields.get(1).getText(), 1));
        }

        // Start the game with custom players
        game.setState(new PlayState(game, players));
    }

    @Override
    public void tick() {
        // Nothing to update continuously
    }

    @Override
    public void onEnter() {
        // Reset state
        errorMessage = "";
        activeField = -1;
        for (ModernTextField field : nameFields) {
            field.setFocused(false);
        }
    }

    @Override
    public void onExit() {
        // Nothing special needed
    }
}
