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

    @Override
    public void paintComponent(Graphics g) {
        paintComponentImpl(g);
    }
    
    private void paintComponentImpl(Graphics g) {
        render(g, 0, 0, getWidth(), getHeight());
    }

    public void render(Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D.Float shape = new RoundRectangle2D.Float(x, y, width, height, cornerRadius, cornerRadius);

        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fill(new RoundRectangle2D.Float(x + 2, y + 2, width, height, cornerRadius, cornerRadius));

        Color buttonColor;
        if (pressed) {
            buttonColor = pressedColor;
        } else if (hovered) {
            buttonColor = hoverColor;
        } else {
            buttonColor = normalColor;
        }

        g2d.setColor(buttonColor);
        g2d.fill(shape);

        g2d.setColor(buttonColor.darker());
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(shape);

        g2d.setColor(textColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);

        if (hovered && !pressed) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.setColor(Color.WHITE);
            g2d.fill(shape);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
}