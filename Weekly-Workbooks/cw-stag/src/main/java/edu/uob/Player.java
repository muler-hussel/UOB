package edu.uob;

import java.util.HashSet;
import java.util.Set;

public class Player extends Character{
    private int health = 3;
    private Inventory inventory;
    public Player(String name, String description) {
        super(name, description);
        this.inventory = new Inventory(name, null);
    }

    public Player(String name, String description, Location location) {
        super(name, description, location);
        this.inventory = new Inventory(name, null);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
