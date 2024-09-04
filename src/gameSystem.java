import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class gameSystem<T extends Player> {
    private List<T> players;
    private final String[] baseColors = {"Red", "Blue", "White"};
    private boolean playerCanceledTurn = false;
    private boolean gameRunning = true;
    private GameLauncherGUI launcher;
    private String firstChosenColor;
    private T currentColorSetter;

    public void setLauncher(GameLauncherGUI launcher) {
        this.launcher = launcher;
    }

    public void setPlayers(List<T> players) {
        this.players = players;
        adjustColorsBasedOnPlayers(players.size());
    }

    private void adjustColorsBasedOnPlayers(int playerCount) {
        // Each player gets 3 cards
        int totalCards = 3 * playerCount;

        // Creates a list with the required number of colors, including exactly one "Black" card
        List<String> colors = new ArrayList<>();
        colors.add("Black"); // Exactly one "Black" card
        for (int i = 0; i < totalCards - 1; i++) {
            colors.add(baseColors[i % baseColors.length]);
        }

        Collections.shuffle(colors); // Shuffle to randomize color distribution
    }

    public void start() {
        player_experience exp = new player_experience<>(this);
        exp.experience_of();

        if (!gameRunning) {
            returnToMainMenu();
            return;
        }

        currentColorSetter = players.get(0);
        while (gameRunning) {
            prepareNewRound();
            playRound();
        }
    }

    public void prepareNewRound() {
        firstChosenColor = null;

        // Creates a list of colors for the round
        List<String> colorList = new ArrayList<>(Arrays.asList(baseColors));
        Collections.shuffle(colorList);

        // Ensures each player gets 3 cards
        int cardsPerPlayer = 3;
        List<String> allColors = new ArrayList<>();

        // Adds exactly one "Black" card
        allColors.add("Black");
        for (int i = 0; i < 3 * players.size() - 1; i++) {
            allColors.add(colorList.get(i % colorList.size()));
        }

        Collections.shuffle(allColors); // Shuffle to ensure random distribution

        int index = 0;
        for (T player : players) {
            player.resetHand();
            List<String> playerHand = new ArrayList<>();

            for (int i = 0; i < cardsPerPlayer; i++) {
                playerHand.add(allColors.get(index++));
            }

            player.getHand().addAll(playerHand);
        }

        removeMatchedColors();
    }

    private void removeMatchedColors() {
        Set<String> allColors = new HashSet<>();
        for (T player : players) {
            allColors.addAll(player.getHand());
        }

        Set<String> matchedColors = new HashSet<>();
        for (String color : baseColors) {
            if (!allColors.contains(color)) {
                matchedColors.add(color);
            }
        }

        for (T player : players) {
            player.getHand().removeIf(matchedColors::contains);
        }
    }

    public void playRound() {
        if (!gameRunning) return;

        firstChosenColor = null;

        while (players.stream().anyMatch(T::isActive) && gameRunning) {
            for (T player : players) {
                if (player.isActive() && player.hasCards()) {
                    if (!playerTurn(player)) {
                        player.setActive(false);
                    }
                } else {
                    player.setActive(false);
                }
            }

            if (!gameRunning) return;
        }

        if (checkGameCompletion()) {
            returnToMainMenu();
        }
    }

    private boolean playerTurn(T player) {
        List<String> hand = player.getHand();
        JPanel panel = new JPanel(new GridLayout(0, hand.size()));
        ButtonGroup buttonGroup = new ButtonGroup();
        final String[] playerChoice = {null};

        for (String color : hand) {
            JButton button = new JButton(new ImageIcon("C:/card_images/" + color + ".jpeg"));
            button.setActionCommand(color);
            button.addActionListener(e -> playerChoice[0] = e.getActionCommand());
            buttonGroup.add(button);
            panel.add(button);
        }

        boolean playerTurnCompleted = false;

        while (!playerTurnCompleted && gameRunning) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    player.getName() + "'s turn",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                playerCanceledTurn = true;
                JOptionPane.showMessageDialog(null, player.getName() + " has canceled their turn. Returning to main menu.");
                gameRunning = false;
                returnToMainMenu();
                return false;
            }

            if (result == JOptionPane.OK_OPTION) {
                if (playerChoice[0] != null) {
                    String chosenColor = playerChoice[0];

                    if (chosenColor.equals("Black")) {
                        JOptionPane.showMessageDialog(null, player.getName() + " picked the black card and lost the game! Returning to main menu.");
                        gameRunning = false;
                        returnToMainMenu();
                        return false;
                    }

                    if (player.equals(currentColorSetter)) {
                        firstChosenColor = chosenColor;
                        player.takeTurn(chosenColor, firstChosenColor);
                        playerTurnCompleted = true;
                        player.getHand().remove(chosenColor);
                    } else {
                        if (firstChosenColor != null && chosenColor.equals(firstChosenColor)) {
                            player.takeTurn(chosenColor, firstChosenColor);
                            playerTurnCompleted = true;
                            player.getHand().remove(chosenColor);
                        } else {
                            if (firstChosenColor == null) {
                                JOptionPane.showMessageDialog(null, "Error: First chosen color is not set. Skipping turn.");
                                playerTurnCompleted = true;
                            } else if (hand.contains(firstChosenColor)) {
                                JOptionPane.showMessageDialog(null, "You must pick the color " + firstChosenColor);
                            } else {
                                int choice = JOptionPane.showConfirmDialog(
                                        null,
                                        "The selected color does not match. Do you want to skip your turn?",
                                        "Skip Turn",
                                        JOptionPane.YES_NO_OPTION
                                );
                                if (choice == JOptionPane.YES_OPTION) {
                                    playerTurnCompleted = true;
                                    JOptionPane.showMessageDialog(null, player.getName() + " has skipped their turn.");
                                } else {
                                    JOptionPane.showMessageDialog(null, "You must pick the color " + firstChosenColor);
                                }
                            }
                        }
                    }

                    if (player.getHand().isEmpty()) {
                        moveColorSetterToNextPlayer(player);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a card before clicking OK.");
                }
            }
        }

        return true;
    }

    private void moveColorSetterToNextPlayer(T current) {
        int currentIndex = players.indexOf(current);
        int nextIndex = (currentIndex + 1) % players.size();

        currentColorSetter = players.get(nextIndex);
        firstChosenColor = null;
    }

    private boolean checkGameCompletion() {
        return players.stream().allMatch(p -> p.getHand().isEmpty() || !p.isActive());
    }

    void returnToMainMenu() {
        gameRunning = false;
        launcher.returnToMainMenu();
    }

    public void reset() {
        players = new ArrayList<>();
        gameRunning = true;
        playerCanceledTurn = false;
        firstChosenColor = null;
    }

    public List<T> getPlayers() {

        return players;
    }
}
