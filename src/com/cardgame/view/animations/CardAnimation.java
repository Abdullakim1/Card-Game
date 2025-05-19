package com.cardgame.view.animations;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CardAnimation {
    private BufferedImage cardImage;
    private float x, y;
    private float targetX, targetY;
    private float speed = 0.1f;
    private boolean isAnimating = false;
    private float alpha = 1.0f;
    private float scale = 1.0f;

    public CardAnimation() {}

    public void setCard(BufferedImage cardImage) {
        this.cardImage = cardImage;
    }

    public void startAnimation(float startX, float startY, float endX, float endY) {
        this.x = startX;
        this.y = startY;
        this.targetX = endX;
        this.targetY = endY;
        this.isAnimating = true;
        this.alpha = 1.0f;
        this.scale = 1.0f;
    }

    public void update() {
        if (!isAnimating) return;

        // Update position
        float dx = targetX - x;
        float dy = targetY - y;
        x += dx * speed;
        y += dy * speed;

        // Check if animation is complete
        if (Math.abs(dx) < 0.1f && Math.abs(dy) < 0.1f) {
            isAnimating = false;
        }
    }

    public void render(Graphics2D g2d) {
        if (cardImage == null || !isAnimating) return;

        // Save original composite
        Composite originalComposite = g2d.getComposite();

        // Apply alpha transparency
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Calculate scaled dimensions
        int width = (int)(cardImage.getWidth() * scale);
        int height = (int)(cardImage.getHeight() * scale);

        // Draw the card
        g2d.drawImage(cardImage, (int)x, (int)y, width, height, null);

        // Restore original composite
        g2d.setComposite(originalComposite);
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
