package com.cardgame.model.game;

import java.awt.image.BufferedImage;

/**
 * Interface for punishment animations shown when a player loses the game
 */
public interface PunishmentAnimation {
    /**
     * Renders a single frame of the animation
     * 
     * @param frame The current frame number
     * @return A BufferedImage containing the rendered frame
     */
    BufferedImage renderFrame(int frame);

    /**
     * Gets the description of the punishment
     * 
     * @return A string describing the punishment
     */
    String getDescription();
}
