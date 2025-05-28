package com.cardgame.model.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Generic container for cards demonstrating parametric polymorphism
 * @param <T> Type of card to be stored in this container
 */
public class CardContainer<T extends Card> {
    // Information hiding: private field (step 1)
    // Instance variable (step 2)
    // Variable, not constant (step 3)
    private final List<T> cards;
    
    public CardContainer() {
        this.cards = new ArrayList<>();
    }
    
    public void add(T card) {
        cards.add(card);
    }
    
    public T get(int index) {
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return cards.get(index);
    }
    
    public T remove(int index) {
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return cards.remove(index);
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    public int size() {
        return cards.size();
    }
    
    public boolean isEmpty() {
        return cards.isEmpty();
    }
    
    public List<T> getCards() {
        // Return an unmodifiable view to maintain encapsulation
        return Collections.unmodifiableList(cards);
    }
    
    // Demonstrates functional programming with generics
    public List<T> filter(Predicate<T> condition) {
        List<T> result = new ArrayList<>();
        for (T card : cards) {
            if (condition.test(card)) {
                result.add(card);
            }
        }
        return result;
    }
}
