package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.view.components.ModernButton;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class RulesState extends GameState {
    private ModernButton backButton;
    private Rectangle backBounds;
    private ModernButton scrollUpButton;
    private ModernButton scrollDownButton;
    private Rectangle scrollUpBounds;
    private Rectangle scrollDownBounds;
    private int scrollOffset = 0;
    private static final int SCROLL_SPEED = 20;
    private static final int MAX_SCROLL = 400; // Will be adjusted based on content
    private final String[] rules = {
        "Card Game Rules",
        "",
        "1. Setup:",
        "   • Each player starts with 7 cards",
        "   • First card from deck is placed face up",
        "   • Players must match color or number",
        "",
        "2. Special Cards:",
        "   • RED   - Skip opponent's turn",
        "   • BLUE  - Opponent draws two cards",
        "   • GREEN - Reverse (your turn again)",
        "   • GOLD  - Wild card (can play on any color)",
        "",
        "3. Your Turn:",
        "   • Play a matching card (same color or number)",
        "   • Play a special card if it matches color",
        "   • Play a GOLD wild card anytime",
        "   • If you can't play, draw a card",
        "     - If drawn card matches, you can play it",
        "     - If not, your turn ends",
        "",
        "4. Winning:",
        "   • First player to play all cards wins!",
        "   • Plan your moves carefully",
        "   • Use special cards strategically",
        "",
        "5. Strategy Tips:",
        "   • Save special cards for critical moments",
        "   • Keep track of opponent's cards",
        "   • Try to keep same-colored cards together",
        "   • Use GOLD cards wisely"
    };

    public RulesState(Game game) {
        super(game);
        initializeComponents();
    }

    private void initializeComponents() {
        int buttonWidth = 150;
        int buttonHeight = 40;
        int centerX = 50;
        backBounds = new Rectangle(centerX, 500, buttonWidth, buttonHeight);
        backButton = new ModernButton("Back to Menu");

        // Add scroll buttons
        scrollUpButton = new ModernButton("▲");
        scrollDownButton = new ModernButton("▼");
        scrollUpBounds = new Rectangle(740, 20, 30, 30);
        scrollDownBounds = new Rectangle(740, 520, 30, 30);
    }

    @Override
    public void tick() {
        // No continuous updates needed for rules
    }

    @Override
    public void render(Graphics g) {
        // Draw background with gradient
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(40, 44, 52),
            0, 600, new Color(24, 26, 31)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 800, 600);

        // Create clipping region for scrolling
        Shape oldClip = g2d.getClip();
        g2d.setClip(30, 20, 740, 560);

        // Draw semi-transparent overlay for better readability
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.fillRect(30, 20, 740, 560);
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.drawRect(30, 20, 740, 560);

        // Enable antialiasing for smoother text
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw title with shadow (fixed position, outside scroll area)
        g2d.setClip(oldClip);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String title = rules[0];
        FontMetrics fm = g.getFontMetrics();
        int titleX = (800 - fm.stringWidth(title)) / 2;
        
        // Draw shadow
        g.setColor(new Color(0, 0, 0, 128));
        g.drawString(title, titleX + 2, 62);
        
        // Draw title
        g.setColor(Color.WHITE);
        g.drawString(title, titleX, 60);

        // Restore clip for scrolling content
        g2d.setClip(30, 20, 740, 560);

        // Draw rules text with different colors for special cards
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        int y = 100 - scrollOffset; // Apply scroll offset
        int leftMargin = 50;
        
        for (int i = 1; i < rules.length; i++) {
            String line = rules[i];
            
            // Add extra spacing before each section
            if (line.startsWith("1.") || line.startsWith("2.") || 
                line.startsWith("3.") || line.startsWith("4.") || 
                line.startsWith("5.")) {
                y += 10;
            }
            
            // Only draw if within visible area
            if (y >= 20 && y <= 580) {
                // Color special card rules and add visual emphasis
                if (line.contains("RED")) {
                    g.setColor(new Color(220, 53, 69));
                    g2d.setFont(g.getFont().deriveFont(Font.BOLD));
                } else if (line.contains("BLUE")) {
                    g.setColor(new Color(0, 123, 255));
                    g2d.setFont(g.getFont().deriveFont(Font.BOLD));
                } else if (line.contains("GREEN")) {
                    g.setColor(new Color(40, 167, 69));
                    g2d.setFont(g.getFont().deriveFont(Font.BOLD));
                } else if (line.contains("GOLD")) {
                    g.setColor(new Color(255, 193, 7));
                    g2d.setFont(g.getFont().deriveFont(Font.BOLD));
                } else {
                    g.setColor(Color.WHITE);
                    g2d.setFont(g.getFont().deriveFont(Font.PLAIN));
                }
                
                // Draw text shadow for better readability
                if (!line.trim().isEmpty()) {
                    g.setColor(new Color(0, 0, 0, 100));
                    g.drawString(line, leftMargin + 1, y + 1);
                    
                    // Restore color and draw main text
                    if (line.contains("RED")) {
                        g.setColor(new Color(220, 53, 69));
                    } else if (line.contains("BLUE")) {
                        g.setColor(new Color(0, 123, 255));
                    } else if (line.contains("GREEN")) {
                        g.setColor(new Color(40, 167, 69));
                    } else if (line.contains("GOLD")) {
                        g.setColor(new Color(255, 193, 7));
                    } else {
                        g.setColor(Color.WHITE);
                    }
                }
                
                g.drawString(line, leftMargin, y);
            }
            y += line.isEmpty() ? 10 : 22; // Add more space between sections
        }

        // Restore original clip
        g2d.setClip(oldClip);

        // Draw scroll buttons only if they exist
        if (scrollUpButton != null && scrollDownButton != null) {
            scrollUpButton.render(g, scrollUpBounds.x, scrollUpBounds.y, scrollUpBounds.width, scrollUpBounds.height);
            scrollDownButton.render(g, scrollDownBounds.x, scrollDownBounds.y, scrollDownBounds.width, scrollDownBounds.height);
        }

        // Draw back button with shadow effect if it exists
        if (backButton != null) {
            backButton.render(g, backBounds.x, backBounds.y, backBounds.width, backBounds.height);
        }
    }

    @Override
    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();

        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            if (backButton != null) backButton.setHovered(backBounds.contains(mouse));
            if (scrollUpButton != null) scrollUpButton.setHovered(scrollUpBounds.contains(mouse));
            if (scrollDownButton != null) scrollDownButton.setHovered(scrollDownBounds.contains(mouse));
            return;
        }

        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            if (backBounds.contains(mouse)) {
                if (backButton != null) backButton.setPressed(true);
            } else if (scrollUpButton != null && scrollUpBounds.contains(mouse)) {
                scrollUpButton.setPressed(true);
                scrollOffset = Math.max(0, scrollOffset - SCROLL_SPEED);
            } else if (scrollDownButton != null && scrollDownBounds.contains(mouse)) {
                scrollDownButton.setPressed(true);
                scrollOffset = Math.min(MAX_SCROLL, scrollOffset + SCROLL_SPEED);
            }
            return;
        }

        if (e.getID() == MouseEvent.MOUSE_RELEASED) {
            if (backButton != null) backButton.setPressed(false);
            if (scrollUpButton != null) scrollUpButton.setPressed(false);
            if (scrollDownButton != null) scrollDownButton.setPressed(false);
            
            if (backBounds.contains(mouse)) {
                // Create the new state before cleaning up the current one
                MenuState nextState = new MenuState(getGame());
                getGame().setState(nextState);
            }
            return;
        }

        // Handle mouse wheel scrolling
        if (e.getID() == MouseEvent.MOUSE_WHEEL && scrollUpButton != null && scrollDownButton != null) {
            MouseWheelEvent wheelEvent = (MouseWheelEvent) e;
            scrollOffset += wheelEvent.getWheelRotation() * SCROLL_SPEED;
            scrollOffset = Math.max(0, Math.min(MAX_SCROLL, scrollOffset));
        }
    }

    @Override
    public void onEnter() {
        // Initialize components if they're null
        if (backButton == null) {
            initializeComponents();
        }
    }

    @Override
    public void onExit() {
        // Clean up resources after the last render
        getGame().runLater(() -> {
            backButton = null;
            scrollUpButton = null;
            scrollDownButton = null;
        });
    }
}
