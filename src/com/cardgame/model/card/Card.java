package com.cardgame.model.card;

import java.awt.*;

public class Card {
    public enum CardColor {
        RED(new Color(220, 53, 69)),
        BLUE(new Color(0, 123, 255)),
        GREEN(new Color(40, 167, 69)),
        GOLD(new Color(255, 193, 7));

        private final Color awtColor;

        CardColor(Color awtColor) {
            this.awtColor = awtColor;
        }

        public Color getAwtColor() {
            return awtColor;
        }
    }

    private CardColor color;
    private int value;
    private boolean faceUp;
    private boolean highlighted;

    // Method overloading for constructors
    public Card(CardColor color, int value) {
        this(color, value, false);
    }

    public Card(CardColor color, int value, boolean faceUp) {
        this.color = color;
        this.value = value;
        this.faceUp = faceUp;
        this.highlighted = false;
    }

    // Method overloading for card matching
    public boolean matches(Card other) {
        if (other == null) return false;
        // Wild cards match with anything
        if (this.color == CardColor.GOLD || other.color == CardColor.GOLD) return true;
        return color == other.color || value == other.value;
    }

    public boolean matches(CardColor color) {
        return this.color == color;
    }

    public boolean matches(int value) {
        return this.value == value;
    }

    public CardColor getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    public boolean isSpecial() {
        return value < 0;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public void render(Graphics g, int x, int y, int width, int height) {
        // Draw card background
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw shadow if highlighted
        if (highlighted) {
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.fillRoundRect(x - 2, y - 2, width + 4, height + 4, 15, 15);
        }

        // Draw card face or back
        if (faceUp) {
            // Card face
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(x, y, width, height, 10, 10);
            
            // Card border
            g2d.setColor(color.getAwtColor());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, width, height, 10, 10);

            // Draw card content
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String display = isSpecial() ? getSpecialText() : String.valueOf(value);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (width - fm.stringWidth(display)) / 2;
            int textY = y + (height + fm.getAscent() - fm.getDescent()) / 2;

            // Draw text shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.drawString(display, textX + 1, textY + 1);

            // Draw text
            g2d.setColor(color.getAwtColor());
            g2d.drawString(display, textX, textY);
        } else {
            // Card back
            g2d.setColor(new Color(30, 34, 42));
            g2d.fillRoundRect(x, y, width, height, 10, 10);
            
            // Pattern on back
            g2d.setColor(new Color(40, 44, 52));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x + 10, y + 10, width - 20, height - 20, 5, 5);
        }
    }

    private String getSpecialText() {
        return switch (color) {
            case RED -> "SKIP";
            case BLUE -> "DRAW";
            case GREEN -> "REV";
            case GOLD -> "WILD";
        };
    }
}
