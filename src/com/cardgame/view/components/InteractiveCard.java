package com.cardgame.view.components;

import com.cardgame.model.Drawable;
import com.cardgame.model.Interactive;
import com.cardgame.model.card.Card;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * A card that can be drawn and interacted with
 * This class demonstrates multityping by implementing multiple interfaces
 */
public class InteractiveCard implements Drawable, Interactive {
    // Information hiding: private fields (step 1)
    // Instance variables (step 2)
    // Some constant, some variable (step 3)
    private final Card card;
    private final Rectangle bounds;
    private boolean selected;
    private boolean hovered;
    
    public InteractiveCard(Card card, int x, int y, int width, int height) {
        this.card = card;
        this.bounds = new Rectangle(x, y, width, height);
        this.selected = false;
        this.hovered = false;
    }
    
    @Override
    public void render(Graphics g, int x, int y, int width, int height) {
        // Update bounds if position changed
        bounds.setLocation(x, y);
        bounds.setSize(width, height);
        
        // Set card highlight based on selection/hover state
        card.setHighlighted(selected || hovered);
        
        // Render the card
        card.render(g, x, y, width, height);
    }
    
    @Override
    public boolean handleMouseEvent(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            boolean wasHovered = hovered;
            hovered = contains(x, y);
            return wasHovered != hovered; // Return true if hover state changed
        }
        else if (e.getID() == MouseEvent.MOUSE_CLICKED && contains(x, y)) {
            selected = !selected;
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }
    
    public Card getCard() {
        return card;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
