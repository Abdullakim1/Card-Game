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
    private CardColor currentWildColor; // Stores the chosen color when a Wild card is on top

    private ModernButton drawButton;
    private Rectangle drawBounds;
    private ModernButton backToMenuButton;
    private Rectangle backToMenuBounds;
    private Rectangle[] currentPlayerCardBounds; // Bounds for the current human player's cards
    private String message;
    private int messageTimer;

    // Game outcome animation
    private boolean showOutcomeAnimation;
    private BufferedImage outcomeImage;
    private boolean outcomeInitialized = false;

    private boolean isAITurnPending = false;
    private boolean includeComputerPlayer; // To distinguish game mode

    // Constants for card rendering, can be static or instance
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
            players.add(new Player(playerNamesFromSelection.get(0), false)); // Human player
            players.add(new Player("Computer", true)); // AI player
        } else {
            for (String name : playerNamesFromSelection) {
                players.add(new Player(name, false)); // All human players
            }
        }

        for (Player p : players) {
            p.addCards(deck.draw(7));
        }

        topCard = deck.draw();
        while (topCard != null && topCard.isSpecial()) { // First card shouldn't be special
            deck.discard(topCard);
            topCard = deck.draw();
        }

        if (topCard == null) { // Should not happen with a standard deck
            gameOver = true;
            message = "Error: Could not start game with a valid card.";
            messageTimer = Integer.MAX_VALUE;
            winnerName = "No One (Setup Error)";
            return;
        }
        topCard.setFaceUp(true);
        currentWildColor = topCard.getColor(); // Initial active color

        currentPlayerIndex = 0; // First player starts
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
        // Only update bounds if it's a human player, AI hand is not shown this way
        if (currentP.isComputer()) {
            currentPlayerCardBounds = new Rectangle[0]; // No bounds needed for AI cards displayed this way
            return;
        }

        List<Card> hand = currentP.getHand();
        int cardWidth = CARD_WIDTH;
        int cardHeight = CARD_HEIGHT;
        int spacing = 10;

        int maxCardsWithoutOverlap = (getGame().getWidth() - 100) / (cardWidth + spacing); // Max cards before overlap
        int effectiveCardWidth = cardWidth;
        int effectiveSpacing = spacing;

        if (hand.size() > maxCardsWithoutOverlap && hand.size() > 0) {
            effectiveCardWidth = (getGame().getWidth() - 100 - spacing) / hand.size(); // Distribute width
            effectiveCardWidth = Math.max(40, effectiveCardWidth); // Min width
            effectiveSpacing = 5; // Smaller spacing when overlapped
            if(hand.size() * effectiveCardWidth + (hand.size()-1)*effectiveSpacing > getGame().getWidth() - 100){
                // if still too wide, overlap cards by reducing spacing
                effectiveSpacing = - (effectiveCardWidth / 2); // Overlap
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
                outcomeImage = GameOutcome.getRandomOutcomeImage();
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

        // AI Logic:
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
            this.currentWildColor = findMostCommonColorInHand(ai, true); // AI chooses color
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
                case BLUE -> "BLUE DRAW 2"; // Hardcoded "2" for message simplicity
                case GREEN -> "GREEN REVERSE";
                case GOLD -> "WILD"; // Color choice will be appended separately
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
                // AI's chosen color (this.currentWildColor) was already set in processAITurn
                // No message change needed here as processAITurn handles it
            } else { // Human player played a WILD card
                promptForColorSelection(); // This will set this.currentWildColor and a message for human
            }
        } else {
            currentWildColor = playedCard.getColor(); // Non-wild card sets the active color
        }

        CardEffect effect = CardEffectFactory.createEffect(playedCard);
        if (effect != null) {
            if (effect instanceof DrawCardEffect) {
                int nextPlayerActualIndex = calculateNextPlayerIndex(currentPlayerIndex, directionClockwise, false, players.size());
                Player playerToDraw = players.get(nextPlayerActualIndex);
                message = playerToDraw.getName() + " draws 2 cards! Turn skipped."; // Draw 2 effect
                effect.apply(this, playerToDraw); // apply calls state.skipNextTurn()
            } else if (effect instanceof SkipTurnEffect) {
                // Message about skip is handled by advanceTurn after skipNextPlayerTurnFlag is used
                effect.apply(this, null); // Player arg not strictly used by SkipTurnEffect
            } else if (effect instanceof ReverseDirectionEffect) {
                // Message about reverse is handled by advanceTurn when Green card is played
                effect.apply(this, null); // Player arg not used
            } else if (effect instanceof WildCardEffect) {
                // Color choice handled above. WildCardEffect's apply might call promptForColorSelection
                // if not already set.
                effect.apply(this, activePlayer);
            } else {
                effect.apply(this, activePlayer); // Default for other effects
            }
        }

        // If message wasn't updated by an effect, ensure it's not stale
        if (messageTimer <= 0 && !gameOver) {
            message = players.get(currentPlayerIndex).getName() + " played. Next turn.";
            messageTimer = 120;
        }


        if (activePlayer.handSize() == 0) {
            gameOver = true;
            winnerName = activePlayer.getName();
            message = "Game Over! " + winnerName + " wins!";
            messageTimer = Integer.MAX_VALUE;
            showOutcomeAnimation = true;
            if (outcomeInitialized) GameOutcome.resetAnimation();
        }
    }

    private void advanceTurn(Card cardJustPlayed) {
        if (gameOver) return;

        boolean turnRepeats = false;
        if (cardJustPlayed != null && cardJustPlayed.getColor() == CardColor.GREEN) { // GREEN REVERSE = player plays again
            turnRepeats = true;
            message = players.get(currentPlayerIndex).getName() + " played REVERSE and plays again!";
        }
        // RED SKIP: skipNextPlayerTurnFlag is set by SkipTurnEffect, turn passes.

        if (!turnRepeats) {
            int previousPlayerActualIndex = currentPlayerIndex; // Store for message generation if a skip occurs
            currentPlayerIndex = calculateNextPlayerIndex(currentPlayerIndex, directionClockwise, skipNextPlayerTurnFlag, players.size());

            if (skipNextPlayerTurnFlag) {
                // The player at 'skippedPlayerIndex' was the one intended to play before the skip was applied
                int skippedPlayerIndex = (previousPlayerActualIndex + (directionClockwise ? 1 : -1) + players.size()) % players.size();
                if (skippedPlayerIndex == currentPlayerIndex && players.size() > 1) { // This means skipNextPlayerTurnFlag caused a double skip to return to same player after one skip
                    // This case needs careful thought for N players. For 2 players, it's clear.
                    // calculateNextPlayerIndex handles one skip.
                    message = players.get(skippedPlayerIndex).getName() + "'s turn was skipped! Now " + players.get(currentPlayerIndex).getName() + "'s turn.";
                } else if (skippedPlayerIndex != currentPlayerIndex) {
                    message = players.get(skippedPlayerIndex).getName() + "'s turn was skipped! Now " + players.get(currentPlayerIndex).getName() + "'s turn.";
                } else { // Only one player, or skip resulted in same player.
                    message = players.get(currentPlayerIndex).getName() + "'s turn!";
                }
                skipNextPlayerTurnFlag = false; // Reset the flag
            } else {
                message = players.get(currentPlayerIndex).getName() + "'s turn!";
            }
        }
        // If turnRepeats, currentPlayerIndex remains the same. Message for "plays again" already set.

        messageTimer = 120;
        updateCurrentPlayerCardBounds();

        if (!gameOver && currentPlayerIndex < players.size() && players.get(currentPlayerIndex).isComputer() && this.includeComputerPlayer) {
            isAITurnPending = true;
        } else {
            isAITurnPending = false;
        }
    }

    private static int calculateNextPlayerIndex(int currentIndex, boolean isClockwise, boolean applySkip, int numPlayers) {
        if (numPlayers <= 0) return 0; // Should not happen
        int direction = isClockwise ? 1 : -1;
        int nextIndex = (currentIndex + direction + numPlayers) % numPlayers;
        if (applySkip) {
            nextIndex = (nextIndex + direction + numPlayers) % numPlayers; // Skip this player
        }
        return nextIndex;
    }

    @Override
    public void render(Graphics g) {
        int windowWidth = getGame().getWidth();
        int windowHeight = getGame().getHeight();

        // Adjusted Y positions and added some X padding for elements near edges
        int topMargin = 50; // Increased top margin for UI elements
        int leftMargin = 30; // General left margin
        int rightMargin = 30; // General right margin

        drawBounds.setBounds(windowWidth - 150 - rightMargin, windowHeight - CARD_HEIGHT - 100, 120, 40);
        // Adjusted "Back to Menu" button position
        backToMenuBounds.setBounds(windowWidth - 150 - rightMargin, topMargin - 20 , 150, 40);


        g.setColor(new Color(40, 44, 52));
        g.fillRect(0, 0, windowWidth, windowHeight);

        if (gameOver) {
            renderGameOverScreen(g, windowWidth, windowHeight); // Ensure this method also respects margins if needed
            return;
        }

        // --- Central elements: Deck and Top Card ---
        int cardCenterX = windowWidth / 2 - CARD_WIDTH / 2;
        int cardCenterY = windowHeight / 2 - CARD_HEIGHT / 2 - 50; // Positioned slightly up from true center

        // Deck Image
        int deckImageX = cardCenterX - CARD_WIDTH - 30; // X-coordinate of the deck image
        g.setColor(new Color(30, 34, 42));
        g.fillRoundRect(deckImageX, cardCenterY, CARD_WIDTH, CARD_HEIGHT, 10, 10);

        // Deck Text (Repositioned to be centered under the deck image)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String deckTextString = "Deck: " + deck.remainingCards();
        FontMetrics deckFm = g.getFontMetrics();
        int deckTextWidth = deckFm.stringWidth(deckTextString);
        int deckTextX = deckImageX + (CARD_WIDTH / 2) - (deckTextWidth / 2); // Centered under deck image
        int deckTextY = cardCenterY + CARD_HEIGHT + 15; // Below deck image
        g.drawString(deckTextString, deckTextX, deckTextY);

        // Top Card (Discard Pile)
        if (topCard != null) {
            topCard.render(g, cardCenterX, cardCenterY, CARD_WIDTH, CARD_HEIGHT);
            if (topCard.getColor() == CardColor.GOLD) {
                // Display chosen wild color next to the top card
                g.setColor(currentWildColor.getAwtColor());
                g.setFont(new Font("Arial", Font.BOLD, 12));
                String wildColorText = "Color: " + currentWildColor.name();
                FontMetrics wildFm = g.getFontMetrics();
                int wildTextX = cardCenterX + (CARD_WIDTH/2) - (wildFm.stringWidth(wildColorText)/2) ; // Centered under top card
                g.drawString(wildColorText, wildTextX, cardCenterY + CARD_HEIGHT + 15);

                // Optional: Draw a small swatch of the chosen color next to the text or top card
                g.fillRect(cardCenterX + CARD_WIDTH + 10, cardCenterY + CARD_HEIGHT - 15, 15, 15);
                g.setColor(Color.BLACK);
                g.drawRect(cardCenterX + CARD_WIDTH + 10, cardCenterY + CARD_HEIGHT - 15, 15, 15);
            }
        }

        // --- Current Player's Hand (Bottom) ---
        Player currentHumanPlayer = players.get(currentPlayerIndex);
        if (!currentHumanPlayer.isComputer()) {
            List<Card> hand = currentHumanPlayer.getHand();
            if (currentPlayerCardBounds != null) { // Ensure bounds are initialized
                for (int i = 0; i < hand.size(); i++) {
                    if (i < currentPlayerCardBounds.length) { // Safety check
                        Card card = hand.get(i);
                        card.setFaceUp(true);
                        boolean canPlayWild = card.getColor() == CardColor.GOLD;
                        boolean matchesNormally = card.matches(topCard);
                        boolean matchesWildColorChoice = topCard.getColor() == CardColor.GOLD && card.matches(currentWildColor);
                        card.setHighlighted(canPlayWild || matchesNormally || matchesWildColorChoice);
                        // Use effectiveWidth/Height from updateCurrentPlayerCardBounds if they differ from CARD_WIDTH/HEIGHT
                        card.render(g, currentPlayerCardBounds[i].x, currentPlayerCardBounds[i].y, currentPlayerCardBounds[i].width, currentPlayerCardBounds[i].height);
                    }
                }
            }
        }

        // --- Opponent Information (Top Area) ---
        int opponentInfoBaseY = topMargin + 10; // Start opponent info below the top margin
        int opponentInfoXStart = leftMargin;
        int opponentInfoXIncrement = 180; // Horizontal spacing for multiple opponents
        int opponentCardOffsetY = 20; // Vertical offset for cards below name
        int opponentCardMiniWidth = CARD_WIDTH / 2;
        int opponentCardMiniHeight = CARD_HEIGHT / 2;

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            // Skip rendering the current human player here, as their hand is shown at the bottom
            if (i == currentPlayerIndex && !p.isComputer()) continue;

            // Basic horizontal layout for opponents, might need adjustment for many players
            int displayX = opponentInfoXStart + (i % 4) * opponentInfoXIncrement;
            // Basic vertical layout for opponents if more than 4
            int displayY = opponentInfoBaseY + (i / 4) * (opponentCardMiniHeight + 40 + opponentCardOffsetY);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString(p.getName() + ": " + p.handSize() + " cards", displayX, displayY);

            if (i == currentPlayerIndex) { // Highlight if it's this opponent's (AI) turn
                g.setColor(Color.YELLOW);
                // Adjust highlight box to fit text and a few cards
                g.drawRect(displayX - 5, displayY - 20, 170, 25 + opponentCardMiniHeight + 5);
            }

            // Draw face-down cards for opponents
            for (int j = 0; j < p.handSize(); j++) {
                if (j < 5) { // Limit visible opponent cards to avoid clutter for each opponent
                    Card backCard = new Card(CardColor.RED, 0); // Dummy card for back rendering
                    backCard.setFaceUp(false);
                    backCard.render(g, displayX + j * (opponentCardMiniWidth / 3 + 2), displayY + opponentCardOffsetY, opponentCardMiniWidth, opponentCardMiniHeight);
                } else {
                    g.setFont(new Font("Arial", Font.PLAIN, 10));
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawString("+" + (p.handSize() - j) + "", displayX + j * (opponentCardMiniWidth / 3 + 2), displayY + opponentCardOffsetY + opponentCardMiniHeight / 2 + 5);
                    break; // Stop after showing "+X more"
                }
            }
        }

        // --- UI Buttons ---
        drawButton.render(g, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
        backToMenuButton.render(g, backToMenuBounds.x, backToMenuBounds.y, backToMenuBounds.width, backToMenuBounds.height);

        // --- Message Display (Centered, below top card area) ---
        if (messageTimer > 0 && message !=null && !message.trim().isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics msgFm = g.getFontMetrics();
            int messageWidth = msgFm.stringWidth(message);
            int messageX = (windowWidth - messageWidth) / 2;
            // Position message clearly below the central card elements
            int messageY = cardCenterY + CARD_HEIGHT + 45;

            // Background for message for better readability
            g.setColor(new Color(0,0,0,180));
            g.fillRoundRect(messageX - 10, messageY - msgFm.getAscent() - 5, messageWidth + 20, msgFm.getHeight() + 10, 15, 15);
            g.setColor(Color.YELLOW);
            g.drawString(message, messageX, messageY);
        }

        // --- Current Turn Indicator (Top-Left, with increased margin) ---
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        if (currentPlayerIndex < players.size()) { // Ensure currentPlayerIndex is valid
            g.drawString(players.get(currentPlayerIndex).getName() + "'s Turn", leftMargin, topMargin);
        }
    }

    private void renderGameOverScreen(Graphics g, int windowWidth, int windowHeight) {
        // ... (renderGameOverScreen remains mostly the same as your last good version) ...
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
        String winnerMsgText = winnerName != null ? winnerName + " Wins!" : "It's a Draw!"; // Handle no winner
        textX = (windowWidth - fm.stringWidth(winnerMsgText)) / 2;
        g.setColor(Color.BLACK);
        g.drawString(winnerMsgText, textX + 2, 220 + 2);
        g.setColor(Color.GREEN);
        g.drawString(winnerMsgText, textX, 220);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        int scoreY = 280;
        if (!players.isEmpty()) { // Check if players list is initialized
            for(Player p : players) {
                String scoreText = p.getName() + ": " + p.handSize() + " cards remaining";
                fm = g.getFontMetrics();
                textX = (windowWidth - fm.stringWidth(scoreText)) / 2;
                g.drawString(scoreText, textX, scoreY);
                scoreY += 30;
            }
        }

        int buttonX = (windowWidth - backToMenuBounds.width) / 2;
        int buttonY = Math.max(scoreY + 20, windowHeight - 100); // Position below scores or at bottom
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

        if (currentActivePlayer.isComputer() && isAITurnPending) { // AI is thinking or about to act
            if (e.getID() == MouseEvent.MOUSE_CLICKED && backToMenuBounds.contains(mouse)) { // Allow exiting during AI turn
                getGame().setState(new MenuState(getGame()));
            }
            return; // Block other interactions during AI's pending turn
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
                            // Message is set inside handlePlayedCard or promptForColorSelection for wilds
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
        if (drawButton != null) { // Null check for safety
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
    public void onExit() { /* Nothing special */ }

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
        // Message updated by caller (promptForColorSelection or handlePlayedCard)
    }

    public void promptForColorSelection() {
        Player p = players.get(currentPlayerIndex);
        CardColor chosenColor;
        if (p.isComputer()) {
            // AI's color (this.currentWildColor) should be pre-determined in processAITurn.
            // If not, this is a fallback.
            chosenColor = (this.currentWildColor != null && this.currentWildColor != CardColor.GOLD) ? this.currentWildColor : findMostCommonColorInHand(p, true);
        } else {
            // Human player: Needs UI. Auto-selects for now.
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

        // Prioritize non-GOLD colors unless it's for AI choosing its wild color benefit
        for (CardColor c : CardColor.values()) {
            if (c == CardColor.GOLD && !forAIWildChoice) continue;
            int count = counts.getOrDefault(c, 0);
            if (count > max) {
                max = count;
                mostCommon = c;
            }
        }

        if (mostCommon == null || max <=0) { // If hand is empty, only gold, or no clear majority
            // Fallback: pick any non-GOLD color if possible, or a default
            for (Card card : player.getHand()) {
                if (card.getColor() != CardColor.GOLD) return card.getColor();
            }
            return CardColor.BLUE; // Absolute fallback
        }
        return mostCommon;
    }
}