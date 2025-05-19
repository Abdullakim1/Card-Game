package com.cardgame.model.player;

import com.cardgame.model.card.Card;
import com.cardgame.model.card.Card.CardColor;
import java.util.*;

public class Player {
    private String name;
    private List<Card> hand;
    private boolean isComputer;

    public Player(String name, boolean isComputer) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.isComputer = isComputer;
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

    public boolean isComputer() {
        return isComputer;
    }

    public String getName() {
        return name;
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
