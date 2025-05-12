package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StaticData {
    private Map<String, GameEntity> gameEntities = new HashMap<>();

    private final Set<String> basicAction = new HashSet<>(){{
        add("inventory");
        add("inv");
        add("get");
        add("drop");
        add("goto");
        add("look");
    }};
    private Map<String, Set<GameAction>> customAction = new HashMap<>();

    private Map<String, Player> allPlayer = new HashMap<>();

    private Player curPlayer = null;

    private Location startLoc = null;

    public StaticData() {
    }

    public Map<String, GameEntity> getGameEntities() {
        return gameEntities;
    }

    public void setGameEntities(Map<String, GameEntity> gameEntities) {
        this.gameEntities = gameEntities;
    }

    public Set<String> getBasicAction() {
        return basicAction;
    }

    public Map<String, Set<GameAction>> getCustomAction() {
        return customAction;
    }

    public void setCustomAction(Map<String, Set<GameAction>> customAction) {
        this.customAction = customAction;
    }

    public Map<String, Player> getAllPlayer() {
        return allPlayer;
    }

    public void setAllPlayer(Map<String, Player> allPlayer) {
        this.allPlayer = allPlayer;
    }

    public Player getCurPlayer() {
        return curPlayer;
    }

    public void setCurPlayer(Player curPlayer) {
        this.curPlayer = curPlayer;
    }

    public Location getStartLoc() {
        return startLoc;
    }

    public void setStartLoc(Location startLoc) {
        this.startLoc = startLoc;
    }
}
