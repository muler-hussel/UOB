package edu.uob;

import java.util.Map;
import java.util.Set;
public class ExecuteAction {
    private StaticData staticData;
    private Map<String, GameEntity> gameEntities;

    public ExecuteAction(StaticData staticData) {
        this.staticData = staticData;
        this.gameEntities = staticData.getGameEntities();
    }

    public String inventory() {
        Set<GameEntity> entInInv = staticData.getCurPlayer().getInventory().getEntitiesInLoc();
        StringBuilder respond = new StringBuilder();
        for (GameEntity gameEntity : entInInv) {
            respond.append(gameEntity.getDescription()).append('\n');
        }
        return respond.toString();
    }

    public String getOrDrop(String curTrigger, GameEntity gameEntity) {
        if (gameEntity instanceof Furniture || gameEntity instanceof Location) {
            return "Fail to act";
        }
        Player curPlayer = staticData.getCurPlayer();
        Location curLoc = curPlayer.getLocation();
        Location playerInv = curPlayer.getInventory();

        if (curTrigger.equals("get")) {
            return this.getDropFunc(curLoc, playerInv, gameEntity, "pick up ");
        }
        return this.getDropFunc(playerInv, curLoc, gameEntity, "drop ");
    }

    private String getDropFunc(Location prevLoc, Location newLoc, GameEntity gameEnt, String inPuts) {
        StringBuilder respond = new StringBuilder();

        if (!this.remove(prevLoc, newLoc, gameEnt)) {
            return respond.append("Fail to ").append(inPuts).toString();
        }

        respond.append("You ").append(inPuts);
        return respond.append(gameEnt.getName()).toString();
    }

    public String gotoLoc(Location target) {
        staticData.getCurPlayer().setLocation(target);
        return this.look();
    }

    public String look() {
        Location curLoc = staticData.getCurPlayer().getLocation();
        StringBuilder respond = new StringBuilder();
        String locationDescription = curLoc.getDescription();

        respond.append("You are in ").append(locationDescription).append(" You can see:").append('\n');
        for (GameEntity gameEntity : curLoc.getEntitiesInLoc()) {
            respond.append(gameEntity.getDescription()).append('\n');
        }
        respond.append("You can access from here:").append('\n');
        for (Location target : curLoc.getTargetLocations()) {
            respond.append(target.getName()).append('\n');
        }
        return respond.toString();
    }

    public String customAction(GameAction curAction) {
        Player curPlayer = staticData.getCurPlayer();
        Set<String> consumeEntity = curAction.getConsumed();
        Set<String> produceEntity = curAction.getProduced();
        StringBuilder actRespond = new StringBuilder();
        if (!this.consumeEnt(consumeEntity, actRespond, curPlayer)) {
            return "Action is invalid";
        }
        if (!this.produceEnt(produceEntity, actRespond, curPlayer)) {
            return "Action is invalid";
        }

        return actRespond.append(curAction.getNarration()).toString();
    }

    private boolean consumeEnt(Set<String> conEnt, StringBuilder actRes, Player curPlayer) {
        for (String consumeName : conEnt) {
            if (consumeName.equals("health")) {
                this.changeHealth(curPlayer, actRes, -1);
                continue;
            }

            if (!gameEntities.containsKey(consumeName)) {
                return false;
            }
            GameEntity consumeEnt = gameEntities.get(consumeName);
            if (consumeEnt instanceof Location) {
                Set<Location> targetLocations = curPlayer.getLocation().getTargetLocations();
                targetLocations.remove((Location) consumeEnt);
            } else {
                Location storeroom = (Location) staticData.getGameEntities().get("storeroom");
                if (!this.remove(consumeEnt.getLocation(), storeroom, consumeEnt)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean produceEnt(Set<String> proEnts, StringBuilder actRes, Player curPlayer) {
        for (String proName : proEnts) {
            if (proName.equals("health")) {
                this.changeHealth(curPlayer, actRes, 1);
                continue;
            }
            if (!gameEntities.containsKey(proName)) {
                return false;
            }
            GameEntity proEnt = gameEntities.get(proName);
            Location curLocation = curPlayer.getLocation();
            if (proEnt instanceof Location) {
                Set<Location> targetLocations = curLocation.getTargetLocations();
                targetLocations.add((Location) proEnt);
            } else {
                Location prevLoc = proEnt.getLocation();
                if (!this.remove(prevLoc, curLocation, proEnt)) {
                    return false;
                }
            }
        }
        return true;
    }

    //-1 is consuming, 1 is produce
    private void changeHealth(Player curPlayer, StringBuilder actRespond, int changeType) {
        int playerHealth = curPlayer.getHealth();
        playerHealth += changeType;
        if (playerHealth == 0) {
            actRespond.append(this.playerDie(curPlayer));
        } else if (playerHealth > 3) {
            playerHealth = 3;
        }
        curPlayer.setHealth(playerHealth);
        actRespond.append("current health").append(playerHealth).append('\n');
    }

    private String playerDie(Player curPlayer) {
        Location playerInv = curPlayer.getInventory();
        Set<GameEntity> entInInv = playerInv.getEntitiesInLoc();
        Location curLocation = curPlayer.getLocation();

        for (GameEntity invEnt : entInInv) {
            this.remove(playerInv, curLocation, invEnt);
        }
        curPlayer.setLocation(staticData.getStartLoc());
        curPlayer.setHealth(3);
        return "you died and lost all of your items, you must return to the start of the game";
    }

    private boolean remove(Location prevLoc, Location newLoc, GameEntity curEnt) {
        Set<GameEntity> entitiesInPrev = prevLoc.getEntitiesInLoc();
        Set<GameEntity> entitiesInNew = newLoc.getEntitiesInLoc();
        if (!entitiesInPrev.contains(curEnt) || entitiesInNew.contains(curEnt)) {
            return false;
        }
        entitiesInPrev.remove(curEnt);
        entitiesInNew.add(curEnt);
        curEnt.setLocation(newLoc);
        return true;
    }
}
