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
import com.cardgame.model.game.GameOutcome;
import com.cardgame.view.components.ModernButton;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
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
    private String message;
    private int messageTimer;
    private boolean showOutcomeAnimation;
    private BufferedImage outcomeImage;
    private boolean outcomeInitialized = false;
    private boolean isAITurnPending = false;
    private boolean includeComputerPlayer;
    private TurnManager turnManager;
    private PlayStateUI playStateUI;
    private GameOutcomeManager gameOutcomeManager;
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 120;

    public PlayState(Game game, List<String> playerNames, boolean includeComputer) {
        super(game);
        initializeGame(playerNames, includeComputer);
        this.turnManager = new TurnManager(this);
        this.playStateUI = new PlayStateUI(this);
        this.gameOutcomeManager = new GameOutcomeManager(this);
    }
    
    public PlayState(Game game) {
        super(game);
        List<String> defaultNames = new ArrayList<>();
        defaultNames.add("Player");
        initializeGame(defaultNames, true);
        this.turnManager = new TurnManager(this);
        this.playStateUI = new PlayStateUI(this);
        this.gameOutcomeManager = new GameOutcomeManager(this);
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
        if (!gameOver && players.get(currentPlayerIndex).isComputer() && this.includeComputerPlayer) {
            isAITurnPending = true;
        } else {
            isAITurnPending = false;
        }
    }

    @Override
    public void tick() {
        if (messageTimer > 0) {
            messageTimer--;
        }
        if (showOutcomeAnimation && gameOver) {
            gameOutcomeManager.showOutcomeAnimation();
        }
        if (!gameOver && currentPlayerIndex < players.size() && players.get(currentPlayerIndex).isComputer() && isAITurnPending) {
            isAITurnPending = false;
            turnManager.processAITurn();
        }
    }

    @Override
    public void render(Graphics g) {
        playStateUI.render(g);
    }

    @Override
    public void handleMouseEvent(MouseEvent e) {
        playStateUI.handleMouseEvent(e);
    }

    public void handlePlayedCard(Card playedCard) {
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
                Player playerToDraw = turnManager.getNextPlayer();
                message = playerToDraw.getName() + " draws 2 cards!";
                effect.apply(this, playerToDraw);
            } else if (effect instanceof SkipTurnEffect) {
                effect.apply(this, null);
            } else if (effect instanceof ReverseDirectionEffect) {
                message = activePlayer.getName() + " played REVERSE! Direction of play is changed.";
                effect.apply(this, null);
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

    public void advanceTurn(Card cardJustPlayed) {
        turnManager.advanceTurn(cardJustPlayed);
    }

    @Override
    public void onEnter() {
        playStateUI.onEnter();
        if (!gameOver && currentPlayerIndex < players.size() && players.get(currentPlayerIndex).isComputer() && this.includeComputerPlayer) {
            isAITurnPending = true;
        } else {
            isAITurnPending = false;
        }
    }

    @Override
    public void onExit() {}

    public Deck getDeck() {
        return deck;
    }

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
        if (mostCommon == null || max <= 0) {
            for (Card card : player.getHand()) {
                if (card.getColor() != CardColor.GOLD) return card.getColor();
            }
            return CardColor.BLUE;
        }
        return mostCommon;
    }

    public String formatCardNameForMessage(Card card) {
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

    public List<Player> getPlayers() {
        return players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public Card getTopCard() {
        return topCard;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public boolean isDirectionClockwise() {
        return directionClockwise;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageTimer() {
        return messageTimer;
    }

    public void setMessageTimer(int messageTimer) {
        this.messageTimer = messageTimer;
    }

    public boolean isShowOutcomeAnimation() {
        return showOutcomeAnimation;
    }

    public BufferedImage getOutcomeImage() {
        return outcomeImage;
    }

    public void setOutcomeImage(BufferedImage outcomeImage) {
        this.outcomeImage = outcomeImage;
    }

    public boolean isOutcomeInitialized() {
        return outcomeInitialized;
    }

    public boolean isAITurnPending() {
        return isAITurnPending;
    }

    public void setAITurnPending(boolean AITurnPending) {
        isAITurnPending = AITurnPending;
    }

    public boolean isIncludeComputerPlayer() {
        return includeComputerPlayer;
    }

    public boolean getSkipNextPlayerTurnFlag() {
        return skipNextPlayerTurnFlag;
    }

    public void setSkipNextPlayerTurnFlag(boolean skipNextPlayerTurnFlag) {
        this.skipNextPlayerTurnFlag = skipNextPlayerTurnFlag;
    }

    public static int getCardWidth() {
        return CARD_WIDTH;
    }

    public static int getCardHeight() {
        return CARD_HEIGHT;
    }
}