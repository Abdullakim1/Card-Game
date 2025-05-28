package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.view.components.ModernButton;
import java.awt.*;
import java.awt.event.MouseEvent;

public class MenuState extends GameState {
    private ModernButton playButton;
    private ModernButton humanPlayButton;
    private ModernButton rulesButton;
    private ModernButton exitButton;
    private Rectangle playBounds;
    private Rectangle humanPlayBounds;
    private Rectangle rulesBounds;
    private Rectangle exitBounds;

    public MenuState(Game game) {
        super(game);
        initializeButtons();
    }

    private void initializeButtons() {
        // Calculate button positions based on window size
        int buttonWidth = 200;
        int buttonHeight = 50;
        int startY = 220;
        int spacing = 60;
        int centerX = (800 - buttonWidth) / 2;

        playBounds = new Rectangle(centerX, startY, buttonWidth, buttonHeight);
        humanPlayBounds = new Rectangle(centerX, startY + spacing, buttonWidth, buttonHeight);
        rulesBounds = new Rectangle(centerX, startY + spacing * 2, buttonWidth, buttonHeight);
        exitBounds = new Rectangle(centerX, startY + spacing * 3, buttonWidth, buttonHeight);

        playButton = new ModernButton("Play vs Computer");
        humanPlayButton = new ModernButton("Play with Humans");
        rulesButton = new ModernButton("Rules");
        exitButton = new ModernButton("Exit");
    }

    @Override
    public void tick() {
        // No continuous updates needed for menu
    }

    @Override
    public void render(Graphics g) {
        // Draw background gradient
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(40, 44, 52),
            0, 600, new Color(24, 26, 31)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 800, 600);

        // Draw title with shadow
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "Card Game";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (800 - fm.stringWidth(title)) / 2;
        int titleY = 150;

        // Draw shadow
        g.setColor(new Color(0, 0, 0, 100));
        g.drawString(title, titleX + 2, titleY + 2);

        // Draw title
        g.setColor(Color.WHITE);
        g.drawString(title, titleX, titleY);

        // Draw decorative line under title
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(titleX, titleY + 10, titleX + fm.stringWidth(title), titleY + 10);

        // Draw buttons with their current bounds
        playButton.render(g, playBounds.x, playBounds.y, playBounds.width, playBounds.height);
        humanPlayButton.render(g, humanPlayBounds.x, humanPlayBounds.y, humanPlayBounds.width, humanPlayBounds.height);
        rulesButton.render(g, rulesBounds.x, rulesBounds.y, rulesBounds.width, rulesBounds.height);
        exitButton.render(g, exitBounds.x, exitBounds.y, exitBounds.width, exitBounds.height);

        // Draw version text
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(new Color(200, 200, 200));
        String version = "Version 1.0";
        g.drawString(version, 10, 580);
    }

    @Override
    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();

        // Handle hover effects
        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            playButton.setHovered(playBounds.contains(mouse));
            humanPlayButton.setHovered(humanPlayBounds.contains(mouse));
            rulesButton.setHovered(rulesBounds.contains(mouse));
            exitButton.setHovered(exitBounds.contains(mouse));
            return;
        }

        // Handle button press effects
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            if (playBounds.contains(mouse)) {
                playButton.setPressed(true);
            } else if (humanPlayBounds.contains(mouse)) {
                humanPlayButton.setPressed(true);
            } else if (rulesBounds.contains(mouse)) {
                rulesButton.setPressed(true);
            } else if (exitBounds.contains(mouse)) {
                exitButton.setPressed(true);
            }
            return;
        }

        // Handle button release and click effects
        if (e.getID() == MouseEvent.MOUSE_RELEASED || e.getID() == MouseEvent.MOUSE_CLICKED) {
            // Reset pressed states
            playButton.setPressed(false);
            humanPlayButton.setPressed(false);
            rulesButton.setPressed(false);
            exitButton.setPressed(false);

            // Handle button actions
            if (playBounds.contains(mouse)) {
                // Redirect to single player name entry for vs Computer mode
                getGame().setState(new SinglePlayerNameState(getGame()));
            } else if (humanPlayBounds.contains(mouse)) {
                // Human-only multiplayer mode
                PlayerSelectionState state = new PlayerSelectionState(getGame());
                state.setIncludeComputer(false);
                getGame().setState(state);
            } else if (rulesBounds.contains(mouse)) {
                getGame().setState(new RulesState(getGame()));
            } else if (exitBounds.contains(mouse)) {
                System.exit(0);
            }
        }
    }

    @Override
    public void onEnter() {
        // Reset button states when entering menu
        playButton.setHovered(false);
        playButton.setPressed(false);
        humanPlayButton.setHovered(false);
        humanPlayButton.setPressed(false);
        rulesButton.setHovered(false);
        rulesButton.setPressed(false);
        exitButton.setHovered(false);
        exitButton.setPressed(false);
    }

    @Override
    public void onExit() {
        // Nothing special needed when exiting menu
    }
}
