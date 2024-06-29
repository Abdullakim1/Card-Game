import javax.swing.*;
import java.util.Scanner;
import java.util.SplittableRandom;

abstract class experience {
    abstract void experience_of();
    abstract String general();
}
public class player_experience extends experience {
    private int gaming_experience;
    private int gaming_experience2;
    private int gaming_experience3;

    protected String general() {
        return "For statistical purpose Enter your gaming experience";
    }

    protected void experience_of() {
        try {

            String input1 = JOptionPane.showInputDialog("Player1 Enter your gaming experience:");
            gaming_experience = Integer.parseInt(input1);

            String input2 = JOptionPane.showInputDialog("Player2 Enter your gaming experience:");
            gaming_experience2 = Integer.parseInt(input2);

            String input3 = JOptionPane.showInputDialog("Player3 Enter your gaming experience:");
            gaming_experience3 = Integer.parseInt(input3);


            if (gaming_experience < 5) {
                JOptionPane.showMessageDialog(null, "Unfortunately your experience is insufficient");
                System.exit(0);
            } else if (gaming_experience == 5 || gaming_experience > 5) {
                JOptionPane.showMessageDialog(null,"qualified");

            }
            //Player 2

            if (gaming_experience2 < 5) {
                JOptionPane.showMessageDialog(null, "Unfortunately your experience is insufficient");
                System.exit(0);

            } else if (gaming_experience2 == 5 || gaming_experience2 > 5) {
                JOptionPane.showMessageDialog(null,"qualified");


            }
            //player 3

            if (gaming_experience3 < 5) {
                JOptionPane.showMessageDialog(null, "Unfortunately your experience is insufficient");
                System.exit(0);
            } else if (gaming_experience3 == 5 || gaming_experience3 > 5) {
                JOptionPane.showMessageDialog(null,"qualified");


            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input, please enter number");
        }
    }
    public void setGamingExperience(int experience1, int experience2,int experience3){
        gaming_experience=experience1;
        gaming_experience2=experience2;
        gaming_experience3=experience3;
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
}