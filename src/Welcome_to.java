import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

interface Beginning {
    String welcome();
}

public class Welcome_to implements Beginning {
    private String gameName;
    private String version;
    private LocalDateTime currentTime;

    public Welcome_to(String gameName, String version) {
        this.gameName = gameName;
        this.version = version;
        this.currentTime = LocalDateTime.now();
    }

    public Welcome_to() {

    }

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
}

class usage implements Beginning {
    @Override
    public String welcome() {
        return "Please wait for the system distribution!";
    }
}

class usage2 implements Beginning {
    @Override
    public String welcome() {
        return "Press Enter if you don't have the matching card";
    }
}
