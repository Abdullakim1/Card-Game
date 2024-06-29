import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameLauncherGUI extends JFrame {
    private gameSystem game;
    private player_experience PlayerExperience;
    private Players player_ok;
    private String name, name2, name3;
    private int age, age2, age3;

    private JPanel mainMenuPanel;
    private JPanel gamePanel;

    public GameLauncherGUI() {
        setTitle("Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1700, 800);
        game = new gameSystem();
        game.setLauncher(this); // Pass reference to this instance of GameLauncherGUI to gameSystem
        PlayerExperience = new player_experience();
        player_ok = new Players();

        mainMenuPanel = createMainMenuPanel();
        getContentPane().add(mainMenuPanel);

        setVisible(true);
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JButton playButton = new JButton("Play");
        playButton.addActionListener(e -> {
            if (gamePanel == null) {
                showPlay();
            }
        });

        JButton welcomeButton = new JButton("Welcome message");
        welcomeButton.addActionListener(e -> showWelcome());

        JButton ruleButton = new JButton("Game Rules");
        ruleButton.addActionListener(e -> showGameRules());

        JButton players = new JButton("Players Names");
        players.addActionListener(e -> showNames());

        JButton experience = new JButton("Player Experience");
        experience.addActionListener(e -> showExperience());

        JButton probability = new JButton("Winning probability");
        probability.addActionListener(e -> showProb());

        panel.add(experience);
        panel.add(players);
        panel.add(probability);
        panel.add(playButton);
        panel.add(welcomeButton);
        panel.add(ruleButton);
        panel.add(experience);

        return panel;
    }

    private void showPlay() {
        resetGame(); // Reset game state
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(new JLabel("Game is in progress..."), BorderLayout.CENTER); // Placeholder for game content

        getContentPane().removeAll(); // Clear all components
        getContentPane().add(gamePanel);
        revalidate();
        repaint();
        game.start(); // Start the game
    }

    private void resetGame() {
        // Reset any game-related data or state here
        game.reset(); // Assuming gameSystem has a method to reset the game state
        gamePanel = null; // Clear the game panel reference
    }

    private void showWelcome() {
        // Placeholder method for showing welcome
        Welcome_to welcome = new Welcome_to("Super Fun Card Game", "1.0");
        JOptionPane.showMessageDialog(null, welcome.welcome(), "Welcome", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showGameRules() {
        Game_Rule120 gameRule = new Game_Rule120();
        StringBuilder rulesMessage = new StringBuilder();
        rulesMessage.append("General rules of the game:\n");
        rulesMessage.append("- ").append(gameRule.gen_rule()).append("\n");
        rulesMessage.append("- ").append(gameRule.rule_one()).append("\n");
        rulesMessage.append("- ").append(gameRule.rule_two()).append("\n");
        rulesMessage.append("- ").append(gameRule.rule_three()).append("\n");

        JOptionPane.showMessageDialog(this, rulesMessage.toString(), "Game Rules", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showNames() {
        try {
            String inputName1 = JOptionPane.showInputDialog("Player1 Enter your name:");
            if (inputName1 == null) {
                returnToMainMenu();
                return;
            }
            String inputAge1 = JOptionPane.showInputDialog("Player1 Enter your age:");
            if (inputAge1 == null) {
                returnToMainMenu();
                return;
            }

            String inputName2 = JOptionPane.showInputDialog("Player2 Enter your name:");
            if (inputName2 == null) {
                returnToMainMenu();
                return;
            }
            String inputAge2 = JOptionPane.showInputDialog("Player2 Enter your age:");
            if (inputAge2 == null) {
                returnToMainMenu();
                return;
            }

            String inputName3 = JOptionPane.showInputDialog("Player3 Enter your name:");
            if (inputName3 == null) {
                returnToMainMenu();
                return;
            }
            String inputAge3 = JOptionPane.showInputDialog("Player3 Enter your age:");
            if (inputAge3 == null) {
                returnToMainMenu();
                return;
            }

            name = inputName1;
            age = Integer.parseInt(inputAge1);
            name2 = inputName2;
            age2 = Integer.parseInt(inputAge2);
            name3 = inputName3;
            age3 = Integer.parseInt(inputAge3);

            JOptionPane.showMessageDialog(this, "Player 1: " + name + ", Age: " + age +
                    "\nPlayer 2: " + name2 + ", Age: " + age2 +
                    "\nPlayer 3: " + name3 + ", Age: " + age3);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input, please enter a valid number for age.");
        }
    }

    private void showExperience() {
        JTextField textField1 = new JTextField(10);
        JTextField textField2 = new JTextField(10);
        JTextField textField3 = new JTextField(10);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Player 1 experience"));
        inputPanel.add(textField1);
        inputPanel.add(new JLabel("Player 2 experience"));
        inputPanel.add(textField2);
        inputPanel.add(new JLabel("Player 3 experience"));
        inputPanel.add(textField3);

        int option = JOptionPane.showConfirmDialog(null, inputPanel, "Enter Player Experience", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                int experience1 = Integer.parseInt(textField1.getText());
                int experience2 = Integer.parseInt(textField2.getText());
                int experience3 = Integer.parseInt(textField3.getText());

                PlayerExperience.setGamingExperience(experience1, experience2, experience3);

                JOptionPane.showMessageDialog(null, "Player 1 Experience: " + PlayerExperience.getGamingExperience() +
                        "\nPlayer 2 Experience: " + PlayerExperience.getGamingExperience2() +
                        "\nPlayer 3 Experience: " + PlayerExperience.getGamingExperience3());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input, please enter valid numbers.");
            }
        } else {
            returnToMainMenu();
        }
    }


    private void showProb() {
        game.prepareNewRound();

        prob_win_player1 cal = new prob_win_player1();
        String pl1 = cal.prob_win_player1(game.player1Hand);

        prob_win_player2 cal2 = new prob_win_player2();
        String pl2 = cal2.prob_win_player2(game.player2Hand);

        prob_win_player3 cal3 = new prob_win_player3();
        String pl3 = cal3.prob_win_player3(game.player3Hand);

        String combine = pl1 + "\n\n" + pl2 + "\n\n" + pl3 + "\n";

        JOptionPane.showMessageDialog(null, combine);
    }

    public void returnToMainMenu() {
        getContentPane().removeAll();
        getContentPane().add(mainMenuPanel);
        gamePanel=null;
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameLauncherGUI::new);
    }
}
