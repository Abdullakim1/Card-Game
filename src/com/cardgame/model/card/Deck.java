package com.cardgame.model.card;

import java.util.*;

public class Deck {
    private List<Card> cards;
    private List<Card> discardPile;
    private Random random;

    public Deck() {
        random = new Random();
        cards = new ArrayList<>();
        discardPile = new ArrayList<>();
        initializeDeck();
        shuffle();
    }

    private void initializeDeck() {
        // Add number cards (0-9) for each color
        for (Card.CardColor color : Card.CardColor.values()) {
            if (color != Card.CardColor.GOLD) {  // Skip GOLD for number cards
                for (int number = 0; number <= 9; number++) {
                    cards.add(new Card(color, number));
                    if (number != 0) {  // Add duplicates of non-zero numbers
                        cards.add(new Card(color, number));
                    }
                }
            }
        }

        // Add special cards
        // RED - Skip turn
        for (int i = 0; i < 2; i++) {
            cards.add(new Card(Card.CardColor.RED, -1));
        }
        // BLUE - Draw two
        for (int i = 0; i < 2; i++) {
            cards.add(new Card(Card.CardColor.BLUE, -1));
        }
        // GREEN - Reverse
        for (int i = 0; i < 2; i++) {
            cards.add(new Card(Card.CardColor.GREEN, -1));
        }
        // GOLD - Wild
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Card.CardColor.GOLD, -1));
        }
    }

    public void shuffle() {
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Card temp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, temp);
        }
    }

    public Card draw() {
        if (cards.isEmpty()) {
            recycleDiscardPile();
            if (cards.isEmpty()) {
                return null;  // No cards left even after recycling
            }
        }
        return cards.remove(cards.size() - 1);
    }

    public List<Card> draw(int count) {
        List<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Card card = draw();
            if (card != null) {
                drawnCards.add(card);
            } else {
                break;  // No more cards to draw
            }
        }
        return drawnCards;
    }

    public void discard(Card card) {
        if (card != null) {
            discardPile.add(card);
        }
    }

    private void recycleDiscardPile() {
        if (discardPile.isEmpty()) {
            return;
        }
        
        // Keep the top card in the discard pile
        Card topCard = discardPile.remove(discardPile.size() - 1);
        
        // Add all other cards back to the deck
        cards.addAll(discardPile);
        discardPile.clear();
        
        // Shuffle the recycled cards
        shuffle();
        
        // Put the top card back in the discard pile
        discardPile.add(topCard);
    }

    public int remainingCards() {
        return cards.size();
    }

    public int discardSize() {
        return discardPile.size();
    }
}
