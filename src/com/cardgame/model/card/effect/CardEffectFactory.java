package com.cardgame.model.card.effect;

import com.cardgame.model.card.Card;
import com.cardgame.model.card.Card.CardColor;
import java.util.HashMap;
import java.util.Map;


public class CardEffectFactory {
    
    private static final Map<CardColor, Class<? extends CardEffect>> colorEffectMap = new HashMap<>();
    
    static {
        colorEffectMap.put(CardColor.RED, SkipTurnEffect.class);
        colorEffectMap.put(CardColor.BLUE, DrawCardEffect.class);
        colorEffectMap.put(CardColor.GREEN, ReverseDirectionEffect.class);
        colorEffectMap.put(CardColor.GOLD, WildCardEffect.class);
    }
    
    
    public static void registerEffect(CardColor color, Class<? extends CardEffect> effectClass) {
        colorEffectMap.put(color, effectClass);
    }
    
    public static CardEffect createEffect(Card card) {
        if (!card.isSpecial()) {
            return null;
        }
        
        try {
            Class<? extends CardEffect> effectClass = colorEffectMap.get(card.getColor());
            if (effectClass == null) {
                return null;
            }
            
            if (effectClass == DrawCardEffect.class) {
                return new DrawCardEffect(2);
            } else if (effectClass == SkipTurnEffect.class) {
                return new SkipTurnEffect();
            } else if (effectClass == ReverseDirectionEffect.class) {
                return new ReverseDirectionEffect();
            } else if (effectClass == WildCardEffect.class) {
                return new WildCardEffect();
            }
            
            return effectClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
