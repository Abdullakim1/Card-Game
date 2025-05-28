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
    
    /**
     * Gets the player's cards
     * @return The list of cards in the player's hand
     */
    public List<Card> getCards() {
        return getHand();
    }
    
    /**
     * Draws a card from the deck and adds it to the player's hand
     * @param deck The deck to draw from
     * @return The card that was drawn, or null if the deck is empty
     */
    public Card drawCard(Deck deck) {
        if (deck != null) {
            Card card = deck.draw();
            if (card != null) {
                hand.add(card);
            }
            return card;
        }
        return null;
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
