import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class Players<T extends Player> {
    private final List<T> players = new ArrayList<>();
    private final gameSystem<T> game;

    public Players(gameSystem<T> game) {
        this.game = game;
        getPlayers();
    }

    public void getPlayers() {
        int i = 1;
        while (true) {
            String playerName = JOptionPane.showInputDialog(null, "Enter name for Player " + i + ":", "Player Name", JOptionPane.PLAIN_MESSAGE);

            if (playerName == null) {
                game.reset(); // Optionally reset the game state if needed
                game.returnToMainMenu(); // Return to the main menu
                return; // Exit the method to stop further processing
            }

            playerName = playerName.trim();
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Player name cannot be empty. Please enter a valid name.");
            } else {
                players.add((T) new Player(playerName));
                i++;
            }

            int addMore = JOptionPane.showConfirmDialog(null, "Would you like to add another player?", "Add Player", JOptionPane.YES_NO_OPTION);
            if (addMore == JOptionPane.NO_OPTION) break;
        }

        game.setPlayers(players);
    }

    public List<T> getAllPlayers() {
        return players;
    }
}
