package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.model.card.Card;
import com.cardgame.model.card.Card.CardColor;
import com.cardgame.model.player.Player;
import com.cardgame.view.components.ModernButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

public class PlayStateUI {
    private PlayState playState;
    private ModernButton drawButton;
    private Rectangle drawBounds;
    private ModernButton backToMenuButton;
    private Rectangle backToMenuBounds;
    private Rectangle[] currentPlayerCardBounds;

    public PlayStateUI(PlayState playState) {
        this.playState = playState;
        this.drawButton = new ModernButton("Draw Card");
        this.drawBounds = new Rectangle(0, 0, 120, 40);
        this.backToMenuButton = new ModernButton("Back to Menu");
        this.backToMenuBounds = new Rectangle(0, 0, 150, 40);
        updateCurrentPlayerCardBounds();
    }

    public void render(Graphics g) {
        int windowWidth = playState.getGame().getWidth();
        int windowHeight = playState.getGame().getHeight();
        int topMargin = 50;
        int leftMargin = 30;
        int rightMargin = 30;
        drawBounds.setBounds(windowWidth - 150 - rightMargin, windowHeight - playState.getCardHeight() - 100, 120, 40);
        backToMenuBounds.setBounds(windowWidth - 150 - rightMargin, topMargin - 20, 150, 40);
        g.setColor(new Color(40, 44, 52));
        g.fillRect(0, 0, windowWidth, windowHeight);
        if (playState.isGameOver()) {
            renderGameOverScreen(g, windowWidth, windowHeight);
            return;
        }
        int cardCenterX = windowWidth / 2 - playState.getCardWidth() / 2;
        int cardCenterY = windowHeight / 2 - playState.getCardHeight() / 2 - 50;
        int deckImageX = cardCenterX - playState.getCardWidth() - 30;
        g.setColor(new Color(30, 34, 42));
        g.fillRoundRect(deckImageX, cardCenterY, playState.getCardWidth(), playState.getCardHeight(), 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String deckTextString = "Deck: " + playState.getDeck().remainingCards();
        FontMetrics deckFm = g.getFontMetrics();
        int deckTextWidth = deckFm.stringWidth(deckTextString);
        int deckTextX = deckImageX + (playState.getCardWidth() / 2) - (deckTextWidth / 2);
        int deckTextY = cardCenterY + playState.getCardHeight() + 15;
        g.drawString(deckTextString, deckTextX, deckTextY);
        if (playState.getTopCard() != null) {
            playState.getTopCard().render(g, cardCenterX, cardCenterY, playState.getCardWidth(), playState.getCardHeight());
            if (playState.getTopCard().getColor() == CardColor.GOLD) {
                g.setColor(playState.getCurrentWildColor().getAwtColor());
                g.setFont(new Font("Arial", Font.BOLD, 12));
                String wildColorText = "Color: " + playState.getCurrentWildColor().name();
                FontMetrics wildFm = g.getFontMetrics();
                int wildTextX = cardCenterX + (playState.getCardWidth() / 2) - (wildFm.stringWidth(wildColorText) / 2);
                g.drawString(wildColorText, wildTextX, cardCenterY + playState.getCardHeight() + 15);
                g.fillRect(cardCenterX + playState.getCardWidth() + 10, cardCenterY + playState.getCardHeight() - 15, 15, 15);
                g.setColor(Color.BLACK);
                g.drawRect(cardCenterX + playState.getCardWidth() + 10, cardCenterY + playState.getCardHeight() - 15, 15, 15);
            }
        }
        Player currentHumanPlayer = playState.getPlayers().get(playState.getCurrentPlayerIndex());
        if (!currentHumanPlayer.isComputer()) {
            List<Card> hand = currentHumanPlayer.getHand();
            if (currentPlayerCardBounds != null) {
                for (int i = 0; i < hand.size(); i++) {
                    if (i < currentPlayerCardBounds.length) {
                        Card card = hand.get(i);
                        card.setFaceUp(true);
                        boolean canPlayWild = card.getColor() == CardColor.GOLD;
                        boolean matchesNormally = card.matches(playState.getTopCard());
                        boolean matchesWildColorChoice = playState.getTopCard().getColor() == CardColor.GOLD && card.matches(playState.getCurrentWildColor());
                        card.setHighlighted(canPlayWild || matchesNormally || matchesWildColorChoice);
                        card.render(g, currentPlayerCardBounds[i].x, currentPlayerCardBounds[i].y, currentPlayerCardBounds[i].width, currentPlayerCardBounds[i].height);
                    }
                }
            }
        }
        int opponentInfoBaseY = topMargin + 10;
        int opponentInfoXStart = leftMargin;
        int opponentInfoXIncrement = 180;
        int opponentCardOffsetY = 20;
        int opponentCardMiniWidth = playState.getCardWidth() / 2;
        int opponentCardMiniHeight = playState.getCardHeight() / 2;
        for (int i = 0; i < playState.getPlayers().size(); i++) {
            Player p = playState.getPlayers().get(i);
            if (i == playState.getCurrentPlayerIndex() && !p.isComputer()) continue;
            int displayX = opponentInfoXStart + (i % 4) * opponentInfoXIncrement;
            int displayY = opponentInfoBaseY + (i / 4) * (opponentCardMiniHeight + 40 + opponentCardOffsetY);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString(p.getName() + ": " + p.handSize() + " cards", displayX, displayY);
            if (i == playState.getCurrentPlayerIndex()) {
                g.setColor(Color.YELLOW);
                g.drawRect(displayX - 5, displayY - 20, 170, 25 + opponentCardMiniHeight + 5);
            }
            for (int j = 0; j < p.handSize(); j++) {
                if (j < 5) {
                    Card backCard = new Card(CardColor.RED, 0);
                    backCard.setFaceUp(false);
                    backCard.render(g, displayX + j * (opponentCardMiniWidth / 3 + 2), displayY + opponentCardOffsetY, opponentCardMiniWidth, opponentCardMiniHeight);
                } else {
                    g.setFont(new Font("Arial", Font.PLAIN, 10));
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawString("+" + (p.handSize() - j) + "", displayX + j * (opponentCardMiniWidth / 3 + 2), displayY + opponentCardOffsetY + opponentCardMiniHeight / 2 + 5);
                    break;
                }
            }
        }
        drawButton.render(g, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
        backToMenuButton.render(g, backToMenuBounds.x, backToMenuBounds.y, backToMenuBounds.width, backToMenuBounds.height);
        if (playState.getMessageTimer() > 0 && playState.getMessage() != null && !playState.getMessage().trim().isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics msgFm = g.getFontMetrics();
            int messageWidth = msgFm.stringWidth(playState.getMessage());
            int messageX = (windowWidth - messageWidth) / 2;
            int messageY = cardCenterY + playState.getCardHeight() + 45;
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRoundRect(messageX - 10, messageY - msgFm.getAscent() - 5, messageWidth + 20, msgFm.getHeight() + 10, 15, 15);
            g.setColor(Color.YELLOW);
            g.drawString(playState.getMessage(), messageX, messageY);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        if (playState.getCurrentPlayerIndex() < playState.getPlayers().size()) {
            g.drawString(playState.getPlayers().get(playState.getCurrentPlayerIndex()).getName() + "'s Turn", leftMargin, topMargin);
        }
    }

    private void renderGameOverScreen(Graphics g, int windowWidth, int windowHeight) {
        if (playState.isShowOutcomeAnimation() && playState.isOutcomeInitialized() && playState.getOutcomeImage() != null) {
            int animX = (windowWidth - playState.getOutcomeImage().getWidth()) / 2;
            int animY = windowHeight / 2 - playState.getOutcomeImage().getHeight() / 2 + 70;
            g.setColor(new Color(255, 215, 0));
            g.fillRect(animX - 5, animY - 5, playState.getOutcomeImage().getWidth() + 10, playState.getOutcomeImage().getHeight() + 10);
            g.drawImage(playState.getOutcomeImage(), animX, animY, null);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("LOSER'S FATE:", animX, animY - 10);
        } else if (playState.isShowOutcomeAnimation()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Punishment loading...", windowWidth / 2 - 100, windowHeight / 2 + 80);
        }
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String gameOverText = "Game Over!";
        int textX = (windowWidth - fm.stringWidth(gameOverText)) / 2;
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(gameOverText, textX + 3, 150 + 3);
        g.setColor(Color.WHITE);
        g.drawString(gameOverText, textX, 150);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        fm = g.getFontMetrics();
        String winnerMsgText = playState.getWinnerName() != null ? playState.getWinnerName() + " Wins!" : "It's a Draw!";
        textX = (windowWidth - fm.stringWidth(winnerMsgText)) / 2;
        g.setColor(Color.BLACK);
        g.drawString(winnerMsgText, textX + 2, 220 + 2);
        g.setColor(Color.GREEN);
        g.drawString(winnerMsgText, textX, 220);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        int scoreY = 280;
        if (!playState.getPlayers().isEmpty()) {
            for (Player p : playState.getPlayers()) {
                String scoreText = p.getName() + ": " + p.handSize() + " cards remaining";
                fm = g.getFontMetrics();
                textX = (windowWidth - fm.stringWidth(scoreText)) / 2;
                g.drawString(scoreText, textX, scoreY);
                scoreY += 30;
            }
        }
        int buttonX = (windowWidth - backToMenuBounds.width) / 2;
        int buttonY = Math.max(scoreY + 20, windowHeight - 100);
        backToMenuBounds.setBounds(buttonX, buttonY, backToMenuBounds.width, backToMenuBounds.height);
        backToMenuButton.render(g, backToMenuBounds.x, backToMenuBounds.y, backToMenuBounds.width, backToMenuBounds.height);
    }

    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();
        if (playState.isGameOver()) {
            if (e.getID() == MouseEvent.MOUSE_MOVED) {
                backToMenuButton.setHovered(backToMenuBounds.contains(mouse));
            } else if (e.getID() == MouseEvent.MOUSE_CLICKED && backToMenuBounds.contains(mouse)) {
                playState.getGame().setState(new MenuState(playState.getGame()));
            }
            return;
        }
        Player currentActivePlayer = playState.getPlayers().get(playState.getCurrentPlayerIndex());
        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            boolean canInteractWithDraw = !currentActivePlayer.isComputer() || !playState.isAITurnPending();
            if (canInteractWithDraw) {
                drawButton.setHovered(drawBounds.contains(mouse));
            } else {
                drawButton.setHovered(false);
            }
            backToMenuButton.setHovered(backToMenuBounds.contains(mouse));
            return;
        }
        if (currentActivePlayer.isComputer() && playState.isAITurnPending()) {
            if (e.getID() == MouseEvent.MOUSE_CLICKED && backToMenuBounds.contains(mouse)) {
                playState.getGame().setState(new MenuState(playState.getGame()));
            }
            return;
        }
        if (e.getID() == MouseEvent.MOUSE_CLICKED) {
            if (drawBounds.contains(mouse) && !currentActivePlayer.isComputer()) {
                Card drawn = playState.getDeck().draw();
                if (drawn != null) {
                    currentActivePlayer.addCard(drawn);
                    playState.setMessage(currentActivePlayer.getName() + " drew a card.");
                    updateCurrentPlayerCardBounds();
                    playState.advanceTurn(null);
                } else {
                    playState.setMessage("Deck is empty!");
                }
                playState.setMessageTimer(120);
                return;
            }
            if (backToMenuBounds.contains(mouse)) {
                playState.getGame().setState(new MenuState(playState.getGame()));
                return;
            }
            if (!currentActivePlayer.isComputer()) {
                List<Card> hand = currentActivePlayer.getHand();
                for (int i = 0; i < currentPlayerCardBounds.length; i++) {
                    if (i < hand.size() && currentPlayerCardBounds[i].contains(mouse)) {
                        Card selectedCard = hand.get(i);
                        boolean canPlayWild = selectedCard.getColor() == CardColor.GOLD;
                        boolean matchesNormally = selectedCard.matches(playState.getTopCard());
                        boolean matchesWildColorChoice = playState.getTopCard().getColor() == CardColor.GOLD && selectedCard.matches(playState.getCurrentWildColor());
                        if (canPlayWild || matchesNormally || matchesWildColorChoice) {
                            Card played = currentActivePlayer.playCard(i);
                            playState.handlePlayedCard(played);
                            playState.advanceTurn(played);
                        } else {
                            playState.setMessage("Cannot play this card. Match color/value or play a Wild.");
                            playState.setMessageTimer(120);
                        }
                        return;
                    }
                }
            }
        }
    }

    public void onEnter() {
        if (drawButton != null) {
            drawButton.setHovered(false);
            drawButton.setPressed(false);
        }
        if (backToMenuButton != null) {
            backToMenuButton.setHovered(false);
            backToMenuButton.setPressed(false);
        }
        updateCurrentPlayerCardBounds();
    }

    private void updateCurrentPlayerCardBounds() {
        if (playState.isGameOver() || playState.getPlayers().isEmpty() || playState.getCurrentPlayerIndex() >= playState.getPlayers().size())
            return;
        Player currentP = playState.getPlayers().get(playState.getCurrentPlayerIndex());
        if (currentP.isComputer()) {
            currentPlayerCardBounds = new Rectangle[0];
            return;
        }
        List<Card> hand = currentP.getHand();
        int cardWidth = playState.getCardWidth();
        int cardHeight = playState.getCardHeight();
        int spacing = 10;
        int maxCardsWithoutOverlap = (playState.getGame().getWidth() - 100) / (cardWidth + spacing);
        int effectiveCardWidth = cardWidth;
        int effectiveSpacing = spacing;
        if (hand.size() > maxCardsWithoutOverlap && hand.size() > 0) {
            effectiveCardWidth = (playState.getGame().getWidth() - 100 - spacing) / hand.size();
            effectiveCardWidth = Math.max(40, effectiveCardWidth);
            effectiveSpacing = 5;
            if (hand.size() * effectiveCardWidth + (hand.size() - 1) * effectiveSpacing > playState.getGame().getWidth() - 100) {
                effectiveSpacing = -(effectiveCardWidth / 2);
            }
        }
        int totalHandWidth = hand.isEmpty() ? 0 : (hand.size() - 1) * (effectiveCardWidth + effectiveSpacing) + effectiveCardWidth;
        int startX = (playState.getGame().getWidth() - totalHandWidth) / 2;
        int yPos = playState.getGame().getHeight() - cardHeight - 50;
        currentPlayerCardBounds = new Rectangle[hand.size()];
        for (int i = 0; i < hand.size(); i++) {
            currentPlayerCardBounds[i] = new Rectangle(startX + i * (effectiveCardWidth + effectiveSpacing), yPos, effectiveCardWidth, cardHeight);
        }
    }
}