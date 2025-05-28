package com.cardgame.model.card.effect;

import com.cardgame.model.card.Card;
import com.cardgame.model.card.Card.CardColor;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating card effects based on card properties
 * Demonstrates the Factory pattern for extensibility
 */
public class CardEffectFactory {
    // Information hiding: private fields (step 1)
    // Class variable (static) (step 2)
    // Variable, not constant (step 3)
    private static final Map<CardColor, Class<? extends CardEffect>> colorEffectMap = new HashMap<>();
    
    // Static initializer to set up the default mappings
    static {
        colorEffectMap.put(CardColor.RED, SkipTurnEffect.class);
        colorEffectMap.put(CardColor.BLUE, DrawCardEffect.class);
        colorEffectMap.put(CardColor.GREEN, ReverseDirectionEffect.class);
        colorEffectMap.put(CardColor.GOLD, WildCardEffect.class);
    }
    
    /**
     * Registers a new effect for a card color
     * This demonstrates extensibility by allowing new effects to be added
     * without modifying existing code
     * 
     * @param color The card color
     * @param effectClass The effect class to associate with this color
     */
    public static void registerEffect(CardColor color, Class<? extends CardEffect> effectClass) {
        colorEffectMap.put(color, effectClass);
    }
    
    /**
     * Creates an effect for a special card
     * @param card The card to create an effect for
     * @return The created effect, or null if the card is not special
     */
    public static CardEffect createEffect(Card card) {
        // Only special cards have effects
        if (!card.isSpecial()) {
            return null;
        }
        
        try {
            // Get the effect class for this card color
            Class<? extends CardEffect> effectClass = colorEffectMap.get(card.getColor());
            if (effectClass == null) {
                return null;
            }
            
            // Special case for draw cards
            if (effectClass == DrawCardEffect.class) {
                return new DrawCardEffect(2); // Draw 2 cards
            }
            
            // Create a new instance of the effect
            return effectClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // If there's an error creating the effect, return null
            e.printStackTrace();
            return null;
        }
    }
}
