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
        int buttonWidth = 200;
        int buttonHeight = 50;
        int spacing = 60;
        
        playBounds = new Rectangle(0, 0, buttonWidth, buttonHeight);
        humanPlayBounds = new Rectangle(0, 0, buttonWidth, buttonHeight);
        rulesBounds = new Rectangle(0, 0, buttonWidth, buttonHeight);
        exitBounds = new Rectangle(0, 0, buttonWidth, buttonHeight);

        playButton = new ModernButton("Play vs Computer");
        humanPlayButton = new ModernButton("Play with Humans");
        rulesButton = new ModernButton("Rules");
        exitButton = new ModernButton("Exit");
    }

    @Override
    public void tick() {
    }

    @Override
    public void render(Graphics g) {
        int windowWidth = getGame().getWidth();
        int windowHeight = getGame().getHeight();
        
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(40, 44, 52),
            0, windowHeight, new Color(24, 26, 31)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, windowWidth, windowHeight);

        g.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "Card Game";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (windowWidth - fm.stringWidth(title)) / 2;
        int titleY = windowHeight / 4;

        g.setColor(new Color(0, 0, 0, 100));
        g.drawString(title, titleX + 2, titleY + 2);

        g.setColor(Color.WHITE);
        g.drawString(title, titleX, titleY);

        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(titleX, titleY + 10, titleX + fm.stringWidth(title), titleY + 10);

        int buttonWidth = 200;
        int buttonHeight = 50;
        int startY = windowHeight / 3 + 50;
        int spacing = 60;
        int centerX = (windowWidth - buttonWidth) / 2;
        
        playBounds.setBounds(centerX, startY, buttonWidth, buttonHeight);
        humanPlayBounds.setBounds(centerX, startY + spacing, buttonWidth, buttonHeight);
        rulesBounds.setBounds(centerX, startY + spacing * 2, buttonWidth, buttonHeight);
        exitBounds.setBounds(centerX, startY + spacing * 3, buttonWidth, buttonHeight);
        
        playButton.render(g, playBounds.x, playBounds.y, playBounds.width, playBounds.height);
        humanPlayButton.render(g, humanPlayBounds.x, humanPlayBounds.y, humanPlayBounds.width, humanPlayBounds.height);
        rulesButton.render(g, rulesBounds.x, rulesBounds.y, rulesBounds.width, rulesBounds.height);
        exitButton.render(g, exitBounds.x, exitBounds.y, exitBounds.width, exitBounds.height);

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(new Color(200, 200, 200));
        String version = "Version 1.0";
        g.drawString(version, 10, windowHeight - 20);
    }

    @Override
    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();

        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            playButton.setHovered(playBounds.contains(mouse));
            humanPlayButton.setHovered(humanPlayBounds.contains(mouse));
            rulesButton.setHovered(rulesBounds.contains(mouse));
            exitButton.setHovered(exitBounds.contains(mouse));
            return;
        }

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

        if (e.getID() == MouseEvent.MOUSE_RELEASED || e.getID() == MouseEvent.MOUSE_CLICKED) {
            playButton.setPressed(false);
            humanPlayButton.setPressed(false);
            rulesButton.setPressed(false);
            exitButton.setPressed(false);

            if (playBounds.contains(mouse)) {
                getGame().setState(new SinglePlayerNameState(getGame()));
            } else if (humanPlayBounds.contains(mouse)) {
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
    }
}
