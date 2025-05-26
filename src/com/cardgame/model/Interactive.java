package com.cardgame.model;

import java.awt.event.MouseEvent;

/**
 * Interface for objects that can interact with user input
 */
public interface Interactive {
    /**
     * Handles mouse events
     * @param e Mouse event
     * @return true if the event was handled, false otherwise
     */
    boolean handleMouseEvent(MouseEvent e);
    
    /**
     * Checks if the point is inside this interactive element
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if the point is inside, false otherwise
     */
    boolean contains(int x, int y);
}
