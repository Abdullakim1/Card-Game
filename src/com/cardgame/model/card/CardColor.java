package com.cardgame.model.card;

import java.awt.Color;

public enum CardColor {
    RED("Skip opponent's turn", "SKIP"),
    BLUE("Draw two cards", "DRAW2"),
    GREEN("Reverse card order", "REVERSE"),
    GOLD("Wild card - can match any color", "WILD");

    private final String power;
    private final String shortName;

    CardColor(String power, String shortName) {
        this.power = power;
        this.shortName = shortName;
    }

    public String getPower() {
        return power;
    }

    public String getShortName() {
        return shortName;
    }

    public Color getDisplayColor() {
        return switch (this) {
            case RED -> new Color(220, 53, 69);    // Bootstrap red
            case BLUE -> new Color(0, 123, 255);   // Bootstrap blue
            case GREEN -> new Color(40, 167, 69);  // Bootstrap green
            case GOLD -> new Color(255, 193, 7);   // Bootstrap yellow
        };
    }

    public boolean matches(CardColor other) {
        return this == other || this == GOLD || other == GOLD;
    }
}
