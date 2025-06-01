package com.cardgame.model;

import java.awt.event.MouseEvent;


public interface Interactive {
    
    boolean handleMouseEvent(MouseEvent e);
    
    boolean contains(int x, int y);
}
