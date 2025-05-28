package com.cardgame.model;

import java.awt.Graphics;

/**
 * Interface for objects that can be drawn on screen
 */
public interface Drawable {
    /**
     * Renders the object on screen
     * @param g Graphics context
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     */
    void render(Graphics g, int x, int y, int width, int height);
}
