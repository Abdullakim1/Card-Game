import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Player {
        private String name;
        private int score;
        private List<String> hand;
        private boolean isActive;

        public Player(String name) {
            this.name = name;
            this.score = 0;
            this.hand = new ArrayList<>();
            this.isActive = true;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        public void addScore(int points){
            if(points < 0){
                throw new IllegalArgumentException("Score cannot be negative");

            }
            this.score += points;
        }

        public List<String> getHand() {
            return hand;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public void resetHand() {
            this.hand.clear();
        }

        public void addCardToHand(String card) {
            this.hand.add(card);
        }

        public void removeCardFromHand(String card) {
            this.hand.remove(card);
        }

        public boolean hasCards() {
            return !this.hand.isEmpty();
        }

        public boolean takeTurn(String chosenColor, String firstChosenColor) {
            // Logic for player to take a turn
            if (chosenColor.equals("Black")) {
                JOptionPane.showMessageDialog(null, name + " picked the black card and lost the game! Returning to main menu.");
                return false; // Player lost
            }

            if (firstChosenColor == null || chosenColor.equals(firstChosenColor)) {
                removeCardFromHand(chosenColor);
                return true;
            }

            // Option to skip turn if not matching the first chosen color
            int choice = JOptionPane.showConfirmDialog(null, "Do you want to skip your turn?", "Skip Turn", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, name + " has skipped their turn.");
                return true; // Player skipped their turn
            } else {
                JOptionPane.showMessageDialog(null, "You must pick the color " + firstChosenColor);
                return true; // Player didn't skip and must pick the correct color
            }
        }
    }
