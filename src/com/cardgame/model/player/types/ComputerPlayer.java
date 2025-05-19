package com.cardgame.model.player.types;

import com.cardgame.model.card.Card;
import java.util.List;

public class ComputerPlayer extends AbstractPlayer {
    public ComputerPlayer(String name, int position) {
        super(name, PlayerType.COMPUTER, position);
    }

    public int selectBestMove(Card topCard) {
        List<Card> computerHand = getHand();
        int bestIndex = -1;

        // First priority: Win the game if possible
        if (computerHand.size() == 1) {
            for (int i = 0; i < computerHand.size(); i++) {
                if (computerHand.get(i).matches(topCard)) {
                    return i;
                }
            }
        }

        // Second priority: Play special cards when opponent has few cards
        for (int i = 0; i < computerHand.size(); i++) {
            Card card = computerHand.get(i);
            if (card.matches(topCard) && card.isSpecial()) {
                return i;
            }
        }

        // Third priority: Play matching cards
        for (int i = 0; i < computerHand.size(); i++) {
            if (computerHand.get(i).matches(topCard)) {
                return i;
            }
        }

        return -1; // No playable card found
    }
}
