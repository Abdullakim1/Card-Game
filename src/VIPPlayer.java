public class VIPPlayer extends PlayerDecorator {
    public VIPPlayer(Player player) {
        super(player);
    }

    @Override
    public void addScore(int points){
        super.addScore(points*2); // VIP players get double points
    }
}
