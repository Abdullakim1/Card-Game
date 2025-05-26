package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.model.card.Card;
import com.cardgame.model.card.Card.CardColor;
import com.cardgame.model.card.Deck;
import com.cardgame.model.player.Player;
import com.cardgame.view.components.ModernButton;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class PlayState extends GameState {
    private Player player;
    private Player computer;
    private Deck deck;
    private Card topCard;
    private boolean playerTurn;
    private boolean skipNextTurn;
    private boolean gameOver;
    private String winner;
    private ModernButton drawButton;
    private Rectangle drawBounds;
    private ModernButton backToMenuButton;
    private Rectangle backToMenuBounds;
    private Rectangle[] cardBounds;
    private String message;
    private int messageTimer;

    public PlayState(Game game) {
        super(game);
        initializeGame();
    }

    private void initializeGame() {
        deck = new Deck();
        player = new Player("Player", false);
        computer = new Player("Computer", true);
        
        // Deal 7 cards to each player
        player.addCards(deck.draw(7));
        computer.addCards(deck.draw(7));
        
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
        drawBounds = new Rectangle(650, 400, 120, 40);
        backToMenuButton = new ModernButton("Back to Menu");
        backToMenuBounds = new Rectangle(650, 500, 120, 40);
        cardBounds = new Rectangle[7]; // Initial size for 7 cards
        updateCardBounds();
    }

    private void updateCardBounds() {
        List<Card> playerHand = player.getHand();
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

    @Override
    public void tick() {
        if (messageTimer > 0) {
            messageTimer--;
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

        // Second priority: Block player from winning or counter their advantage
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

        if (playIndex != -1) {
            // Play the card
            playedCard = computer.playCard(playIndex);
            handlePlayedCard(playedCard);
            if (!skipNextTurn) {
                playerTurn = true;
            }
            message = "Computer played " + playedCard.getColor() + 
                     (playedCard.isSpecial() ? " special card" : " " + playedCard.getValue());
            messageTimer = 60;
        } else {
            // Draw a card only if we have no playable cards
            Card drawnCard = deck.draw();
            if (drawnCard != null) {
                computer.addCard(drawnCard);
                // Check if drawn card can be played
                if (drawnCard.matches(topCard)) {
                    playedCard = computer.playCard(computer.handSize() - 1);
                    handlePlayedCard(playedCard);
                    if (!skipNextTurn) {
                        playerTurn = true;
                    }
                    message = "Computer drew and played " + playedCard.getColor() + 
                             (playedCard.isSpecial() ? " special card" : " " + playedCard.getValue());
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
        
        skipNextTurn = false;
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
                    skipNextTurn = true;
                    message = "Skip turn!";
                    messageTimer = 60;
                    // Skip turn means current player goes again
                    playerTurn = !playerTurn;
                }
                case BLUE -> {
                    Player target = playerTurn ? computer : player;
                    target.addCards(deck.draw(2));
                    message = "Draw 2 cards!";
                    messageTimer = 60;
                }
                case GREEN -> {
                    message = "Reverse! Your turn again!";
                    messageTimer = 60;
                    // Reverse in 2-player game means current player goes again
                    playerTurn = !playerTurn;
                }
                case GOLD -> {
                    message = "Wild card played!";
                    messageTimer = 60;
                }
            }
        }

        // Check for game over
        if (player.handSize() == 0) {
            gameOver = true;
            winner = "Player";
            message = "Congratulations! You win!";
            messageTimer = Integer.MAX_VALUE;
        } else if (computer.handSize() == 0) {
            gameOver = true;
            winner = "Computer";
            message = "Game Over! Computer wins!";
            messageTimer = Integer.MAX_VALUE;
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
            g.drawString(gameOverText, textX + 2, 200 + 2);
            
            // Draw main text
            g.setColor(Color.WHITE);
            g.drawString(gameOverText, textX, 200);
            
            // Draw winner announcement
            g.setFont(new Font("Arial", Font.BOLD, 32));
            fm = g.getFontMetrics();
            String winnerText = winner + " Wins!";
            textX = (800 - fm.stringWidth(winnerText)) / 2;
            g.drawString(winnerText, textX, 280);
            
            // Draw final score
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String scoreText = "Final Score - Player: " + (7 - player.handSize()) + " | Computer: " + (7 - computer.handSize());
            fm = g.getFontMetrics();
            textX = (800 - fm.stringWidth(scoreText)) / 2;
            g.drawString(scoreText, textX, 340);
            
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

        // Draw computer's cards face down
        List<Card> computerHand = computer.getHand();
        int startX = (800 - (computerHand.size() * 100 - 20)) / 2;
        for (int i = 0; i < computerHand.size(); i++) {
            g.setColor(new Color(30, 34, 42));
            g.fillRoundRect(startX + i * 100, 50, 80, 120, 10, 10);
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

        // Draw message
        if (messageTimer > 0) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(message, (800 - fm.stringWidth(message)) / 2, 300);
        }

        // Draw turn indicator
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String turnText = playerTurn ? "Your Turn" : "Computer's Turn";
        g.drawString(turnText, 20, 30);

        // Draw card counts
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Your Cards: " + player.handSize(), 20, 550);
        g.drawString("Computer's Cards: " + computer.handSize(), 20, 80);
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

        // Only handle player interactions during their turn
        if (!playerTurn) return;

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

            // Handle card clicks
            List<Card> playerHand = player.getHand();
            for (int i = 0; i < cardBounds.length && i < playerHand.size(); i++) {
                if (cardBounds[i].contains(mouse)) {
                    Card selectedCard = playerHand.get(i);
                    if (selectedCard.matches(topCard)) {
                        Card played = player.playCard(i);
                        handlePlayedCard(played);
                        playerTurn = false;
                        updateCardBounds();
                        message = "You played " + played.getColor() + 
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
    }

    @Override
    public void onExit() {
        // Nothing special needed
    }
}
