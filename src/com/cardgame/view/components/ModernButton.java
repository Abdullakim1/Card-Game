package com.cardgame.view.components;

import javax.swing.JComponent;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernButton extends JComponent {
    private String text;
    private boolean hovered;
    private boolean pressed;
    private Color normalColor = new Color(61, 90, 254);
    private Color hoverColor = new Color(82, 107, 254);
    private Color pressedColor = new Color(41, 66, 254);
    private Color textColor = Color.WHITE;
    private int cornerRadius = 10;

    public ModernButton(String text) {
        this.text = text;
        this.hovered = false;
        this.pressed = false;
        setOpaque(false);
    }

    public void setHovered(boolean hovered) {
        if (this.hovered != hovered) {
            this.hovered = hovered;
            repaint();
        }
    }

    public void setPressed(boolean pressed) {
        if (this.pressed != pressed) {
            this.pressed = pressed;
            repaint();
        }
    }

    // We're using private instead of protected to follow information hiding principles
    // However, we still need to override the method from JComponent
    @Override
    public void paintComponent(Graphics g) {
        // Call the private implementation method
        paintComponentImpl(g);
    }
    
    // Private implementation method following information hiding principles
    // Information hiding: private method (step 1)
    // Instance method (step 2)
    // Not applicable for methods: constant or variable (step 3)
    private void paintComponentImpl(Graphics g) {
        render(g, 0, 0, getWidth(), getHeight());
    }

    public void render(Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create button shape
        RoundRectangle2D.Float shape = new RoundRectangle2D.Float(x, y, width, height, cornerRadius, cornerRadius);

        // Draw shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fill(new RoundRectangle2D.Float(x + 2, y + 2, width, height, cornerRadius, cornerRadius));

        // Set button color based on state
        Color buttonColor;
        if (pressed) {
            buttonColor = pressedColor;
        } else if (hovered) {
            buttonColor = hoverColor;
        } else {
            buttonColor = normalColor;
        }

        // Draw button background
        g2d.setColor(buttonColor);
        g2d.fill(shape);

        // Draw button border
        g2d.setColor(buttonColor.darker());
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(shape);

        // Draw text
        g2d.setColor(textColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);

        // Draw highlight effect
        if (hovered && !pressed) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.setColor(Color.WHITE);
            g2d.fill(shape);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
}