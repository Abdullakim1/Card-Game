package com.cardgame.model;

import java.awt.Graphics;

public interface Drawable {
    
    void render(Graphics g, int x, int y, int width, int height);
}
