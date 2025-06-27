package com.cardgame.controller.states;

import com.cardgame.model.card.Card;
import com.cardgame.model.card.Card.CardColor;
import com.cardgame.model.player.Player;
import java.util.List;
import java.util.Map;

public class TurnManager {
    private PlayState playState;

    public TurnManager(PlayState playState) {
        this.playState = playState;
    }

    public Player getNextPlayer() {
        int nextIndex = calculateNextPlayerIndex(
            playState.getCurrentPlayerIndex(),
            playState.isDirectionClockwise(),
            false, 
            playState.getPlayers().size()
        );
        return playState.getPlayers().get(nextIndex);
    }

    public void processAITurn() {
        if (playState.isGameOver() || playState.getCurrentPlayerIndex() >= playState.getPlayers().size() || !playState.getPlayers().get(playState.getCurrentPlayerIndex()).isComputer()) {
            playState.setAITurnPending(false);
            return;
        }
        Player ai = playState.getPlayers().get(playState.getCurrentPlayerIndex());
        List<Card> aiHand = ai.getHand();
        Card cardToPlay = null;
        int cardIndex = -1;
        for (int i = 0; i < aiHand.size(); i++) {
            Card currentCardInHand = aiHand.get(i);
            boolean matchesTopNormally = currentCardInHand.matches(playState.getTopCard());
            boolean matchesWildChoice = (playState.getTopCard().getColor() == CardColor.GOLD && currentCardInHand.matches(playState.getCurrentWildColor()));
            boolean isWildCardInHand = currentCardInHand.getColor() == CardColor.GOLD;
            if (isWildCardInHand || matchesTopNormally || matchesWildChoice) {
                cardToPlay = currentCardInHand;
                cardIndex = i;
                break;
            }
        }
        if (cardToPlay != null && cardToPlay.getColor() == CardColor.GOLD) {
            playState.setCurrentColor(findMostCommonColorInHand(ai, true));
        }
        if (cardToPlay != null) {
            Card played = ai.playCard(cardIndex);
            String playedMessage = ai.getName() + " played " + playState.formatCardNameForMessage(played);
            if (played.getColor() == CardColor.GOLD) {
                playedMessage += " (chose " + playState.getCurrentWildColor().name() + ")";
            }
            playState.setMessage(playedMessage);
            playState.setMessageTimer(120);
            playState.handlePlayedCard(played);
            advanceTurn(played);
        } else {
            Card drawn = playState.getDeck().draw();
            if (drawn != null) {
                ai.addCard(drawn);
                playState.setMessage(ai.getName() + " drew a card.");
                playState.setMessageTimer(120);
                boolean canPlayDrawnNormally = drawn.matches(playState.getTopCard());
                boolean canPlayDrawnOnWild = (playState.getTopCard().getColor() == CardColor.GOLD && drawn.matches(playState.getCurrentWildColor()));
                boolean drawnIsWild = drawn.getColor() == CardColor.GOLD;
                if (drawnIsWild || canPlayDrawnNormally || canPlayDrawnOnWild) {
                    Card playedAfterDraw = ai.playCard(ai.getHand().indexOf(drawn));
                    String playedMessage = ai.getName() + " drew and played " + playState.formatCardNameForMessage(playedAfterDraw);
                    if (playedAfterDraw.getColor() == CardColor.GOLD) {
                        playState.setCurrentColor(findMostCommonColorInHand(ai, true));
                        playedMessage += " (chose " + playState.getCurrentWildColor().name() + ")";
                    }
                    playState.setMessage(playedMessage);
                    playState.setMessageTimer(120);
                    playState.handlePlayedCard(playedAfterDraw);
                    advanceTurn(playedAfterDraw);
                    return;
                }
            } else {
                playState.setMessage(ai.getName() + " tried to draw, but deck is empty.");
                playState.setMessageTimer(120);
            }
            advanceTurn(null);
        }
    }

    public void advanceTurn(Card cardJustPlayed) {
        if (playState.isGameOver()) return;
        int previousPlayerActualIndex = playState.getCurrentPlayerIndex();
        playState.setCurrentPlayerIndex(calculateNextPlayerIndex(playState.getCurrentPlayerIndex(), playState.isDirectionClockwise(), playState.getSkipNextPlayerTurnFlag(), playState.getPlayers().size()));
        if (playState.getSkipNextPlayerTurnFlag()) {
            int skippedPlayerIndex = (previousPlayerActualIndex + (playState.isDirectionClockwise() ? 1 : -1) + playState.getPlayers().size()) % playState.getPlayers().size();
            if (playState.getPlayers().size() > 0 && skippedPlayerIndex < playState.getPlayers().size() && playState.getCurrentPlayerIndex() < playState.getPlayers().size()) {
                playState.setMessage(playState.getPlayers().get(skippedPlayerIndex).getName() + "'s turn was skipped! Now " + playState.getPlayers().get(playState.getCurrentPlayerIndex()).getName() + "'s turn.");
            } else {
                playState.setMessage("A player was skipped! Now " + (playState.getCurrentPlayerIndex() < playState.getPlayers().size() ? playState.getPlayers().get(playState.getCurrentPlayerIndex()).getName() : "Next Player") + "'s turn.");
            }
            playState.setSkipNextPlayerTurnFlag(false);
        } else {
            if (playState.getCurrentPlayerIndex() < playState.getPlayers().size() && (playState.getMessage() == null || !playState.getMessage().contains("played REVERSE") && !playState.getMessage().contains("draws 2 cards"))) {
                playState.setMessage(playState.getPlayers().get(playState.getCurrentPlayerIndex()).getName() + "'s turn!");
            } else if (playState.getCurrentPlayerIndex() < playState.getPlayers().size() && playState.getMessage() != null && playState.getMessage().endsWith(".")) {
                playState.setMessage(playState.getMessage() + " Next is " + playState.getPlayers().get(playState.getCurrentPlayerIndex()).getName() + ".");
            }
        }
        playState.setMessageTimer(120);
        if (!playState.isGameOver() && playState.getCurrentPlayerIndex() < playState.getPlayers().size() && playState.getPlayers().get(playState.getCurrentPlayerIndex()).isComputer() && playState.isIncludeComputerPlayer()) {
            playState.setAITurnPending(true);
        } else {
            playState.setAITurnPending(false);
        }
    }

    private static int calculateNextPlayerIndex(int currentIndex, boolean isClockwise, boolean applySkip, int numPlayers) {
        if (numPlayers <= 0) return 0;
        int direction = isClockwise ? 1 : -1;
        int nextIndex = (currentIndex + direction + numPlayers) % numPlayers;
        if (applySkip) {
            nextIndex = (nextIndex + direction + numPlayers) % numPlayers;
        }
        return nextIndex;
    }

    private CardColor findMostCommonColorInHand(Player player, boolean forAIWildChoice) {
        Map<CardColor, Integer> colorCounts = player.getColorCounts();
        CardColor mostCommon = null;
        int max = -1;

        for (CardColor c : CardColor.values()) {
            if (c == CardColor.GOLD && !forAIWildChoice) continue;
            int count = colorCounts.getOrDefault(c, 0);
            if (count > max) {
                max = count;
                mostCommon = c;
            }
        }

        if (mostCommon == null || max <= 0) {
            for (Card card : player.getHand()) {
                if (card.getColor() != CardColor.GOLD) return card.getColor();
            }
            return CardColor.BLUE;
        }
        return mostCommon;
    }
}