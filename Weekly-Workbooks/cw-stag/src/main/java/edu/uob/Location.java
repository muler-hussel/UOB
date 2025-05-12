package edu.uob;

import java.util.HashSet;
import java.util.Set;

public class Location extends GameEntity{
    private Set<Location> targetLocations = new HashSet<>();
    private Set<GameEntity> entitiesInLoc = new HashSet<>();

    public Location(String name, String description) {
        super(name, description);
    }

    public Set<GameEntity> getEntitiesInLoc() {
        return entitiesInLoc;
    }

    public void setEntitiesInLoc(Set<GameEntity> entitiesInLoc) {
        this.entitiesInLoc = entitiesInLoc;
    }

    public Set<Location> getTargetLocations() {
        return targetLocations;
    }

    public void setTargetLocations(Set<Location> targetLocations) {
        this.targetLocations = targetLocations;
    }
}
