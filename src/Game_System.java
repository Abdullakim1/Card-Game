import java.util.ArrayList;
import java.util.List;

public class Game_System {
    private List<Player> players;
    private List<GameObserver> observers;

    public Game_System() {
        players = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public void addPlayer(String playerName) {
        players.add(new Player(playerName));
        notifyObservers();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void resetGame() {
        players.clear();
        notifyObservers();
    }

    public void addObserver(GameObserver observer) {
        if(observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.update();
        }
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public void startGame(){
        if(players.isEmpty()){
            throw new IllegalArgumentException("Cannot start a game without players.");
        }
    }
}
