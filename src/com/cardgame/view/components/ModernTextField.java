package com.cardgame.view.components;

import java.awt.*;

public class ModernTextField {
    private String text;
    private String placeholder;
    private boolean focused;
    private static final Color BACKGROUND_COLOR = new Color(30, 34, 42);
    private static final Color BORDER_COLOR = new Color(61, 90, 128);
    private static final Color FOCUSED_BORDER_COLOR = new Color(100, 149, 237);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color PLACEHOLDER_COLOR = new Color(128, 128, 128);

    public ModernTextField(String placeholder) {
        this.text = "";
        this.placeholder = placeholder;
        this.focused = false;
    }

    public void render(Graphics g, Rectangle bounds) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);

        // Draw border
        g2d.setColor(focused ? FOCUSED_BORDER_COLOR : BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);

        // Draw text
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int textY = bounds.y + (bounds.height - fm.getHeight()) / 2 + fm.getAscent();
        
        if (text.isEmpty()) {
            g2d.setColor(PLACEHOLDER_COLOR);
            g2d.drawString(placeholder, bounds.x + 10, textY);
        } else {
            g2d.setColor(TEXT_COLOR);
            g2d.drawString(text, bounds.x + 10, textY);
        }

        // Draw cursor if focused
        if (focused && text.length() < 20) {
            int cursorX = bounds.x + 10 + (text.isEmpty() ? 0 : fm.stringWidth(text));
            g2d.setColor(TEXT_COLOR);
            g2d.drawLine(cursorX, bounds.y + 10, cursorX, bounds.y + bounds.height - 10);
        }
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPlaceholder() {
        return placeholder;
    }
}
