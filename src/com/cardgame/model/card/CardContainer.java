package com.cardgame.model.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class CardContainer<T extends Card> {
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
        return Collections.unmodifiableList(cards);
    }
    
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
