import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class gameSystem {
    protected List<String> player1Hand;
    protected List<String> player2Hand;
    protected List<String> player3Hand;
    private final String[] colors = {"Red", "Blue", "White", "Black", "Red", "Blue", "White", "Red", "Blue", "White"};
    private boolean playerCanceledTurn = false;
    private boolean gameRunning = true; // Track if the game is running
    private GameLauncherGUI launcher; // Reference to GameLauncherGUI to return to main menu

    public void setLauncher(GameLauncherGUI launcher) {
        this.launcher = launcher;
    }

    public void start() {
        while (gameRunning) {
            prepareNewRound();
            playRound();
        }
    }

    public void prepareNewRound() {
        // Shuffle the colors
        List<String> colorList = Arrays.asList(colors);
        Collections.shuffle(colorList);

        // Deal the colors between players
        player1Hand = new ArrayList<>(colorList.subList(0, colorList.size() / 3));
        player2Hand = new ArrayList<>(colorList.subList(colorList.size() / 3, 2 * colorList.size() / 3));
        player3Hand = new ArrayList<>(colorList.subList(2 * colorList.size() / 3, colorList.size() - 1));
    }

    public void playRound() {
        while (true) {
            if (!playerTurn("Player 1", player1Hand)) break;
            if (!playerTurn("Player 2", player2Hand)) break;
            if (!playerTurn("Player 3", player3Hand)) break;
        }

        // Determine the loser for this round if no player canceled their turn
        if (!playerCanceledTurn) {
            if (containsBlack(player1Hand)) {
                JOptionPane.showMessageDialog(null, "Player 1 picked Black and lost!");
                returnToMainMenu();
            }
            if (containsBlack(player2Hand)) {
                JOptionPane.showMessageDialog(null, "Player 2 picked Black and lost!");
                returnToMainMenu();
            }
            if (containsBlack(player3Hand)) {
                JOptionPane.showMessageDialog(null, "Player 3 picked Black and lost!");
                returnToMainMenu();
            }
        }
        playerCanceledTurn = false; // Reset for the next round
    }

    private boolean playerTurn(String playerName, List<String> playerHand) {
        final boolean[] playerPickedBlack = {false}; // Track if player picked Black card

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
                    String playerChoice = e.getActionCommand();
                    playerHand.remove(playerChoice);
                    if (playerChoice.equals("Black")) {
                        playerPickedBlack[0] = true;
                    }
                }
            });
            buttons.add(button);
            buttonGroup.add(button);
            panel.add(button);
        }

        int result = JOptionPane.showConfirmDialog(null, panel, playerName + "'s turn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            // Player canceled, stop the round without displaying any message
            playerCanceledTurn = true;
            returnToMainMenu();
            return false;
        }

        if (playerPickedBlack[0]) {
            JOptionPane.showMessageDialog(null, playerName + " picked Black and lost!");
            returnToMainMenu();
            return false; // Player picked Black card, stop the round
        }
        return true; // Player didn't pick Black card, continue the round
    }

    private boolean containsBlack(List<String> hand) {
        return hand.contains("Black");
    }

    private void returnToMainMenu() {
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
    }
}
