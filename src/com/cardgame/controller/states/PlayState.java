package com.cardgame.controller.states;

import com.cardgame.Game;
import com.cardgame.model.card.Card;
import com.cardgame.model.card.Card.CardColor;
import com.cardgame.model.card.Deck;
import com.cardgame.model.player.types.AbstractPlayer;
import com.cardgame.model.player.types.HumanPlayer;
import com.cardgame.model.player.types.ComputerPlayer;
import com.cardgame.view.components.ModernButton;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class PlayState extends GameState {
    private HumanPlayer player1;
    private AbstractPlayer player2; // Can be either HumanPlayer or ComputerPlayer
    private Deck deck;
    private Card topCard;
    private boolean player1Turn;
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
    private boolean isVsComputer;

    public PlayState(Game game, boolean isHumanVsHuman) {
        super(game);
        this.isVsComputer = !isHumanVsHuman;
        initializeGame(null);
    }

    public PlayState(Game game, List<AbstractPlayer> players) {
        super(game);
        initializeGame(players);
    }

    private void initializeGame(List<AbstractPlayer> players) {
        deck = new Deck();
        if (players != null && players.size() >= 2) {
            if (players.get(0) instanceof HumanPlayer) {
                player1 = (HumanPlayer) players.get(0);
            } else {
                player1 = new HumanPlayer("Player 1", 0);
            }
            if (players.get(1) instanceof AbstractPlayer) {
                player2 = players.get(1);
            } else {
                player2 = isVsComputer ? 
                    new ComputerPlayer("Computer", 1) : 
                    new HumanPlayer("Player 2", 1);
            }
        } else {
            player1 = new HumanPlayer("Player 1", 0);
            player2 = isVsComputer ? 
                new ComputerPlayer("Computer", 1) : 
                new HumanPlayer("Player 2", 1);
        }
        
        // Deal 7 cards to each player
        player1.addCards(deck.draw(7));
        player2.addCards(deck.draw(7));
        
        // Place first card face up
        topCard = deck.draw();
        while (topCard != null && topCard.isSpecial()) {
            deck.discard(topCard);
            topCard = deck.draw();
        }
        if (topCard != null) {
            topCard.setFaceUp(true);
        }
        
        player1Turn = true;
        skipNextTurn = false;
        gameOver = false;
        winner = null;
        message = "Player 1's turn! Match the color or number";
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
        int cardWidth = 80;
        int cardHeight = 120;
        int spacing = 20;
        List<Card> currentPlayerHand = player1Turn ? player1.getHand() : player2.getHand();
        int startX = (800 - (currentPlayerHand.size() * (cardWidth + spacing) - spacing)) / 2;
        int y = 400;

        cardBounds = new Rectangle[currentPlayerHand.size()];
        for (int i = 0; i < currentPlayerHand.size(); i++) {
            cardBounds[i] = new Rectangle(startX + i * (cardWidth + spacing), y, cardWidth, cardHeight);
        }
    }

    @Override
    public void tick() {
        if (messageTimer > 0) {
            messageTimer--;
        }

        // Handle computer's turn
        if (!player1Turn && !gameOver && isVsComputer) {
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
        if (player1Turn || gameOver) return; // Safety check

        int playIndex = ((ComputerPlayer)player2).selectBestMove(topCard);
        if (playIndex != -1) {
            Card played = player2.playCard(playIndex);
            handlePlayedCard(played);
            player1Turn = true;
            message = "Computer played " + played.getColor() + 
                     (played.isSpecial() ? " special card" : " " + played.getValue());
            messageTimer = 60;
        } else {
            // Draw a card if no playable cards
            Card drawnCard = deck.draw();
            if (drawnCard != null) {
                player2.addCard(drawnCard);
                // Check if drawn card can be played
                if (drawnCard.matches(topCard)) {
                    Card played = player2.playCard(player2.handSize() - 1);
                    handlePlayedCard(played);
                    player1Turn = true;
                    message = "Computer drew and played " + played.getColor() + 
                             (played.isSpecial() ? " special card" : " " + played.getValue());
                } else {
                    message = "Computer drew a card";
                    player1Turn = true;
                }
            } else {
                message = "No cards left to draw!";
                player1Turn = true;
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
                    player1Turn = !player1Turn;
                }
                case BLUE -> {
                    AbstractPlayer target = player1Turn ? player2 : player1;
                    target.addCards(deck.draw(2));
                    message = "Draw 2 cards!";
                    messageTimer = 60;
                }
                case GREEN -> {
                    message = "Reverse! Your turn again!";
                    messageTimer = 60;
                    // Reverse in 2-player game means current player goes again
                    player1Turn = !player1Turn;
                }
                case GOLD -> {
                    message = "Wild card played!";
                    messageTimer = 60;
                }
            }
        }

        // Check for game over
        if (player1.handSize() == 0) {
            gameOver = true;
            winner = "Player 1";
            return;
        }
        if (player2.handSize() == 0) {
            gameOver = true;
            winner = isVsComputer ? "Computer" : "Player 2";
            return;
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
            String scoreText = "Final Score - Player 1: " + (7 - player1.handSize()) + " | " + (isVsComputer ? "Computer" : "Player 2") + ": " + (7 - player2.handSize());
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
        List<Card> computerHand = player2.getHand();
        int startX = (800 - (computerHand.size() * 100 - 20)) / 2;
        for (int i = 0; i < computerHand.size(); i++) {
            g.setColor(new Color(30, 34, 42));
            g.fillRoundRect(startX + i * 100, 50, 80, 120, 10, 10);
        }

        // Draw player's cards
        List<Card> playerHand = player1.getHand();
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
        String turnText = player1Turn ? "Your Turn" : (isVsComputer ? "Computer's Turn" : "Player 2's Turn");
        g.drawString(turnText, 20, 30);

        // Draw card counts
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Your Cards: " + player1.handSize(), 20, 550);
        g.drawString((isVsComputer ? "Computer's Cards" : "Player 2's Cards") + ": " + player2.handSize(), 20, 80);
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
        if ((!player1Turn && isVsComputer) || (!player1Turn && !isVsComputer && cardBounds == null)) return;

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
                    player1.addCard(drawnCard);
                    message = "You drew a card";
                    messageTimer = 60;
                    player1Turn = false;  // End player's turn after drawing
                    updateCardBounds();
                } else {
                    message = "No cards left to draw!";
                    messageTimer = 60;
                }
                return;
            }

            // Handle card clicks
            AbstractPlayer currentPlayer = player1Turn ? player1 : player2;
            List<Card> currentHand = currentPlayer.getHand();
            for (int i = 0; i < cardBounds.length && i < currentHand.size(); i++) {
                if (cardBounds[i].contains(mouse)) {
                    Card selectedCard = currentHand.get(i);
                    if (selectedCard.matches(topCard)) {
                        Card played = currentPlayer.playCard(i);
                        handlePlayedCard(played);
                        player1Turn = !player1Turn;
                        updateCardBounds();
                        String playerName = player1Turn ? "Player 2" : "Player 1";
                        message = playerName + " played " + played.getColor() + 
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
