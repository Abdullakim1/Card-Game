import java.util.List;

public class prob_win_player2 extends Player_prob<String> {

    public String prob_win_player2(List<String> Hand2) {
        String result = calculateWinProbability(Hand2);
        return "Probability for Player 2: \n\n" + result;
    }

    // Overloaded method to accept an array of strings
    public String prob_win_player2(String[] Hand2) {
        String result = calculateWinProbability(Hand2);
        return "Probability for Player 2: \n\n" + result;
    }
}
