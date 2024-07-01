import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class gameSystem {
    protected List<String> player1Hand;
    protected List<String> player2Hand;
    protected List<String> player3Hand;
    private final String[] colors = {"Red", "Blue", "White", "Black", "Red", "Blue", "White", "Red", "Blue", "White"};
    private boolean playerCanceledTurn = false;
    private boolean gameRunning = true; // Track if the game is running
    private GameLauncherGUI launcher; // Reference to GameLauncherGUI to return to main menu
    private String firstChosenColor; // Track the first color chosen by the first player
    private String secondChosenColor;

    public void setLauncher(GameLauncherGUI launcher) {
        this.launcher = launcher;
    }

    public void start() {
        player_experience exp = new player_experience(this);
        exp.experience_of();

        if(!gameRunning) {
            returnToMainMenu();
            return;
        }

        while (gameRunning) {
            prepareNewRound();
            playRound();
        }
    }

    public void prepareNewRound() {
        // Reset firstChosenColor for the new round
        firstChosenColor = null;
        secondChosenColor=null;

        // Shuffle the colors
        List<String> colorList = new ArrayList<>(Arrays.asList(colors));
        Collections.shuffle(colorList);

        // Deal the colors between players
        player1Hand = new ArrayList<>(colorList.subList(0, colorList.size() / 3));
        player2Hand = new ArrayList<>(colorList.subList(colorList.size() / 3, 2 * colorList.size() / 3));
        player3Hand = new ArrayList<>(colorList.subList(2 * colorList.size() / 3, colorList.size() - 1));

        // Remove colors that are not available anymore
        removeMatchedColors(player1Hand, player2Hand, player3Hand);
    }


    private void removeMatchedColors(List<String> hand1, List<String> hand2, List<String> hand3) {
        List<String> allHands = new ArrayList<>();
        allHands.addAll(hand1);
        allHands.addAll(hand2);
        allHands.addAll(hand3);

        Set<String> matchedColors = new HashSet<>();
        for (String color : colors) {
            if (Collections.frequency(allHands, color) == 0) {
                matchedColors.add(color);
            }
        }

        hand1.removeIf(matchedColors::contains);
        hand2.removeIf(matchedColors::contains);
        hand3.removeIf(matchedColors::contains);
    }


    public void playRound() {
        if (!gameRunning) return;
        boolean player1Active = true;
        boolean player2Active = true;
        boolean player3Active = true;

        // Reset firstChosenColor for the new round
        firstChosenColor = null;
        secondChosenColor = null;

        while ((player1Active || player2Active || player3Active) && gameRunning) {
            if (player1Active && player1Hand.size() > 0) {
                if (!playerTurn("Player 1", player1Hand)) {
                    player1Active = false;
                }
            } else {
                player1Active = false;
            }

            if (!gameRunning) return;

            if (player2Active && player2Hand.size() > 0) {
                if (!playerTurn("Player 2", player2Hand)) {
                    player2Active = false;
                }
            } else {
                player2Active = false;
            }

            if (!gameRunning) return;

            if (player3Active && player3Hand.size() > 0) {
                if (!playerTurn("Player 3", player3Hand)) {
                    player3Active = false;
                }
            } else {
                player3Active = false;
            }

            if (!gameRunning) return;
        }

        // Check for game completion (all but one player finished)
        if (player1Hand.isEmpty() && (player2Active || player3Active)) {
            returnToMainMenu();
        }
        if (player2Hand.isEmpty() && (player1Active || player3Active)) {
            returnToMainMenu();
        }
        if (player3Hand.isEmpty() && (player1Active || player2Active)) {
            returnToMainMenu();
        }
    }


    private boolean playerTurn(String playerName, List<String> playerHand) {
        // Track if player picked the matching color
        final boolean[] playerPickedMatchingColor = {false};
        final String[] playerChoice = {null};

        // Show player's hand as buttons
        JPanel panel = new JPanel(new GridLayout(0, playerHand.size()));
        List<JButton> buttons = new ArrayList<>();
        ButtonGroup buttonGroup = new ButtonGroup();

        for (String color : playerHand) {
            JButton button = new JButton(new ImageIcon("C:/card_images/" + color + ".jpeg"));
            button.setActionCommand(color);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    playerChoice[0] = e.getActionCommand();
                }
            });
            buttons.add(button);
            buttonGroup.add(button);
            panel.add(button);
        }

        // Display the player's hand and wait for their selection
        while (!playerPickedMatchingColor[0] && !playerCanceledTurn && gameRunning) {
            int result = JOptionPane.showConfirmDialog(null, panel, playerName + "'s turn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                // Player canceled, stop the game and return to main menu
                playerCanceledTurn = true;
                JOptionPane.showMessageDialog(null, playerName + " has canceled their turn. Returning to main menu.");
                gameRunning = false;
                returnToMainMenu();
                return false;
            }

            if (playerChoice[0] != null) {
                // Check if the player picked the black card
                if (playerChoice[0].equals("Black")) {
                    JOptionPane.showMessageDialog(null, playerName + " picked the black card and lost the game! Returning to main menu.");
                    gameRunning = false;
                    returnToMainMenu();
                    return false;
                }

                // Handling for Player 1's turn
                if (playerName.equals("Player 1")) {
                    if (firstChosenColor == null || playerChoice[0].equals(firstChosenColor)) {
                        playerPickedMatchingColor[0] = true;
                        playerHand.remove(playerChoice[0]);
                        firstChosenColor = playerChoice[0];
                    } else {
                        playerPickedMatchingColor[0] = true;
                        playerHand.remove(playerChoice[0]);
                        firstChosenColor = playerChoice[0];
                    }
                }
                // Handling for Player 2's turn
                else if (playerName.equals("Player 2")) {
                    if (player1Hand.isEmpty() || playerChoice[0].equals(firstChosenColor)) {
                        playerPickedMatchingColor[0] = true;
                        playerHand.remove(playerChoice[0]);
                    } else {
                        // Skip the player's turn
                        int choice = JOptionPane.showConfirmDialog(null, "Do you want to skip your turn?", "Skip Turn", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            playerPickedMatchingColor[0] = true;
                            JOptionPane.showMessageDialog(null, playerName + " has skipped their turn.");
                        } else {
                            JOptionPane.showMessageDialog(null, "You must pick the color " + firstChosenColor);
                        }
                    }
                }
                // Handling for Player 3's turn
                else {
                    if (firstChosenColor == null || playerChoice[0].equals(firstChosenColor)) {
                        playerPickedMatchingColor[0] = true;
                        playerHand.remove(playerChoice[0]);
                    } else {
                        // Skip the player's turn
                        int choice = JOptionPane.showConfirmDialog(null, "Do you want to skip your turn?", "Skip Turn", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            playerPickedMatchingColor[0] = true;
                            JOptionPane.showMessageDialog(null, playerName + " has skipped their turn.");
                        } else {
                            JOptionPane.showMessageDialog(null, "You must pick the color " + firstChosenColor);
                        }
                    }
                }
            }
        }

        return true;
    }


    private boolean allPlayersMatchedColor(String color) {
        return player1Hand.contains(color) && player2Hand.contains(color) && player3Hand.contains(color);
    }

    void returnToMainMenu() {
        gameRunning = false;
        launcher.returnToMainMenu();
    }

    // Method to reset game state
    public void reset() {
        player1Hand = null;
        player2Hand = null;
        player3Hand = null;
        gameRunning = true;
        playerCanceledTurn = false;
        firstChosenColor = null;
    }
}
