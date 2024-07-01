import java.util.List;

public class prob_win_player1 extends Player_prob<String> {

    public String prob_win_player1(List<String> Hand1) {
        String result = calculateWinProbability(Hand1);
        return "Probability for Player 1: \n\n" + result;
    }

    // Overloaded method to accept an array of strings
    public String prob_win_player1(String[] Hand1) {
        String result = calculateWinProbability(Hand1);
        return "Probability for Player 1: \n\n" + result;
    }
}
