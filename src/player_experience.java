import javax.swing.*;
import java.util.List;

abstract class experience {
    abstract void experience_of();
}

public class player_experience<T extends Player> extends experience {
    private List<T> players; // List of players
    private gameSystem<T> game; // Reference to gameSystem

    // Constructor to accept gameSystem reference
    public player_experience(gameSystem<T> game) {
        this.game = game;
        this.players = game.getPlayers();
    }

    @Override
    protected void experience_of() {
        if (players == null || players.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No players available.");
            return;
        }

        int[] experiences = new int[players.size()];
        StringBuilder resultMessage = new StringBuilder();

        for (int i = 0; i < players.size(); i++) {
            String input = JOptionPane.showInputDialog("Player " + (i + 1) + ": Enter your gaming experience:");
            if (input == null) {
                returnToMainMenu();
                return;
            }

            try {
                experiences[i] = Integer.parseInt(input);

                resultMessage.append("Player ").append(i + 1).append(" Experience: ").append(experiences[i]);
                if (experiences[i] < 5) {
                    resultMessage.append(" (Insufficient experience)\n");
                } else {
                    resultMessage.append(" (Qualified)\n");
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input for Player " + (i + 1) + ". Please enter a number.");
                return;
            }
        }

        JOptionPane.showMessageDialog(null, resultMessage.toString());

        // Check if any player is not qualified
        boolean anyPlayerNotQualified = false;
        for (int experience : experiences) {
            if (experience < 5) {
                anyPlayerNotQualified = true;
                break;
            }
        }

        if (anyPlayerNotQualified) {
            returnToMainMenu();
        }
    }

    private void returnToMainMenu() {
        game.returnToMainMenu();
    }
}
