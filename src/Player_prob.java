import java.util.List;

public class Player_prob<T> {

    public String calculateWinProbability(List<T> playerHand) {
        StringBuilder result = new StringBuilder();

        boolean hasBlackCard = playerHand.contains("Black");
        if (hasBlackCard) {
            result.append("Player has low winning chance\n");
        } else {
            int high = 0;
            int avg = 0;
            int low = 0;

            for (T card : playerHand) {
                String cardColor = card.toString();
                switch (cardColor) {
                    case "Red":
                    case "Blue":
                        high++;
                        break;
                    case "White":
                        avg++;
                        break;
                    case "Black":
                        low++;
                        break;
                }
            }
            if (high > avg && high > low) {
                result.append("Player has high winning chance\n");
            } else if (avg >= high && avg > low) {
                result.append("Player has average winning chance\n");
            } else {
                result.append("Player has low winning chance\n");
            }
        }
        return result.toString();
    }

    // Overloaded method to accept an array of strings
    public String calculateWinProbability(String[] playerHand) {
        return calculateWinProbability((List<T>) List.of(playerHand));
    }
}
