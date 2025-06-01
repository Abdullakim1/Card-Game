package com.cardgame.model.game;

import java.awt.image.BufferedImage;


public interface PunishmentAnimation {
    
    BufferedImage renderFrame(int frame);
    String getDescription();
}
