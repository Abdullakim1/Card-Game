package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.model.card.Card;
import com.cardgame.model.card.Card.CardColor;
import com.cardgame.model.card.Deck;
import com.cardgame.model.player.Player;
import com.cardgame.view.components.ModernButton;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.cardgame.model.game.GameOutcome;

public class PlayState extends GameState {
    private Player player;
    private Player computer;
    private Deck deck;
    private Card topCard;
    private boolean playerTurn;
    private boolean skipNextTurn;
    private boolean gameOver;
    private String winner;
    private boolean directionClockwise = true;
    private CardColor currentColor;
    private ModernButton drawButton;
    private Rectangle drawBounds;
    private ModernButton backToMenuButton;
    private Rectangle backToMenuBounds;
    private Rectangle[] cardBounds;
    private String message;
    private int messageTimer;
    
    // Game outcome animation
    private boolean showOutcomeAnimation;
    private BufferedImage outcomeImage;
    private boolean outcomeInitialized = false;

    public PlayState(Game game) {
        super(game);
        initializeGame();
    }
    
    /**
     * Creates a new play state with specified player names and computer player option
     * 
     * @param game The game instance
     * @param playerNames List of player names
     * @param includeComputer Whether to include a computer player
     */
    public PlayState(Game game, List<String> playerNames, boolean includeComputer) {
        super(game);
        initializeGame(playerNames, includeComputer);
    }

    private void initializeGame() {
        List<String> defaultNames = new ArrayList<>();
        defaultNames.add("Player");
        initializeGame(defaultNames, true);
    }
    
    /**
     * Initializes the game with specified player names and computer player option
     * 
     * @param playerNames List of player names
     * @param includeComputer Whether to include a computer player
     */
    private void initializeGame(List<String> playerNames, boolean includeComputer) {
        deck = new Deck();
        
        // Initialize animation variables
        showOutcomeAnimation = false;
        outcomeImage = null;
        outcomeInitialized = false;
        
        // Load the outcome animations
        try {
            GameOutcome.loadOutcomeImages();
            outcomeInitialized = true;
            System.out.println("GameOutcome animations loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading GameOutcome animations: " + e.getMessage());
            e.printStackTrace();
        }
        
        if (includeComputer) {
            // Single player vs computer mode
            player = new Player(playerNames.get(0), false);
            computer = new Player("Computer", true);
            
            // Deal 7 cards to each player
            player.addCards(deck.draw(7));
            computer.addCards(deck.draw(7));
        } else {
            // Human vs human mode
            player = new Player(playerNames.get(0), false);
            computer = new Player(playerNames.size() > 1 ? playerNames.get(1) : "Player 2", false);
            
            // Deal 7 cards to each player
            player.addCards(deck.draw(7));
            computer.addCards(deck.draw(7));
        }
        
        // Place first card face up
        topCard = deck.draw();
        while (topCard != null && topCard.isSpecial()) {
            deck.discard(topCard);
            topCard = deck.draw();
        }
        if (topCard != null) {
            topCard.setFaceUp(true);
        }
        
        playerTurn = true;
        skipNextTurn = false;
        gameOver = false;
        winner = null;
        message = "Your turn! Match the color or number";
        messageTimer = 120;
        
        // Initialize UI elements
        drawButton = new ModernButton("Draw Card");
        drawBounds = new Rectangle(0, 0, 120, 40);
        backToMenuButton = new ModernButton("Back to Menu");
        backToMenuBounds = new Rectangle(0, 0, 120, 40);
        cardBounds = new Rectangle[7]; // Initial size for 7 cards
        updateCardBounds();
    }

    private void updateCardBounds() {
        // Get current window dimensions
        int windowWidth = getGame().getWidth();
        int windowHeight = getGame().getHeight();
        
        List<Card> playerHand = player.getHand();
        int cardWidth = 80;
        int cardHeight = 120;
        int spacing = 20;
        int startX = (windowWidth - (playerHand.size() * (cardWidth + spacing) - spacing)) / 2;
        int y = windowHeight - 200;

        cardBounds = new Rectangle[playerHand.size()];
        for (int i = 0; i < playerHand.size(); i++) {
            cardBounds[i] = new Rectangle(startX + i * (cardWidth + spacing), y, cardWidth, cardHeight);
        }
    }

    @Override
    public void tick() {
        // Update message timer
        if (messageTimer > 0) {
            messageTimer--;
        }
        
        // Update outcome animation if active
        if (showOutcomeAnimation && gameOver) {
            try {
                outcomeImage = GameOutcome.getRandomOutcomeImage();
                if (outcomeImage != null) {
                    System.out.println("Animation frame updated: " + outcomeImage.getWidth() + "x" + outcomeImage.getHeight());
                } else {
                    System.out.println("Warning: Animation frame is null");
                }
            } catch (Exception e) {
                System.err.println("Error updating animation: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Handle computer's turn
        if (!playerTurn && !gameOver) {
            // Add a small delay before computer plays
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handleComputerTurn();
        }
    }

    private void handleComputerTurn() {
        if (playerTurn || gameOver) return; // Safety check

        List<Card> computerHand = computer.getHand();
        Card playedCard = null;
        int playIndex = -1;

        // First priority: Win the game if possible
        for (int i = 0; i < computerHand.size(); i++) {
            Card card = computerHand.get(i);
            if (card.matches(topCard) && computerHand.size() == 1) {
                playedCard = card;
                playIndex = i;
                break;
            }
        }

        // Second priority: Play a skip or reverse card if available
        if (playIndex == -1) {
            for (int i = 0; i < computerHand.size(); i++) {
                Card card = computerHand.get(i);
                if (card.matches(topCard) && card.isSpecial() && 
                    (card.getColor() == CardColor.RED || card.getColor() == CardColor.GREEN)) {
                    playedCard = card;
                    playIndex = i;
                    break;
                }
            }
        }

        // Third priority: Block player from winning or counter their advantage
        if (playIndex == -1 && player.handSize() <= 2) {
            for (int i = 0; i < computerHand.size(); i++) {
                Card card = computerHand.get(i);
                if (card.matches(topCard)) {
                    playedCard = card;
                    playIndex = i;
                    break;
                }
            }
        }

        // Fourth priority: Play any matching card
        if (playIndex == -1) {
            for (int i = 0; i < computerHand.size(); i++) {
                Card card = computerHand.get(i);
                if (card.matches(topCard)) {
                    playedCard = card;
                    playIndex = i;
                    break;
                }
            }
        }

        if (playIndex != -1) {
            // Play the card
            playedCard = computer.playCard(playIndex);
            
            // Store the current player turn state before handling the card
            boolean wasComputerTurn = !playerTurn;
            
            // Handle the played card
            handlePlayedCard(playedCard);
            
            // For special cards (RED skip or GREEN reverse), keep the turn with the computer
            if (playedCard.isSpecial()) {
                if (playedCard.getColor() == CardColor.RED || playedCard.getColor() == CardColor.GREEN) {
                    playerTurn = false; // Keep computer's turn
                    message = "Computer played " + playedCard.getColor() + " special card and gets another turn!";
                } else {
                    playerTurn = true; // Switch to player's turn for other special cards
                }
            } else {
                playerTurn = true; // Switch to player's turn for normal cards
            }
            
            messageTimer = 60;
        } else {
            // Draw a card only if we have no playable cards
            Card drawnCard = deck.draw();
            if (drawnCard != null) {
                computer.addCard(drawnCard);
                // Check if drawn card can be played
                if (drawnCard.matches(topCard)) {
                    playedCard = computer.playCard(computer.handSize() - 1);
                    
                    // Store the current player turn state before handling the card
                    boolean wasComputerTurn = !playerTurn;
                    
                    // Handle the played card
                    handlePlayedCard(playedCard);
                    
                    // For special cards (RED skip or GREEN reverse), keep the turn with the computer
                    if (playedCard.isSpecial()) {
                        if (playedCard.getColor() == CardColor.RED || playedCard.getColor() == CardColor.GREEN) {
                            playerTurn = false; // Keep computer's turn
                            message = "Computer drew and played " + playedCard.getColor() + " special card and gets another turn!";
                        } else {
                            playerTurn = true; // Switch to player's turn for other special cards
                        }
                    } else {
                        playerTurn = true; // Switch to player's turn for normal cards
                    }
                } else {
                    message = "Computer drew a card";
                    playerTurn = true;
                }
            } else {
                message = "No cards left to draw!";
                playerTurn = true;
            }
            messageTimer = 60;
        }
        
        updateCardBounds();
    }

    private void handlePlayedCard(Card played) {
        if (played == null) return;

        // Update top card
        if (topCard != null) {
            deck.discard(topCard);
        }
        topCard = played;
        topCard.setFaceUp(true);

        // Handle special card effects
        if (played.isSpecial()) {
            switch (played.getColor()) {
                case RED -> {
                    // RED = Skip card - player who played this gets another turn
                    message = playerTurn ? 
                        "Skip! " + computer.getName() + "'s turn skipped! You get another turn!" : 
                        "Skip! " + player.getName() + "'s turn skipped! Computer gets another turn!";
                    messageTimer = 60;
                    
                    // Don't change playerTurn - the current player gets another turn
                    // This is handled by keeping the current playerTurn value
                }
                case BLUE -> {
                    // BLUE = Draw 2 cards
                    Player target = playerTurn ? computer : player;
                    target.addCards(deck.draw(2));
                    message = target.getName() + " draws 2 cards!";
                    messageTimer = 60;
                }
                case GREEN -> {
                    // GREEN = Reverse direction - player who played this gets another turn
                    message = playerTurn ? 
                        "Reverse! Direction changed! You get another turn!" : 
                        "Reverse! Direction changed! Computer gets another turn!";
                    messageTimer = 60;
                    
                    // Don't change playerTurn - the current player gets another turn
                    // This is handled by keeping the current playerTurn value
                }
                case GOLD -> {
                    // GOLD = Wild card
                    message = "Wild card played!";
                    messageTimer = 60;
                }
            }
        }

        // Check for game over
        if (player.handSize() == 0) {
            gameOver = true;
            winner = player.getName();
            message = "Game Over! " + player.getName() + " wins!";
            messageTimer = Integer.MAX_VALUE;
            
            // Always show animation when the human player wins
            showOutcomeAnimation = true;
            try {
                GameOutcome.resetAnimation();
                System.out.println("Animation started for player win: " + player.getName());
                System.out.println("Is computer player: " + player.isComputer());
            } catch (Exception e) {
                System.err.println("Error starting animation: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (computer.handSize() == 0) {
            gameOver = true;
            winner = computer.getName();
            message = "Game Over! " + computer.getName() + " wins!";
            messageTimer = Integer.MAX_VALUE;
            
            // Always show animation when the computer wins
            showOutcomeAnimation = true;
            try {
                GameOutcome.resetAnimation();
                System.out.println("Animation started for computer win: " + computer.getName());
                System.out.println("Is computer: " + computer.isComputer());
            } catch (Exception e) {
                System.err.println("Error starting animation: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // Get current window dimensions
        int windowWidth = getGame().getWidth();
        int windowHeight = getGame().getHeight();
        
        // Update UI element positions
        updateCardBounds();
        int rightMargin = windowWidth - 150;
        drawBounds.setBounds(rightMargin, windowHeight - 200, 120, 40);
        backToMenuBounds.setBounds(rightMargin, windowHeight - 100, 120, 40);
        
        // Draw background
        g.setColor(new Color(40, 44, 52));
        g.fillRect(0, 0, windowWidth, windowHeight);

        if (gameOver) {
            // Draw game over screen
            
            // Draw outcome animation if active
            if (showOutcomeAnimation) {
                try {
                    if (outcomeImage != null) {
                        System.out.println("Drawing animation frame: " + outcomeImage.getWidth() + "x" + outcomeImage.getHeight());
                        
                        // Make the animation more prominent
                        int x = (windowWidth - outcomeImage.getWidth()) / 2;
                        int y = windowHeight / 2; // Center vertically
                        
                        // Draw a border around the animation to make it stand out
                        g.setColor(new Color(255, 215, 0)); // Gold border
                        g.fillRect(x - 10, y - 10, outcomeImage.getWidth() + 20, outcomeImage.getHeight() + 20);
                        
                        // Draw the animation
                        g.drawImage(outcomeImage, x, y, null);
                        
                        // Draw a label for the animation
                        g.setColor(Color.RED);
                        g.setFont(new Font("Arial", Font.BOLD, 20));
                        g.drawString("PUNISHMENT FOR LOSING:", x, y - 15);
                    } else {
                        // If outcomeImage is null, draw a placeholder
                        g.setColor(Color.RED);
                        g.setFont(new Font("Arial", Font.BOLD, 24));
                        g.drawString("PUNISHMENT LOADING...", 300, 350);
                        System.out.println("Drawing placeholder for null animation");
                    }
                } catch (Exception e) {
                    System.err.println("Error rendering animation: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Draw error message if animation fails
                    g.setColor(Color.RED);
                    g.setFont(new Font("Arial", Font.BOLD, 16));
                    g.drawString("Animation Error: " + e.getMessage(), 250, 350);
                }
            }
            
            g.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm = g.getFontMetrics();
            String gameOverText = "Game Over!";
            int textX = (windowWidth - fm.stringWidth(gameOverText)) / 2;
            
            // Draw text shadow
            g.setColor(new Color(0, 0, 0, 100));
            g.drawString(gameOverText, textX + 2, 200 + 2);
            
            // Draw main text
            g.setColor(Color.WHITE);
            g.drawString(gameOverText, textX, 200);
            
            // Draw winner announcement
            g.setFont(new Font("Arial", Font.BOLD, 32));
            fm = g.getFontMetrics();
            String winnerText = winner + " Wins!";
            textX = (windowWidth - fm.stringWidth(winnerText)) / 2;
            g.drawString(winnerText, textX, 280);
            
            // Draw final score
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String scoreText = "Final Score - " + player.getName() + ": " + (7 - player.handSize()) + " | " + computer.getName() + ": " + (7 - computer.handSize());
            fm = g.getFontMetrics();
            textX = (windowWidth - fm.stringWidth(scoreText)) / 2;
            g.drawString(scoreText, textX, 340);
            
            // Draw back to menu button centered at the bottom
            int buttonX = (windowWidth - backToMenuBounds.width) / 2;
            int buttonY = windowHeight - 100;
            backToMenuBounds.setBounds(buttonX, buttonY, backToMenuBounds.width, backToMenuBounds.height);
            backToMenuButton.render(g, backToMenuBounds.x, backToMenuBounds.y, backToMenuBounds.width, backToMenuBounds.height);
            return;
        }

        // Draw deck
        g.setColor(new Color(30, 34, 42));
        g.fillRoundRect(windowWidth - 100, windowHeight - 250, 80, 120, 10, 10);

        // Draw top card
        if (topCard != null) {
            topCard.render(g, windowWidth / 2 - 40, windowHeight / 2 - 60, 80, 120);
        }

        // Draw computer's cards face down
        List<Card> computerHand = computer.getHand();
        int opponentCardWidth = 60;
        int opponentCardHeight = 90;
        int opponentSpacing = 15;
        int opponentStartX = (windowWidth - (computerHand.size() * (opponentCardWidth + opponentSpacing) - opponentSpacing)) / 2;
        int opponentY = 150;
        for (int i = 0; i < computerHand.size(); i++) {
            g.setColor(new Color(30, 34, 42));
            g.fillRoundRect(opponentStartX + i * (opponentCardWidth + opponentSpacing), opponentY, opponentCardWidth, opponentCardHeight, 10, 10);
        }

        // Draw player's cards
        List<Card> playerHand = player.getHand();
        // Make sure cardBounds array matches the hand size
        if (cardBounds.length != playerHand.size()) {
            updateCardBounds();
        }
        for (int i = 0; i < playerHand.size(); i++) {
            Card card = playerHand.get(i);
            if (card != null && i < cardBounds.length) {
                card.setFaceUp(true);
                card.render(g, cardBounds[i].x, cardBounds[i].y, 
                           cardBounds[i].width, cardBounds[i].height);
            }
        }

        // Draw UI elements
        drawButton.render(g, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
        backToMenuButton.render(g, backToMenuBounds.x, backToMenuBounds.y, backToMenuBounds.width, backToMenuBounds.height);

        // Draw message
        if (messageTimer > 0) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(Color.YELLOW);
            FontMetrics fm = g.getFontMetrics();
            int messageX = (windowWidth - fm.stringWidth(message)) / 2;
            g.drawString(message, messageX, windowHeight / 2 - 100);
        }

        // Draw turn indicator
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String turnText = playerTurn ? player.getName() + "'s Turn" : computer.getName() + "'s Turn";
        g.drawString(turnText, 20, 30);

        // Draw player name and hand count
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        g.drawString(player.getName() + "'s Hand " + player.handSize() + " cards", 50, windowHeight - 220);
        g.drawString(computer.getName() + "'s Cards: " + computer.handSize(), 20, 80);
        g.drawString("Deck: " + deck.remainingCards(), windowWidth - 100, 180);
    }

    @Override
    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();

        if (gameOver) {
            // Handle game over screen interactions
            if (e.getID() == MouseEvent.MOUSE_MOVED) {
                backToMenuButton.setHovered(backToMenuBounds.contains(mouse));
            } else if (e.getID() == MouseEvent.MOUSE_CLICKED && backToMenuBounds.contains(mouse)) {
                getGame().setState(new MenuState(getGame()));
            }
            return;
        }
        
        // Only handle events during player's turn
        if (!playerTurn) return;
        
        // Handle hover effects
        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            drawButton.setHovered(drawBounds.contains(mouse));
            backToMenuButton.setHovered(backToMenuBounds.contains(mouse));
            return;
        }

        // Handle clicks
        if (e.getID() == MouseEvent.MOUSE_CLICKED) {
            // Check if draw button was clicked
            if (drawBounds.contains(mouse)) {
                Card drawnCard = deck.draw();
                if (drawnCard != null) {
                    player.addCard(drawnCard);
                    message = "You drew a card";
                    messageTimer = 60;
                    playerTurn = false;  // End player's turn after drawing
                    updateCardBounds();
                } else {
                    message = "No cards left to draw!";
                    messageTimer = 60;
                }
                return;
            }
            
            // Check if back to menu button was clicked
            if (backToMenuBounds.contains(mouse)) {
                getGame().setState(new MenuState(getGame()));
                return;
            }
            
            // Check if a card was clicked
            for (int i = 0; i < cardBounds.length && i < player.getHand().size(); i++) {
                if (cardBounds[i].contains(mouse)) {
                    // Try to play the card
                    Card selectedCard = player.getHand().get(i);
                    if (selectedCard.matches(topCard)) {
                        Card playedCard = player.playCard(i);
                        handlePlayedCard(playedCard);
                        
                        // For special cards (RED skip or GREEN reverse), keep the turn with the player
                        if (playedCard.isSpecial() && 
                            (playedCard.getColor() == CardColor.RED || playedCard.getColor() == CardColor.GREEN)) {
                            playerTurn = true; // Player gets another turn
                        } else {
                            playerTurn = false; // Switch to computer's turn for other cards
                        }
                        
                        updateCardBounds();
                        message = "You played " + playedCard.getColor() +
                                (playedCard.isSpecial() ? " special card" : " " + playedCard.getValue());
                        messageTimer = 60;
                    } else {
                        message = "Card doesn't match! Match the color or number.";
                        messageTimer = 60;
                    }
                    break;
                }
            }
        }
    }
    @Override
    public void onEnter() {
        // Reset button states
        drawButton.setHovered(false);
        drawButton.setPressed(false);
        if (backToMenuButton != null) {
            backToMenuButton.setHovered(false);
            backToMenuButton.setPressed(false);
        }
    }

    @Override
    public void onExit() {
        // Nothing special needed
    }
    
    /**
     * Reverses the direction of play
     */
    public void reverseDirection() {
        directionClockwise = !directionClockwise;
        message = "Direction reversed!";
        messageTimer = 120;
        
        // Note: We don't change playerTurn here anymore
        // The player who played the reverse card gets another turn
        // This is handled in the handlePlayedCard method
    }
    
    /**
     * Gets the current deck
     * @return The game deck
     */
    public Deck getDeck() {
        return deck;
    }
    
    /**
     * Sets the flag to skip the next player's turn
     */
    public void skipNextTurn() {
        skipNextTurn = true;
        message = "Next turn skipped!";
        messageTimer = 120;
        
        // Note: We don't change playerTurn here anymore
        // The player who played the skip card gets another turn
        // This is handled in the handlePlayedCard method
    }
    
    /**
     * Sets the current color for wild card effects
     * @param color The new color to set
     */
    public void setCurrentColor(CardColor color) {
        this.currentColor = color;
        message = "Color changed to " + color.toString();
        messageTimer = 120;
    }
    
    /**
     * Prompts the player to select a color for wild cards
     */
    public void promptForColorSelection() {
        // This would typically show a UI for color selection
        // For now, we'll default to a color based on the player's hand
        CardColor mostCommonColor = findMostCommonColorInHand(player);
        setCurrentColor(mostCommonColor);
    }
    
    /**
     * Helper method to find the most common card color in a player's hand
     * @param player The player whose hand to analyze
     * @return The most common card color
     */
    private CardColor findMostCommonColorInHand(Player player) {
        // Count occurrences of each color
        int[] colorCounts = new int[CardColor.values().length];
        List<Card> playerCards = player.getCards();
        
        for (Card card : playerCards) {
            if (card.getColor() != CardColor.GOLD) { // Skip wild cards
                colorCounts[card.getColor().ordinal()]++;
            }
        }
        
        // Find the color with the highest count
        CardColor mostCommonColor = CardColor.RED; // Default
        int maxCount = 0;
        
        for (CardColor color : CardColor.values()) {
            if (color != CardColor.GOLD && colorCounts[color.ordinal()] > maxCount) {
                maxCount = colorCounts[color.ordinal()];
                mostCommonColor = color;
            }
        }
        
        return mostCommonColor;
    }
}
