import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

interface Beginning {
    String welcome();
}

public class Welcome_to implements Beginning {
    // Private fields to enforce encapsulation
    private String gameName;
    private String version;
    private LocalDateTime currentTime;

    // Constructor to initialize the class with necessary information
    public Welcome_to(String gameName, String version) {
        setGameName(gameName);
        setVersion(version);
        this.currentTime = LocalDateTime.now();
    }

    // Public method to get the game name
    public String getGameName() {
        return gameName;
    }

    // Private method to set the game name, enforcing information hiding
    private void setGameName(String gameName) {
        if (gameName == null || gameName.isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be null or empty.");
        }
        this.gameName = gameName;
    }

    // Public method to get the version
    public String getVersion() {
        return version;
    }

    // Private method to set the version, enforcing information hiding
    private void setVersion(String version) {
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("Version cannot be null or empty.");
        }
        this.version = version;
    }

    // Public method to get the current time
    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    // Private method to set the current time, which is initialized once in the constructor
    private void setCurrentTime(LocalDateTime currentTime) {
        if (currentTime == null) {
            throw new IllegalArgumentException("Current time cannot be null.");
        }
        this.currentTime = currentTime;
    }

    // Override the welcome method to provide a formatted welcome message
    @Override
    public String welcome() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a");
        String formattedDate = currentTime.format(formatter);

        StringBuilder welcomeMessage = new StringBuilder();
        welcomeMessage.append("******************************************\n");
        welcomeMessage.append("**                                      **\n");
        welcomeMessage.append("**         Welcome to ").append(gameName).append("        **\n");
        welcomeMessage.append("**                                      **\n");
        welcomeMessage.append("******************************************\n");
        welcomeMessage.append("\n");
        welcomeMessage.append("Version: ").append(version).append("\n");
        welcomeMessage.append("We are delighted to see you!\n");
        welcomeMessage.append("Current date and time: ").append(formattedDate).append("\n");
        welcomeMessage.append("\n");
        welcomeMessage.append("Enjoy your game and have fun!\n");
        welcomeMessage.append("Powered by Abdullakim Zamirbek uulu\n");
        welcomeMessage.append("\n");
        welcomeMessage.append("******************************************");

        return welcomeMessage.toString();
    }

    public void displayAndStartGame() {
        String welcomeMessage = welcome();
        JOptionPane pane = new JOptionPane(welcomeMessage, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{},null);
        JDialog dialog = pane.createDialog("Welcome");
        dialog.setAlwaysOnTop(true);
        dialog.setModal(false);
        dialog.setVisible(true);
        new Timer(3000, e -> dialog.dispose()).start();

    }
}

