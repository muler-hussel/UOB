package edu.uob;

public class Furniture extends GameEntity{
    public Furniture(String name, String description) {
        super(name, description);
    }

    public Furniture(String name, String description, Location location) {
        super(name, description, location);
    }
}
