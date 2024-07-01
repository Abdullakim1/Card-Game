import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

abstract class game_rule {
    abstract String gen_rule();
    abstract String rule_one();
    abstract String rule_two();
    abstract String rule_three();
}

public class Game_Rule120 extends game_rule {
    private Timer timer;
    private boolean timeUp;
    private JTextArea ruleTextArea; // TextArea to display rules
    private int currentRuleIndex; // Track current rule index

    public Game_Rule120() {
        this.timer = new Timer();
        this.timeUp = false;
        this.currentRuleIndex = 0;
    }

    @Override
    public String gen_rule() {
        return "General rules of the game:";
    }

    @Override
    public String rule_one() {
        return "If you have a black card, you lose, but keep playing";
    }

    @Override
    public String rule_two() {
        return "Each person will get 3 cards randomly";
    }

    @Override
    public String rule_three() {
        return "Rarely black card in the game, the distribution is on system";
    }

    public void setRuleTextArea(JTextArea ruleTextArea) {
        this.ruleTextArea = ruleTextArea;
    }

    public void displayRules() {
        // Clear previous text in ruleTextArea
        ruleTextArea.setText("");

        String[] rules = {gen_rule(), rule_one(), rule_two(), rule_three()};
        currentRuleIndex = 0; // Reset current rule index

        TimerTask displayTask = new TimerTask() {
            @Override
            public void run() {
                if (currentRuleIndex < rules.length) {
                    String currentRule = rules[currentRuleIndex];
                    typewriterEffect(currentRule);
                    currentRuleIndex++;
                } else {
                    timer.cancel(); // Cancel timer after displaying all rules
                }
            }
        };

        // Task to run every 3 seconds
        timer.scheduleAtFixedRate(displayTask, 0, 3000);
    }

    private void typewriterEffect(String message) {
        new Thread(() -> {
            try {
                for (char c : message.toCharArray()) {
                    SwingUtilities.invokeLater(() -> {
                        ruleTextArea.append(String.valueOf(c));
                        ruleTextArea.setCaretPosition(ruleTextArea.getDocument().getLength()); // Scroll to bottom
                    });
                    Thread.sleep(50);
                }
                SwingUtilities.invokeLater(() -> ruleTextArea.append("\n"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void startTimer() {
        timeUp = false;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeUp = true;
                SwingUtilities.invokeLater(() -> ruleTextArea.append("After you put the card it will come back to main menu \n"));
            }
        }, 10000);
    }

    public void cancelTimer() {
        timer.cancel();
    }

    public boolean isTimeUp() {
        return timeUp;
    }
}

