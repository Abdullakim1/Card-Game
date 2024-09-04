public class PlayerDecorator extends Player {
    protected Player decoratedPlayer;

    public PlayerDecorator(Player player) {
        super(player.getName());
        this.decoratedPlayer = player;
    }

    @Override
    public void addScore(int points){
        decoratedPlayer.addScore(points);
    }

    @Override
    public int getScore(){
        return decoratedPlayer.getScore();
    }

    @Override
    public String getName(){
        return decoratedPlayer.getName();
    }
}
