package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.model.card.Card;
import com.cardgame.model.card.Card.CardColor;
import com.cardgame.model.card.Deck;
import com.cardgame.model.card.effect.CardEffect;
import com.cardgame.model.card.effect.CardEffectFactory;
import com.cardgame.model.card.effect.DrawCardEffect;
import com.cardgame.model.card.effect.ReverseDirectionEffect;
import com.cardgame.model.card.effect.SkipTurnEffect;
import com.cardgame.model.card.effect.WildCardEffect;
import com.cardgame.model.player.Player;
import com.cardgame.view.components.ModernButton;
import com.cardgame.model.game.GameOutcome;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayState extends GameState {
    private List<Player> players;
    private int currentPlayerIndex;
    private Deck deck;
    private Card topCard;
    private boolean skipNextPlayerTurnFlag;
    private boolean gameOver;
    private String winnerName;
    private boolean directionClockwise = true;
    private CardColor currentWildColor; 

    private ModernButton drawButton;
    private Rectangle drawBounds;
    private ModernButton backToMenuButton;
    private Rectangle backToMenuBounds;
    private Rectangle[] currentPlayerCardBounds; 
    private String message;
    private int messageTimer;

    private boolean showOutcomeAnimation;
    private BufferedImage outcomeImage;
    private boolean outcomeInitialized = false;

    private boolean isAITurnPending = false;
    private boolean includeComputerPlayer; 

    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 120;

    public PlayState(Game game) {
        super(game);
        List<String> defaultNames = new ArrayList<>();
        defaultNames.add("Player");
        initializeGame(defaultNames, true);
    }

    public PlayState(Game game, List<String> playerNames, boolean includeComputer) {
        super(game);
        initializeGame(playerNames, includeComputer);
    }

    private void initializeGame(List<String> playerNamesFromSelection, boolean includeComputerMode) {
        this.includeComputerPlayer = includeComputerMode;
        deck = new Deck();
        players = new ArrayList<>();

        try {
            GameOutcome.loadOutcomeImages();
            outcomeInitialized = true;
        } catch (Exception e) {
            System.err.println("Error loading GameOutcome animations: " + e.getMessage());
        }
        showOutcomeAnimation = false;
        outcomeImage = null;

        if (this.includeComputerPlayer) {
            players.add(new Player(playerNamesFromSelection.get(0), false)); 
            players.add(new Player("Computer", true)); 
        } else {
            for (String name : playerNamesFromSelection) {
                players.add(new Player(name, false)); 
            }
        }

        for (Player p : players) {
            p.addCards(deck.draw(7));
        }

        topCard = deck.draw();
        while (topCard != null && topCard.isSpecial()) { 
            deck.discard(topCard);
            topCard = deck.draw();
        }

        if (topCard == null) { 
            gameOver = true;
            message = "Error: Could not start game with a valid card.";
            messageTimer = Integer.MAX_VALUE;
            winnerName = "No One (Setup Error)";
            return;
        }
        topCard.setFaceUp(true);
        currentWildColor = topCard.getColor(); 
        currentPlayerIndex = 0; 
        skipNextPlayerTurnFlag = false;
        gameOver = false;
        winnerName = null;
        directionClockwise = true;
        message = players.get(currentPlayerIndex).getName() + "'s turn! Match color or number.";
        messageTimer = 180;

        drawButton = new ModernButton("Draw Card");
        drawBounds = new Rectangle(0, 0, 120, 40);
        backToMenuButton = new ModernButton("Back to Menu");
        backToMenuBounds = new Rectangle(0, 0, 150, 40);

        updateCurrentPlayerCardBounds();

        if (!gameOver && players.get(currentPlayerIndex).isComputer() && this.includeComputerPlayer) {
            isAITurnPending = true;
        } else {
            isAITurnPending = false;
        }
    }

    private void updateCurrentPlayerCardBounds() {
        if (gameOver || players.isEmpty() || currentPlayerIndex >= players.size()) return;

        Player currentP = players.get(currentPlayerIndex);
        if (currentP.isComputer()) {
            currentPlayerCardBounds = new Rectangle[0]; 
            return;
        }

        List<Card> hand = currentP.getHand();
        int cardWidth = CARD_WIDTH;
        int cardHeight = CARD_HEIGHT;
        int spacing = 10;

        int maxCardsWithoutOverlap = (getGame().getWidth() - 100) / (cardWidth + spacing); 
        int effectiveCardWidth = cardWidth;
        int effectiveSpacing = spacing;

        if (hand.size() > maxCardsWithoutOverlap && hand.size() > 0) {
            effectiveCardWidth = (getGame().getWidth() - 100 - spacing) / hand.size(); // Distributing width
            effectiveCardWidth = Math.max(40, effectiveCardWidth); 
            effectiveSpacing = 5; 
            if(hand.size() * effectiveCardWidth + (hand.size()-1)*effectiveSpacing > getGame().getWidth() - 100){
                effectiveSpacing = - (effectiveCardWidth / 2); 
            }
        }

        int totalHandWidth = hand.isEmpty() ? 0 : (hand.size() -1) * (effectiveCardWidth + effectiveSpacing) + effectiveCardWidth;
        int startX = (getGame().getWidth() - totalHandWidth) / 2;
        int yPos = getGame().getHeight() - cardHeight - 50;

        currentPlayerCardBounds = new Rectangle[hand.size()];
        for (int i = 0; i < hand.size(); i++) {
            currentPlayerCardBounds[i] = new Rectangle(startX + i * (effectiveCardWidth + effectiveSpacing), yPos, effectiveCardWidth, cardHeight);
        }
    }

    @Override
    public void tick() {
        if (messageTimer > 0) {
            messageTimer--;
        }

        if (showOutcomeAnimation && gameOver) {
            try {
                outcomeImage = GameOutcome.getRandomOutcomeImageStatic();
            } catch (Exception e) {
                System.err.println("Error updating animation: " + e.getMessage());
            }
        }

        if (!gameOver && currentPlayerIndex < players.size() && players.get(currentPlayerIndex).isComputer() && isAITurnPending) {
            isAITurnPending = false;
            processAITurn();
        }
    }

    private void processAITurn() {
        if (gameOver || currentPlayerIndex >= players.size() || !players.get(currentPlayerIndex).isComputer()) {
            isAITurnPending = false;
            return;
        }

        Player ai = players.get(currentPlayerIndex);
        List<Card> aiHand = ai.getHand();
        Card cardToPlay = null;
        int cardIndex = -1;

        for (int i = 0; i < aiHand.size(); i++) {
            Card currentCardInHand = aiHand.get(i);
            boolean matchesTopNormally = currentCardInHand.matches(topCard);
            boolean matchesWildChoice = (topCard.getColor() == CardColor.GOLD && currentCardInHand.matches(currentWildColor));
            boolean isWildCardInHand = currentCardInHand.getColor() == CardColor.GOLD;

            if (isWildCardInHand || matchesTopNormally || matchesWildChoice) {
                cardToPlay = currentCardInHand;
                cardIndex = i;
                break;
            }
        }

        if (cardToPlay != null && cardToPlay.getColor() == CardColor.GOLD) {
            this.currentWildColor = findMostCommonColorInHand(ai, true); 
        }

        if (cardToPlay != null) {
            Card played = ai.playCard(cardIndex);
            String playedMessage = ai.getName() + " played " + formatCardNameForMessage(played);
            if (played.getColor() == CardColor.GOLD) {
                playedMessage += " (chose " + this.currentWildColor.name() + ")";
            }
            message = playedMessage;
            messageTimer = 120;

            handlePlayedCard(played);
            advanceTurn(played);
        } else {
            Card drawn = deck.draw();
            if (drawn != null) {
                ai.addCard(drawn);
                message = ai.getName() + " drew a card.";
                messageTimer = 120;

                boolean canPlayDrawnNormally = drawn.matches(topCard);
                boolean canPlayDrawnOnWild = (topCard.getColor() == CardColor.GOLD && drawn.matches(currentWildColor));
                boolean drawnIsWild = drawn.getColor() == CardColor.GOLD;

                if (drawnIsWild || canPlayDrawnNormally || canPlayDrawnOnWild) {
                    Card playedAfterDraw = ai.playCard(ai.getHand().indexOf(drawn));
                    String playedMessage = ai.getName() + " drew and played " + formatCardNameForMessage(playedAfterDraw);
                    if (playedAfterDraw.getColor() == CardColor.GOLD) {
                        this.currentWildColor = findMostCommonColorInHand(ai, true);
                        playedMessage += " (chose " + this.currentWildColor.name() + ")";
                    }
                    message = playedMessage;
                    messageTimer = 120;

                    handlePlayedCard(playedAfterDraw);
                    advanceTurn(playedAfterDraw);
                    return;
                }
            } else {
                message = ai.getName() + " tried to draw, but deck is empty.";
                messageTimer = 120;
            }
            advanceTurn(null);
        }
    }

    private String formatCardNameForMessage(Card card) {
        if (card == null) return "a null card";
        String cardColorName = card.getColor().name();
        if (card.isSpecial()) {
            return switch (card.getColor()) {
                case RED -> "RED SKIP";
                case BLUE -> "BLUE DRAW 2"; 
                case GREEN -> "GREEN REVERSE";
                case GOLD -> "WILD"; 
            };
        } else {
            return cardColorName + " " + card.getValue();
        }
    }


    private void handlePlayedCard(Card playedCard) {
        if (playedCard == null) return;

        if (topCard != null) {
            deck.discard(topCard);
        }
        topCard = playedCard;
        topCard.setFaceUp(true);

        Player activePlayer = players.get(currentPlayerIndex);

        if (playedCard.getColor() == CardColor.GOLD) {
            if (activePlayer.isComputer()) {
                setCurrentColor(this.currentWildColor);
            } else {
                promptForColorSelection();
            }
        } else {
            currentWildColor = playedCard.getColor();
        }

        CardEffect effect = CardEffectFactory.createEffect(playedCard);
        if (effect != null) {
            if (effect instanceof DrawCardEffect) {
                int nextPlayerActualIndex = calculateNextPlayerIndex(currentPlayerIndex, directionClockwise, false, players.size());
                Player playerToDraw = players.get(nextPlayerActualIndex);
                message = playerToDraw.getName() + " draws 2 cards!";
                effect.apply(this, playerToDraw);
            } else if (effect instanceof SkipTurnEffect) {
                
                effect.apply(this, null);
            } else if (effect instanceof ReverseDirectionEffect) {
                message = activePlayer.getName() + " played REVERSE! Direction of play is changed.";
                effect.apply(this, null);
            } else if (effect instanceof WildCardEffect) {
                effect.apply(this, activePlayer);
            } else {
                effect.apply(this, activePlayer);
            }
        }

        if (messageTimer <= 0 && !gameOver && (message == null || message.endsWith("'s turn!"))) { 
            message = activePlayer.getName() + " played " + formatCardNameForMessage(playedCard) + ".";
            messageTimer = 120;
        }

        if (activePlayer.handSize() == 0) {
            gameOver = true;
            winnerName = activePlayer.getName();
            message = "Game Over! " + winnerName + " wins!";
            messageTimer = Integer.MAX_VALUE;
            showOutcomeAnimation = true;
            if (outcomeInitialized) GameOutcome.resetAnimationStatic();
        }
    }

    private void advanceTurn(Card cardJustPlayed) {
        if (gameOver) return;

        int previousPlayerActualIndex = currentPlayerIndex;
        currentPlayerIndex = calculateNextPlayerIndex(currentPlayerIndex, directionClockwise, skipNextPlayerTurnFlag, players.size());

        if (skipNextPlayerTurnFlag) {
            int skippedPlayerIndex = (previousPlayerActualIndex + (directionClockwise ? 1 : -1) + players.size()) % players.size();
            
            if (players.size() > 0 && skippedPlayerIndex < players.size() && currentPlayerIndex < players.size()) {
                message = players.get(skippedPlayerIndex).getName() + "'s turn was skipped! Now " + players.get(currentPlayerIndex).getName() + "'s turn.";
            } else {
                message = "A player was skipped! Now " + (currentPlayerIndex < players.size() ? players.get(currentPlayerIndex).getName() : "Next Player") + "'s turn.";
            }
            skipNextPlayerTurnFlag = false; 
        } else {
            if (currentPlayerIndex < players.size() && (message == null || !message.contains("played REVERSE") && !message.contains("draws 2 cards"))) {
                message = players.get(currentPlayerIndex).getName() + "'s turn!";
            } else if (currentPlayerIndex < players.size() && message != null && message.endsWith(".")) {
                message += " Next is " + players.get(currentPlayerIndex).getName() + ".";
            }
        }

        messageTimer = 120;
        updateCurrentPlayerCardBounds();

        if (!gameOver && currentPlayerIndex < players.size() && players.get(currentPlayerIndex).isComputer() && this.includeComputerPlayer) {
            isAITurnPending = true;
        } else {
            isAITurnPending = false;
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

    @Override
    public void render(Graphics g) {
        int windowWidth = getGame().getWidth();
        int windowHeight = getGame().getHeight();

        int topMargin = 50; 
        int leftMargin = 30; 
        int rightMargin = 30; 

        drawBounds.setBounds(windowWidth - 150 - rightMargin, windowHeight - CARD_HEIGHT - 100, 120, 40);
        backToMenuBounds.setBounds(windowWidth - 150 - rightMargin, topMargin - 20 , 150, 40);


        g.setColor(new Color(40, 44, 52));
        g.fillRect(0, 0, windowWidth, windowHeight);

        if (gameOver) {
            renderGameOverScreen(g, windowWidth, windowHeight); 
            return;
        }

        int cardCenterX = windowWidth / 2 - CARD_WIDTH / 2;
        int cardCenterY = windowHeight / 2 - CARD_HEIGHT / 2 - 50; 

        int deckImageX = cardCenterX - CARD_WIDTH - 30; 
        g.setColor(new Color(30, 34, 42));
        g.fillRoundRect(deckImageX, cardCenterY, CARD_WIDTH, CARD_HEIGHT, 10, 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String deckTextString = "Deck: " + deck.remainingCards();
        FontMetrics deckFm = g.getFontMetrics();
        int deckTextWidth = deckFm.stringWidth(deckTextString);
        int deckTextX = deckImageX + (CARD_WIDTH / 2) - (deckTextWidth / 2); 
        int deckTextY = cardCenterY + CARD_HEIGHT + 15; 
        g.drawString(deckTextString, deckTextX, deckTextY);

        if (topCard != null) {
            topCard.render(g, cardCenterX, cardCenterY, CARD_WIDTH, CARD_HEIGHT);
            if (topCard.getColor() == CardColor.GOLD) {
                g.setColor(currentWildColor.getAwtColor());
                g.setFont(new Font("Arial", Font.BOLD, 12));
                String wildColorText = "Color: " + currentWildColor.name();
                FontMetrics wildFm = g.getFontMetrics();
                int wildTextX = cardCenterX + (CARD_WIDTH/2) - (wildFm.stringWidth(wildColorText)/2) ; 
                g.drawString(wildColorText, wildTextX, cardCenterY + CARD_HEIGHT + 15);

                g.fillRect(cardCenterX + CARD_WIDTH + 10, cardCenterY + CARD_HEIGHT - 15, 15, 15);
                g.setColor(Color.BLACK);
                g.drawRect(cardCenterX + CARD_WIDTH + 10, cardCenterY + CARD_HEIGHT - 15, 15, 15);
            }
        }

        Player currentHumanPlayer = players.get(currentPlayerIndex);
        if (!currentHumanPlayer.isComputer()) {
            List<Card> hand = currentHumanPlayer.getHand();
            if (currentPlayerCardBounds != null) {
                for (int i = 0; i < hand.size(); i++) {
                    if (i < currentPlayerCardBounds.length) { 
                        Card card = hand.get(i);
                        card.setFaceUp(true);
                        boolean canPlayWild = card.getColor() == CardColor.GOLD;
                        boolean matchesNormally = card.matches(topCard);
                        boolean matchesWildColorChoice = topCard.getColor() == CardColor.GOLD && card.matches(currentWildColor);
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
        int opponentCardMiniWidth = CARD_WIDTH / 2;
        int opponentCardMiniHeight = CARD_HEIGHT / 2;

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (i == currentPlayerIndex && !p.isComputer()) continue;

            int displayX = opponentInfoXStart + (i % 4) * opponentInfoXIncrement;
            int displayY = opponentInfoBaseY + (i / 4) * (opponentCardMiniHeight + 40 + opponentCardOffsetY);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString(p.getName() + ": " + p.handSize() + " cards", displayX, displayY);

            if (i == currentPlayerIndex) { 
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

        if (messageTimer > 0 && message !=null && !message.trim().isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics msgFm = g.getFontMetrics();
            int messageWidth = msgFm.stringWidth(message);
            int messageX = (windowWidth - messageWidth) / 2;
            int messageY = cardCenterY + CARD_HEIGHT + 45;

            g.setColor(new Color(0,0,0,180));
            g.fillRoundRect(messageX - 10, messageY - msgFm.getAscent() - 5, messageWidth + 20, msgFm.getHeight() + 10, 15, 15);
            g.setColor(Color.YELLOW);
            g.drawString(message, messageX, messageY);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        if (currentPlayerIndex < players.size()) { 
            g.drawString(players.get(currentPlayerIndex).getName() + "'s Turn", leftMargin, topMargin);
        }
    }

    private void renderGameOverScreen(Graphics g, int windowWidth, int windowHeight) {
        if (showOutcomeAnimation && outcomeInitialized && outcomeImage != null) {
            int animX = (windowWidth - outcomeImage.getWidth()) / 2;
            int animY = windowHeight / 2 - outcomeImage.getHeight() / 2 + 70;
            g.setColor(new Color(255, 215, 0));
            g.fillRect(animX - 5, animY - 5, outcomeImage.getWidth() + 10, outcomeImage.getHeight() + 10);
            g.drawImage(outcomeImage, animX, animY, null);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("LOSER'S FATE:", animX, animY - 10);
        } else if (showOutcomeAnimation) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Punishment loading...", windowWidth/2 - 100, windowHeight/2 + 80);
        }

        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String gameOverText = "Game Over!";
        int textX = (windowWidth - fm.stringWidth(gameOverText)) / 2;
        g.setColor(new Color(0,0,0,150));
        g.drawString(gameOverText, textX + 3, 150 + 3);
        g.setColor(Color.WHITE);
        g.drawString(gameOverText, textX, 150);

        g.setFont(new Font("Arial", Font.BOLD, 32));
        fm = g.getFontMetrics();
        String winnerMsgText = winnerName != null ? winnerName + " Wins!" : "It's a Draw!"; 
        textX = (windowWidth - fm.stringWidth(winnerMsgText)) / 2;
        g.setColor(Color.BLACK);
        g.drawString(winnerMsgText, textX + 2, 220 + 2);
        g.setColor(Color.GREEN);
        g.drawString(winnerMsgText, textX, 220);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        int scoreY = 280;
        if (!players.isEmpty()) { 
            for(Player p : players) {
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


    @Override
    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();

        if (gameOver) {
            if (e.getID() == MouseEvent.MOUSE_MOVED) {
                backToMenuButton.setHovered(backToMenuBounds.contains(mouse));
            } else if (e.getID() == MouseEvent.MOUSE_CLICKED && backToMenuBounds.contains(mouse)) {
                getGame().setState(new MenuState(getGame()));
            }
            return;
        }

        Player currentActivePlayer = players.get(currentPlayerIndex);

        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            boolean canInteractWithDraw = !currentActivePlayer.isComputer() || !isAITurnPending;
            if(canInteractWithDraw) {
                drawButton.setHovered(drawBounds.contains(mouse));
            } else {
                drawButton.setHovered(false);
            }
            backToMenuButton.setHovered(backToMenuBounds.contains(mouse));
            return;
        }

        if (currentActivePlayer.isComputer() && isAITurnPending) { 
            if (e.getID() == MouseEvent.MOUSE_CLICKED && backToMenuBounds.contains(mouse)) { 
                getGame().setState(new MenuState(getGame()));
            }
            return; 
        }


        if (e.getID() == MouseEvent.MOUSE_CLICKED) {
            if (drawBounds.contains(mouse) && !currentActivePlayer.isComputer()) {
                Card drawn = deck.draw();
                if (drawn != null) {
                    currentActivePlayer.addCard(drawn);
                    message = currentActivePlayer.getName() + " drew a card.";
                    updateCurrentPlayerCardBounds();
                    advanceTurn(null);
                } else {
                    message = "Deck is empty!";
                }
                messageTimer = 120;
                return;
            }

            if (backToMenuBounds.contains(mouse)) {
                getGame().setState(new MenuState(getGame()));
                return;
            }

            if (!currentActivePlayer.isComputer()) {
                List<Card> hand = currentActivePlayer.getHand();
                for (int i = 0; i < currentPlayerCardBounds.length; i++) {
                    if (i < hand.size() && currentPlayerCardBounds[i].contains(mouse)) {
                        Card selectedCard = hand.get(i);
                        boolean canPlayWild = selectedCard.getColor() == CardColor.GOLD;
                        boolean matchesNormally = selectedCard.matches(topCard);
                        boolean matchesWildColorChoice = topCard.getColor() == CardColor.GOLD && selectedCard.matches(currentWildColor);

                        if (canPlayWild || matchesNormally || matchesWildColorChoice) {
                            Card played = currentActivePlayer.playCard(i);
                            handlePlayedCard(played);
                            advanceTurn(played);
                        } else {
                            message = "Cannot play this card. Match color/value or play a Wild.";
                            messageTimer = 120;
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override
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
        if (!gameOver && currentPlayerIndex < players.size() && players.get(currentPlayerIndex).isComputer() && this.includeComputerPlayer) {
            isAITurnPending = true;
        } else {
            isAITurnPending = false;
        }
    }

    @Override
    public void onExit() {  }

    public Deck getDeck() { return deck; }

    public void skipNextTurn() {
        this.skipNextPlayerTurnFlag = true;
    }

    public void reverseDirection() {
        directionClockwise = !directionClockwise;
    }

    public CardColor getCurrentWildColor() {
        return this.currentWildColor;
    }

    public void setCurrentColor(CardColor color) {
        this.currentWildColor = color;
    }

    public void promptForColorSelection() {
        Player p = players.get(currentPlayerIndex);
        CardColor chosenColor;
        if (p.isComputer()) {
            
            chosenColor = (this.currentWildColor != null && this.currentWildColor != CardColor.GOLD) ? this.currentWildColor : findMostCommonColorInHand(p, true);
        } else {
            chosenColor = findMostCommonColorInHand(p, false);
            message = p.getName() + " played WILD and chose " + chosenColor.name() + ".";
            messageTimer = 120;
        }
        setCurrentColor(chosenColor);
    }

    private CardColor findMostCommonColorInHand(Player player, boolean forAIWildChoice) {
        Map<CardColor, Integer> counts = player.getColorCounts();
        CardColor mostCommon = null;
        int max = -1;

        for (CardColor c : CardColor.values()) {
            if (c == CardColor.GOLD && !forAIWildChoice) continue;
            int count = counts.getOrDefault(c, 0);
            if (count > max) {
                max = count;
                mostCommon = c;
            }
        }

        if (mostCommon == null || max <=0) { 
            for (Card card : player.getHand()) {
                if (card.getColor() != CardColor.GOLD) return card.getColor();
            }
            return CardColor.BLUE; 
        }
        return mostCommon;
    }
}