package edu.uob;

public class Artefact extends GameEntity{
    private Player player;

    public Artefact(String name, String description) {
        super(name, description);
    }
    public Artefact(String name, String description, Location location) {
        super(name, description, location);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
