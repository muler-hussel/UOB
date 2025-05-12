package edu.uob;

public abstract class GameEntity
{
    private String name;
    private String description;
    private Location location;

    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public GameEntity(String name, String description, Location location) {
        this.name = name;
        this.description = description;
        this.location = location;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
