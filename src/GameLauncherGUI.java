import javax.swing.*;
import java.awt.*;

public class GameLauncherGUI extends JFrame {
    private gameSystem game;
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

        JButton probability = new JButton("Winning probability");
        probability.addActionListener(e -> showProb());

        panel.add(players);
        panel.add(probability);
        panel.add(playButton);
        panel.add(welcomeButton);
        panel.add(ruleButton);
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
        game.reset();
        gamePanel = null; // Clear the game panel reference
    }

    private void showWelcome() {
        // Placeholder method for showing welcome
        Welcome_to welcome = new Welcome_to("Super Fun Card Game", "1.0");
        JOptionPane.showMessageDialog(null, welcome.welcome(), "Welcome", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showGameRules() {
        if (gamePanel != null) {
            gamePanel.removeAll(); // Clear previous content
        }

        JTextArea ruleTextArea = new JTextArea(20, 40);
        ruleTextArea.setEditable(false);
        ruleTextArea.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font for readability
        JScrollPane scrollPane = new JScrollPane(ruleTextArea);
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(scrollPane, BorderLayout.CENTER);

        Game_Rule120 rules = new Game_Rule120();
        rules.setRuleTextArea(ruleTextArea);
        rules.displayRules();

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            returnToMainMenu();
            rules.cancelTimer(); // Cancel timer when returning to main menu
        });
        backButton.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font for button

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        gamePanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().removeAll(); // Clear all components
        getContentPane().add(gamePanel);
        revalidate();
        repaint();
    }


    private void showNames() {
        Players players = new Players();
        players.Player_s();

        if(players.getName()==null || players.getName2()==null || players.getName3()==null) {
            returnToMainMenu();
        }
        JOptionPane.showMessageDialog(null, "Player 1: " + players.getName() + ", Age: " + players.getAge() +
                "\nPlayer 2: " + players.getName2() + ", Age: " + players.getAge2() +
                "\nPlayer 3: " + players.getName3() + ", Age: " + players.getAge3());
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
