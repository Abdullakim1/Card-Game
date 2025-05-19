package com.cardgame.model.player.types;

import com.cardgame.model.card.Card;
import com.cardgame.model.card.Card.CardColor;
import java.util.*;

public abstract class AbstractPlayer {
    protected String name;
    protected List<Card> hand;
    protected PlayerType type;
    protected int position;  // Player's position at the table (0-3)

    protected AbstractPlayer(String name, PlayerType type, int position) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.type = type;
        this.position = position;
    }

    public void addCard(Card card) {
        if (card != null) {
            hand.add(card);
        }
    }

    public void addCards(List<Card> cards) {
        if (cards != null) {
            hand.addAll(cards);
        }
    }

    public Card playCard(int index) {
        if (index >= 0 && index < hand.size()) {
            return hand.remove(index);
        }
        return null;
    }

    public List<Card> getHand() {
        return new ArrayList<>(hand);
    }

    public int handSize() {
        return hand.size();
    }

    public String getName() {
        return name;
    }

    public PlayerType getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public boolean hasPlayableCard(Card topCard) {
        for (Card card : hand) {
            if (card.matches(topCard)) {
                return true;
            }
        }
        return false;
    }

    public Map<CardColor, Integer> getColorCounts() {
        Map<CardColor, Integer> colorCounts = new EnumMap<>(CardColor.class);
        for (Card card : hand) {
            CardColor color = card.getColor();
            colorCounts.put(color, colorCounts.getOrDefault(color, 0) + 1);
        }
        return colorCounts;
    }
}
