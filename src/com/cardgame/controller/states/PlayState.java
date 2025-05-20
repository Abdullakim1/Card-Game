package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.model.card.Card;
import com.cardgame.model.card.Deck;
import com.cardgame.model.player.Player;
import com.cardgame.model.player.PlayerManager;
import com.cardgame.view.components.ModernButton;
import com.cardgame.model.game.GameOutcome;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PlayState extends GameState {
    private List<Player> players;
    private int currentPlayerIndex;
    private Deck deck;
    private Card topCard;
    private boolean skipNextTurn;
    private boolean gameOver;
    private Player winner;
    private Player loser;
    private ModernButton drawButton;
    private Rectangle drawBounds;
    private ModernButton backToMenuButton;
    private Rectangle backToMenuBounds;
    private Rectangle[] cardBounds;
    private String message;
    private int messageTimer;
    private boolean showingOutcome;
    private int outcomeAnimationTimer;
    private static final int OUTCOME_ANIMATION_DURATION = 300; // 5 seconds at 60 FPS
    private int direction = 1; // 1 for clockwise, -1 for counter-clockwise
    private boolean includeComputer; // Flag to track if we're including a computer player

    public PlayState(Game game) {
        super(game);
        players = new ArrayList<>();
        players.add(new Player("Player", false));
        players.add(new Player("Computer", true));
        initializeGame();
    }

    public PlayState(Game game, List<String> playerNames, boolean includeComputer) {
        super(game);
        players = new ArrayList<>();
        this.includeComputer = includeComputer;

        // Add human players
        for (String name : playerNames) {
            players.add(new Player(name, false));
        }
        
        // Add computer player if requested
        if (includeComputer) {
            players.add(new Player("Computer", true));
        }
        
        initializeGame();
    }
    
    /**
     * Constructor that accepts a PlayerManager
     * @param game The game instance
     * @param playerManager The player manager containing all players
     */
    public PlayState(Game game, PlayerManager playerManager) {
        super(game);
        players = new ArrayList<>(playerManager.getPlayers());
        initializeGame();
    }

    private void initializeGame() {
        deck = new Deck();

        // Deal 7 cards to each player
        for (Player player : players) {
            player.addCards(deck.draw(7));
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

        currentPlayerIndex = 0;
        skipNextTurn = false;
        gameOver = false;
        winner = null;
        loser = null;
        showingOutcome = false;
        outcomeAnimationTimer = 0;
        message = players.get(0).getName() + "'s turn! Match the color or number";
        messageTimer = 120;

        // Initialize UI elements
        drawButton = new ModernButton("Draw Card");
        drawBounds = new Rectangle(650, 400, 120, 40);
        backToMenuButton = new ModernButton("Back to Menu");
        backToMenuBounds = new Rectangle(650, 500, 120, 40);
        cardBounds = new Rectangle[7]; // Initial size for 7 cards
        updateCardBounds();
    }

    private void updateCardBounds() {
        // Show cards for the current player (all players are human in multiplayer mode)
        Player currentPlayer = getCurrentPlayer();
        List<Card> playerHand = currentPlayer.getHand();
        int cardWidth = 80;
        int cardHeight = 120;
        int spacing = 20;
        int startX = (800 - (playerHand.size() * (cardWidth + spacing) - spacing)) / 2;
        int y = 400;

        cardBounds = new Rectangle[playerHand.size()];
        for (int i = 0; i < playerHand.size(); i++) {
            cardBounds[i] = new Rectangle(startX + i * (cardWidth + spacing), y, cardWidth, cardHeight);
        }
    }

    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    private void nextPlayer() {
        // Move to the next player in the direction of play
        currentPlayerIndex = (currentPlayerIndex + direction + players.size()) % players.size();
        message = getCurrentPlayer().getName() + "'s turn";
        messageTimer = 60;
        updateCardBounds();
    }

    @Override
    public void tick() {
        if (messageTimer > 0) {
            messageTimer--;
        }
        
        if (gameOver) {
            if (outcomeAnimationTimer < OUTCOME_ANIMATION_DURATION) {
                outcomeAnimationTimer++;
                if (outcomeAnimationTimer == 1) {
                    // Start showing the outcome animation on the first frame
                    showingOutcome = true;
                    // Reset the animation frame to trigger the roulette effect
                    GameOutcome.resetAnimation();
                }
            }
            return;
        }
        
        // Handle computer turns
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer.isComputer() && !gameOver) {
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
        if (gameOver) return; // Safety check

        Player computer = getCurrentPlayer();
        if (!computer.isComputer()) return; // Another safety check

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

        // Second priority: Block players who are close to winning
        if (playIndex == -1) {
            // Check if any player has 2 or fewer cards
            boolean threatExists = false;
            for (Player p : players) {
                if (p != computer && p.handSize() <= 2) {
                    threatExists = true;
                    break;
                }
            }

            if (threatExists) {
                for (int i = 0; i < computerHand.size(); i++) {
                    Card card = computerHand.get(i);
                    if (card.matches(topCard)) {
                        playedCard = card;
                        playIndex = i;
                        break;
                    }
                }
            }
        }

        // Third priority: Play any matching card
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

        // If we found a card to play, play it
        if (playIndex != -1) {
            playedCard = computer.playCard(playIndex);
            handlePlayedCard(playedCard);
            message = computer.getName() + " played " + playedCard.getColor() +
                    (playedCard.isSpecial() ? " special card" : " " + playedCard.getValue());
            messageTimer = 60;
        } else {
            // If no playable card, draw a card
            Card drawnCard = deck.draw();
            if (drawnCard != null) {
                computer.addCard(drawnCard);
                message = computer.getName() + " drew a card";
                messageTimer = 60;
                
                // Check if the drawn card can be played
                Card drawnCardRef = computer.getHand().get(computer.handSize() - 1);
                if (drawnCardRef.matches(topCard)) {
                    // If the drawn card matches, play it immediately
                    playedCard = computer.playCard(computer.handSize() - 1);
                    handlePlayedCard(playedCard);
                    message = computer.getName() + " drew and played " + playedCard.getColor() +
                            (playedCard.isSpecial() ? " special card" : " " + playedCard.getValue());
                    messageTimer = 60;
                } else {
                    // Only end turn if the drawn card can't be played
                    nextPlayer(); // Move to next player after drawing
                }
            } else {
                message = "No cards left to draw!";
                messageTimer = 60;
                nextPlayer();
            }
        }

        // Check if computer has won
        if (computer.handSize() == 0) {
            gameOver = true;
            winner = computer;

            // Find the player with the most cards as the loser
            Player worstPlayer = null;
            int maxCards = -1;
            for (Player p : players) {
                if (p != computer && p.handSize() > maxCards) {
                    maxCards = p.handSize();
                    worstPlayer = p;
                }
            }
            loser = worstPlayer;

            message = computer.getName() + " wins! " +
                    (loser != null ? loser.getName() + " gets punished!" : "");
            messageTimer = 300;
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
                case RED: {
                    // Skip next player's turn
                    skipNextTurn = true;
                    message = "Skip turn!";
                    messageTimer = 60;
                    nextPlayer(); // Skip to the player after the next
                    break;
                }
                case BLUE: {
                    // Reverse direction (matters in multiplayer)
                    direction *= -1; // Flip the direction
                    message = "Direction reversed!";
                    messageTimer = 60;
                    break;
                }
                case GREEN: {
                    // Draw 2 cards for the next player
                    int nextPlayerIdx = (currentPlayerIndex + direction + players.size()) % players.size();
                    Player nextPlayer = players.get(nextPlayerIdx);
                    nextPlayer.addCards(deck.draw(2));
                    message = nextPlayer.getName() + " draws 2 cards!";
                    messageTimer = 60;
                    break;
                }
                case GOLD: {
                    // Wild card - no special effect
                    message = "Wild card played!";
                    messageTimer = 60;
                    break;
                }
            }
        }

        // Check if game is over
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer.handSize() == 0) {
            gameOver = true;
            winner = currentPlayer;
            
            // Find the player with the most cards as the loser
            Player worstPlayer = null;
            int maxCards = -1;
            for (Player p : players) {
                if (p != currentPlayer && p.handSize() > maxCards) {
                    maxCards = p.handSize();
                    worstPlayer = p;
                }
            }
            loser = worstPlayer;
            
            message = currentPlayer.getName() + " wins! " + 
                    (loser != null ? loser.getName() + " gets punished!" : "");
            messageTimer = 300;
            return;
        }
        
        // Move to the next player if the game isn't over
        if (!skipNextTurn) {
            nextPlayer();
        } else {
            skipNextTurn = false;
        }
    }

    @Override
    public void render(Graphics g) {
        // Draw background
        g.setColor(new Color(40, 44, 52));
        g.fillRect(0, 0, 800, 600);

        if (gameOver) {
            // Draw game over screen
            g.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm = g.getFontMetrics();
            String gameOverText = "Game Over!";
            int textX = (800 - fm.stringWidth(gameOverText)) / 2;

            // Draw text shadow
            g.setColor(new Color(0, 0, 0, 100));
            g.drawString(gameOverText, textX + 2, 150 + 2);

            // Draw main text
            g.setColor(Color.WHITE);
            g.drawString(gameOverText, textX, 150);

            // Draw winner announcement
            g.setFont(new Font("Arial", Font.BOLD, 32));
            fm = g.getFontMetrics();
            String winnerText = winner.getName() + " Wins!";
            textX = (800 - fm.stringWidth(winnerText)) / 2;
            g.drawString(winnerText, textX, 200);

            // Draw final scores
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            int scoreY = 240;
            for (Player p : players) {
                String scoreText = p.getName() + ": " + (7 - p.handSize()) + " points";
                fm = g.getFontMetrics();
                textX = (800 - fm.stringWidth(scoreText)) / 2;
                g.drawString(scoreText, textX, scoreY);
                scoreY += 30;
            }

            // If there's a loser, show the punishment animation
            if (loser != null && showingOutcome) {
                g.setFont(new Font("Arial", Font.BOLD, 24));
                String punishmentText = loser.getName() + "'s Punishment:";
                fm = g.getFontMetrics();
                textX = (800 - fm.stringWidth(punishmentText)) / 2;
                g.drawString(punishmentText, textX, 320);
                
                // Draw the outcome animation frame
                BufferedImage currentFrame = GameOutcome.getRandomOutcomeImage();
                if (currentFrame != null) {
                    int imgWidth = Math.min(currentFrame.getWidth(), 300);
                    int imgHeight = Math.min(currentFrame.getHeight(), 200);
                    int imgX = (800 - imgWidth) / 2;
                    g.drawImage(currentFrame, imgX, 340, imgWidth, imgHeight, null);
                }
            }
            
            // Draw back to menu button
            backToMenuButton.render(g, backToMenuBounds.x, backToMenuBounds.y, backToMenuBounds.width, backToMenuBounds.height);
            return;
        }
        
        // Draw deck
        g.setColor(new Color(30, 34, 42));
        g.fillRoundRect(650, 200, 80, 120, 10, 10);

        // Draw top card
        if (topCard != null) {
            topCard.render(g, 550, 200, 80, 120);
        }

        // Draw current player indicator with a more visible highlight
        Player currentPlayer = getCurrentPlayer();
        g.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Create a background highlight for the current player
        String turnText = currentPlayer.getName() + "'s Turn";
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(turnText);
        
        // Draw highlight background
        g.setColor(new Color(65, 105, 225, 180)); // Semi-transparent blue
        g.fillRoundRect(15, 25, textWidth + 20, 35, 10, 10);
        
        // Draw border
        g.setColor(new Color(100, 140, 255));
        ((Graphics2D)g).setStroke(new BasicStroke(2));
        g.drawRoundRect(15, 25, textWidth + 20, 35, 10, 10);
        
        // Draw text
        g.setColor(Color.WHITE);
        g.drawString(turnText, 25, 50);

        // Draw all players' hands
        int playerInfoX = 20;
        int playerInfoY = 80;
        int playerInfoSpacing = 30;

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            
            // Skip computer players in the display for human-only multiplayer
            if (p.isComputer() && !includeComputer) {
                continue;
            }
            
            g.setFont(new Font("Arial", Font.BOLD, 18));

            // Highlight current player in the list
            if (i == currentPlayerIndex) {
                g.setColor(new Color(65, 105, 225, 100)); // Matching blue highlight
                g.fillRoundRect(playerInfoX - 5, playerInfoY - 20, 200, 25, 5, 5);
            }

            g.setColor(Color.WHITE);
            g.drawString(p.getName() + "'s Hand: " + p.handSize() + " cards", playerInfoX, playerInfoY);

            // If this is not the current player, draw cards face down (all players are human in multiplayer)
            if (i != currentPlayerIndex) {
                int cardWidth = 40;
                int cardHeight = 60;
                int cardSpacing = 10;
                int startX = playerInfoX;
                int y = playerInfoY + 10;

                for (int j = 0; j < p.handSize(); j++) {
                    // Draw face down card
                    g.setColor(new Color(30, 34, 42));
                    g.fillRoundRect(startX + j * cardSpacing, y, cardWidth, cardHeight, 10, 10);
                    g.setColor(Color.WHITE);
                    g.drawRoundRect(startX + j * cardSpacing, y, cardWidth, cardHeight, 10, 10);
                }
            }

            playerInfoY += playerInfoSpacing + 70; // Move to next player position
        }

        // Draw current player's hand (if human)
        if (!currentPlayer.isComputer()) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(Color.WHITE);
            g.drawString("Your Hand:", 20, 380);

            List<Card> playerHand = currentPlayer.getHand();
            for (int i = 0; i < playerHand.size() && i < cardBounds.length; i++) {
                Card card = playerHand.get(i);
                card.setFaceUp(true); // Player can see their own cards
                card.render(g, cardBounds[i].x, cardBounds[i].y, cardBounds[i].width, cardBounds[i].height);
            }
        }

        // Draw UI elements
        drawButton.render(g, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
        backToMenuButton.render(g, backToMenuBounds.x, backToMenuBounds.y, backToMenuBounds.width, backToMenuBounds.height);

        // Draw message
        if (messageTimer > 0) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(message, (800 - fm.stringWidth(message)) / 2, 300);
        }

        // Draw deck count
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Deck: " + deck.remainingCards(), 650, 180);
    }

    @Override
    public void handleMouseEvent(MouseEvent e) {
        Point mouse = e.getPoint();

        if (gameOver) {
            // Handle game over screen interactions
            if (e.getID() == MouseEvent.MOUSE_MOVED) {
                backToMenuButton.setHovered(backToMenuBounds.contains(mouse));
                return;
            }

            if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                if (backToMenuBounds.contains(mouse)) {
                    backToMenuButton.setPressed(true);
                }
                return;
            }

            if (e.getID() == MouseEvent.MOUSE_RELEASED || e.getID() == MouseEvent.MOUSE_CLICKED) {
                if (backToMenuBounds.contains(mouse)) {
                    game.setState(new MenuState(game));
                }
                return;
            }
        }

        // Only handle player interactions during their turn and if current player is human
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer.isComputer()) return;

        if (e.getID() == MouseEvent.MOUSE_MOVED) {
            drawButton.setHovered(drawBounds.contains(mouse));
            backToMenuButton.setHovered(backToMenuBounds.contains(mouse));
            return;
        }

        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            if (drawBounds.contains(mouse)) {
                drawButton.setPressed(true);
            } else if (backToMenuBounds.contains(mouse)) {
                backToMenuButton.setPressed(true);
            }
            return;
        }

        if (e.getID() == MouseEvent.MOUSE_RELEASED || e.getID() == MouseEvent.MOUSE_CLICKED) {
            // Reset pressed states
            drawButton.setPressed(false);
            backToMenuButton.setPressed(false);

            if (backToMenuBounds.contains(mouse)) {
                game.setState(new MenuState(game));
                return;
            }

            if (drawBounds.contains(mouse)) {
                Card drawnCard = deck.draw();
                if (drawnCard != null) {
                    currentPlayer.addCard(drawnCard);
                    message = currentPlayer.getName() + " drew a card";
                    messageTimer = 60;
                    
                    // Check if the drawn card can be played
                    Card drawnCardRef = currentPlayer.getHand().get(currentPlayer.handSize() - 1);
                    if (drawnCardRef.matches(topCard)) {
                        message += " - Card can be played!";
                    } else {
                        // Only end turn if the drawn card can't be played
                        nextPlayer();  // End player's turn after drawing
                    }
                    updateCardBounds();
                } else {
                    message = "No cards left to draw!";
                    messageTimer = 60;
                    nextPlayer();
                }
                return;
            }

            // Handle card clicks
            List<Card> playerHand = currentPlayer.getHand();
            for (int i = 0; i < cardBounds.length && i < playerHand.size(); i++) {
                if (cardBounds[i].contains(mouse)) {
                    Card selectedCard = playerHand.get(i);
                    if (selectedCard.matches(topCard)) {
                        Card played = currentPlayer.playCard(i);
                        handlePlayedCard(played);
                        updateCardBounds();
                        message = currentPlayer.getName() + " played " + played.getColor() +
                                (played.isSpecial() ? " special card" : " " + played.getValue());
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
        
        // Load outcome images
        GameOutcome.loadOutcomeImages();
    }
    
    @Override
    public void onExit() {
        // Nothing special needed
    }
}
