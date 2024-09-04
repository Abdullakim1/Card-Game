import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GameLauncherGUI extends JFrame implements GameObserver {
    private gameSystem<Player> game;  // Game system object
    private Players<Player> players;  // Players object

    private JPanel mainMenuPanel;  // Panel for the main menu
    private JPanel gamePanel;  // Panel for the game
    private Game_System gameSystem;
    private JList<String> playerList;
    private JTextField playerNameField;

    public GameLauncherGUI() {
        setTitle("Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1700, 800);
        setLayout(new BorderLayout());
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addPlayerButton = new JButton("Add Player");
        JTextField playerNameField = new JTextField(15);

        controlPanel.add(new JLabel("Player Name:"));
        controlPanel.add(playerNameField);
        controlPanel.add(addPlayerButton);

        add(controlPanel, BorderLayout.NORTH);

        JScrollPane listScrollPane = new JScrollPane(playerList);
        add(listScrollPane, BorderLayout.CENTER);

        // Button to promote to VIP
        JButton promoteButton = new JButton("Promote to VIP");
        promoteButton.addActionListener(e -> {
            int selectedIndex = playerList.getSelectedIndex();
            promoteToVIP(selectedIndex);
        });
        add(promoteButton, BorderLayout.SOUTH);

        addPlayerButton.addActionListener(e -> {
            String playerName = playerNameField.getText().trim();
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid player name.", "Input Error", JOptionPane.WARNING_MESSAGE);
            } else {
                gameSystem.addPlayer(playerName);
                refreshPlayerList();
            }
        });


        // Initialize the game with a new instance of gameSystem with Player type
        this.gameSystem = new Game_System();
        this.gameSystem.addObserver(this);
        game = new gameSystem<>();
        game.setLauncher(this);  // Pass the current GUI instance to the gameSystem


        mainMenuPanel = createMainMenuPanel();  // Create the main menu panel
        getContentPane().add(mainMenuPanel);  // Add the main menu panel to the frame

        setVisible(true);
    }
    @Override
    public void update(){
        refreshPlayerList();
    }

    private void refreshPlayerList() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Player player : gameSystem.getPlayers()) {
            String playerDisplay = player.getName() + " - Score: " + player.getScore();
            if (player instanceof VIPPlayer) {
                playerDisplay += " (VIP)"; // Append VIP status to the player name
            }
            model.addElement(playerDisplay);
        }
        playerList.setModel(model);
    }


    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JButton playButton = new JButton("Play");
        playButton.addActionListener(e -> {
            if (gamePanel == null) {
                showPlay();  // Show the play screen
            }
        });

        JButton ruleButton = new JButton("Game Rules");
        ruleButton.addActionListener(e -> showGameRules());  // Show the game rules screen
        panel.add(playButton);
        panel.add(ruleButton);
        return panel;
    }

    private void showPlay() {
        resetGame();  // Reset the game state before starting

        // Show a welcome screen and start the game
        Welcome_to welcomeScreen = new Welcome_to("Black Card", "1.0");
        welcomeScreen.displayAndStartGame();

        // Initialize the Players object and manage player input
        players = new Players<>(game);

        // Ensure players are properly set up before starting the game
        List<Player> playerList = players.getAllPlayers();
        if (!playerList.isEmpty()) {
            game.setPlayers(playerList);  // Set players in the game system

            // Setup the game panel
            gamePanel = new JPanel(new BorderLayout());
            gamePanel.add(new JLabel("Game is in progress..."), BorderLayout.CENTER);  // Placeholder for actual game content

            getContentPane().removeAll();  // Clear previous components
            getContentPane().add(gamePanel);  // Add the game panel
            revalidate();
            repaint();

            game.start();  // Start the game logic
        } else {
            JOptionPane.showMessageDialog(this, "No players available! Returning to the main menu.");
            returnToMainMenu();  // Return to the main menu if no players are set up
        }
    }

    private void resetGame() {
        // Reset game state and clear game panel
        if (game != null) {
            game.reset();  // Call reset on the game system
        }
        gamePanel = null;  // Clear game panel reference
    }

    private void showGameRules() {
        // Display game rules in a new panel
        if (gamePanel != null) {
            gamePanel.removeAll();  // Clear existing game panel content
        }

        JTextArea ruleTextArea = new JTextArea(20, 40);
        ruleTextArea.setEditable(false);
        ruleTextArea.setFont(new Font("Arial", Font.PLAIN, 16));  // Set font for readability
        JScrollPane scrollPane = new JScrollPane(ruleTextArea);
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(scrollPane, BorderLayout.CENTER);

        Game_Rule120 rules = new Game_Rule120();
        rules.setRuleTextArea(ruleTextArea);
        rules.displayRules();  // Display game rules

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            returnToMainMenu();  // Return to main menu when the button is clicked
            rules.cancelTimer();  // Stop any running timers in the rules display
        });
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));  // Set button font

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        gamePanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().removeAll();  // Clear all components
        getContentPane().add(gamePanel);
        revalidate();
        repaint();
    }

    private void promoteToVIP(int playerIndex) {
        List<Player> players = gameSystem.getPlayers();
        if (playerIndex >= 0 && playerIndex < players.size()) {
            Player player = players.get(playerIndex);
            VIPPlayer vipPlayer = new VIPPlayer(player);
            gameSystem.getPlayers().set(playerIndex, vipPlayer);

            // Visible feedback to the player
            JOptionPane.showMessageDialog(this, player.getName() + " has been promoted to VIP status!", "Promotion", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the list to reflect changes
            refreshPlayerList();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid player selection.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void returnToMainMenu() {
        getContentPane().removeAll();
        getContentPane().add(mainMenuPanel);
        gamePanel = null;  // Clear game panel reference
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameLauncherGUI::new);  // Start the GUI in the event dispatch thread
    }
}

