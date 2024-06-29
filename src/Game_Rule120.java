abstract class game_rule {
    abstract String gen_rule();
    abstract String rule_one();
    abstract String rule_two();
    abstract String rule_three();
}
public class Game_Rule120 extends game_rule{
    public String gen_rule(){
        return "General rules of the game!";
    }
    public String rule_one(){
        return "If you have black card you lose, but keep playing until the end";
    }
    public String rule_two(){
        return "If you won't put the card estimated time frame, you lose";
    }
    public String rule_three(){
        return "The time that is allocated for each player is 10 sec!";
    }
}
