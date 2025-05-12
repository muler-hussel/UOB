package edu.uob;

public class Character extends GameEntity{
    private Player player;

    public Character(String name, String description) {
        super(name, description);
    }
    public Character(String name, String description, Location location) {
        super(name, description, location);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
