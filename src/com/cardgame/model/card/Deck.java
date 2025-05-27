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
        for (Card.CardColor color : Card.CardColor.values()) {
            if (color != Card.CardColor.GOLD) {  
                for (int number = 0; number <= 9; number++) {
                    cards.add(new Card(color, number));
                    if (number != 0) {  
                        cards.add(new Card(color, number));
                    }
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            cards.add(new Card(Card.CardColor.RED, -1));
        }
        for (int i = 0; i < 2; i++) {
            cards.add(new Card(Card.CardColor.BLUE, -1));
        }
        for (int i = 0; i < 2; i++) {
            cards.add(new Card(Card.CardColor.GREEN, -1));
        }
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
                return null;  
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
                break;  
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
        
        Card topCard = discardPile.remove(discardPile.size() - 1);
        
        cards.addAll(discardPile);
        discardPile.clear();
        
        shuffle();
        
        discardPile.add(topCard);
    }

    public int remainingCards() {
        return cards.size();
    }

    public int discardSize() {
        return discardPile.size();
    }
}
