import javax.swing.*;

abstract class experience {
    abstract void experience_of();
}

public class player_experience extends experience {
    private int gaming_experience;
    private int gaming_experience2;
    private int gaming_experience3;
    private gameSystem game; // Reference to gameSystem

    // Constructor to accept gameSystem reference
    public player_experience(gameSystem game) {
        this.game = game;
    }

    @Override
    protected void experience_of() {
        try {
            String input1 = JOptionPane.showInputDialog("Player 1: Enter your gaming experience:");
            if(input1==null){
                returnToMainMenu();
            }
            gaming_experience = Integer.parseInt(input1);

            String input2 = JOptionPane.showInputDialog("Player 2: Enter your gaming experience:");
            if(input2==null){
                returnToMainMenu();
            }
            gaming_experience2 = Integer.parseInt(input2);

            String input3 = JOptionPane.showInputDialog("Player 3: Enter your gaming experience:");
            if(input3==null){
                returnToMainMenu();
            }
            gaming_experience3 = Integer.parseInt(input3);

            StringBuilder resultMessage = new StringBuilder();

            resultMessage.append("Player 1 Experience: ").append(gaming_experience);
            if (gaming_experience < 5) {
                resultMessage.append(" (Insufficient experience)\n");
            } else {
                resultMessage.append(" (Qualified)\n");
            }

            resultMessage.append("Player 2 Experience: ").append(gaming_experience2);
            if (gaming_experience2 < 5) {
                resultMessage.append(" (Insufficient experience)\n");
            } else {
                resultMessage.append(" (Qualified)\n");
            }

            resultMessage.append("Player 3 Experience: ").append(gaming_experience3);
            if (gaming_experience3 < 5) {
                resultMessage.append(" (Insufficient experience)\n");
            } else {
                resultMessage.append(" (Qualified)\n");
            }

            JOptionPane.showMessageDialog(null, resultMessage.toString());

            if (gaming_experience < 5 || gaming_experience2 < 5 || gaming_experience3 < 5) {
                returnToMainMenu();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input, please enter a number.");
        }
    }

    public void setGamingExperience(int experience1, int experience2, int experience3) {
        gaming_experience = experience1;
        gaming_experience2 = experience2;
        gaming_experience3 = experience3;
    }

    public int getGamingExperience() {
        return gaming_experience;
    }

    public int getGamingExperience2() {
        return gaming_experience2;
    }

    public int getGamingExperience3() {
        return gaming_experience3;
    }

    public boolean isqualified(int experience) {
        return experience >= 5;
    }

    private void returnToMainMenu() {
        game.returnToMainMenu();
    }
}
