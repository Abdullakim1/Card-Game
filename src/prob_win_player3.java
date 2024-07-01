import java.util.List;

public class prob_win_player3 extends Player_prob<String> {

    public String prob_win_player3(List<String> Hand3) {
        String result = calculateWinProbability(Hand3);
        return "Probability for Player 3: \n\n" + result;
    }

    // Overloaded method to accept an array of strings
    public String prob_win_player3(String[] Hand3) {
        String result = calculateWinProbability(Hand3);
        return "Probability for Player 3: \n\n" + result;
    }
}

