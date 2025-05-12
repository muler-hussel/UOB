package edu.uob;

import java.util.*;

public class CommandHandleUtil {
    private StaticData staticData;
    private ExecuteAction executeAction;

    public CommandHandleUtil(StaticData staticData) {
        this.staticData = staticData;
        this.executeAction = new ExecuteAction(staticData);
    }

    public StringBuilder extractName(String command) {
        StringBuilder playerNameSb = new StringBuilder();
        boolean findColon = false;
        for (char commandChar : command.toCharArray()) {
            if (!findColon) playerNameSb.append(commandChar);
            if (commandChar == ':') findColon = true;
        }
        return playerNameSb;
    }

    public boolean validPlayerName(String playerName) {
        if (this.staticData.getBasicAction().contains(playerName)) {
            return false;
        }
        for (char c : playerName.toCharArray()) {
            if (!java.lang.Character.isLetter(c) && c != '\'' && c != '-' && c != ' ') {
                return false;
            }
        }
        return true;
    }

    public Player handlePlayer(String playerName) {
        Map<String, Player> allPlayer = this.staticData.getAllPlayer();
        Player curPlayer;
        if (allPlayer.containsKey(playerName)) {
            curPlayer = allPlayer.get(playerName);
        } else {
            curPlayer = new Player(playerName, null, this.staticData.getStartLoc());
            allPlayer.put(playerName, curPlayer);
            this.staticData.setAllPlayer(allPlayer);
        }
        return curPlayer;
    }

    public String searchAction(String inputCommand) {
        GameAction curAction = null;//custom action found
        String basicEntity = null; //entity for basic action to act
        String finalTrigger = null;
        Map<String, Set<GameAction>> cusAction = staticData.getCustomAction();

        for (String curTrigger : inputCommand.split("\\s+")) {
            if (staticData.getBasicAction().contains(curTrigger)) { //action is basic one
                List<String> basicEnts = this.checkBasicCommand(inputCommand, curTrigger);
                if (finalTrigger != null && basicEnts != null) {
                    return "You can only perform one action";
                } else if (basicEnts != null && !basicEnts.isEmpty()) {
                    basicEntity = basicEnts.get(0);
                }
                finalTrigger = curTrigger;
            } else if (cusAction.containsKey(curTrigger)) { //custom action
                List<GameAction> validAct = this.checkCusAct(inputCommand, cusAction.get(curTrigger));
                if (validAct.size() > 1 || (!validAct.isEmpty() && finalTrigger != null)) {
                    return "You can only perform one action";
                } else if (validAct.size() == 1) {
                    curAction = validAct.get(0);
                    finalTrigger = curTrigger;
                }
            }
        }
        if (finalTrigger == null) {
            return "No valid action found";
        }

        //basic action, only record trigger and entity to handle with
        if (curAction == null) {
            return this.executeBasicAction(finalTrigger, basicEntity);
        }
        //custom action
        return executeAction.customAction(curAction);
    }

    private List<String> checkBasicCommand(String inputCommand, String curTrigger) {
        Map<String, Set<? extends GameEntity>> command2Set = this.getStringSetMap();
        List<String> basicEnt = new LinkedList<>();

        if (Set.of("inventory", "inv", "look").contains(curTrigger)) {
            if (!inputCommand.replace(curTrigger,"").trim().isEmpty()) {
                return null;
            }
        } else {
            basicEnt = this.basicEntNum(inputCommand, command2Set.get(curTrigger));
            if (basicEnt.size() != 1) {
                return null;
            }
        }
        return basicEnt;
    }

    private Map<String, Set<? extends GameEntity>> getStringSetMap() {
        Player curPlayer = staticData.getCurPlayer();
        Set<Location> targetLocations = curPlayer.getLocation().getTargetLocations();
        Set<GameEntity> entitiesInLoc = curPlayer.getLocation().getEntitiesInLoc();
        Set<GameEntity> entInInv = curPlayer.getInventory().getEntitiesInLoc();
        return new HashMap<>(){{
            put("get", entitiesInLoc);
            put("drop", entInInv);
            put("goto", targetLocations);
        }};
    }

    private List<String> basicEntNum(String inputCommand, Set<? extends GameEntity> availableEnt) {
        List<String> basicEnt = new LinkedList<>();
        for (String entity : inputCommand.split("\\s+")) {
            if (!staticData.getGameEntities().containsKey(entity)) {
                continue;
            }
            GameEntity curEntity = staticData.getGameEntities().get(entity);
            if (availableEnt.contains(curEntity)) {
                basicEnt.add(entity);
            }
        }
        return basicEnt;
    }

    private List<GameAction> checkCusAct(String inputCommand, Set<GameAction> actionByTrigger) {
        List<GameAction> validActions = new LinkedList<>();
        //at least on subject
        for (GameAction action : actionByTrigger) {
            int amountOfSub = this.cusEntAmount(action, inputCommand);
            if (amountOfSub > 0) {
                validActions.add(action);
            }
        }
        return validActions;
    }

    private int cusEntAmount(GameAction action, String inputCommand) {
        Map<String, GameEntity> gameEntities = staticData.getGameEntities();
        //inventory or curLocation
        Player curPlayer = staticData.getCurPlayer();
        Set<GameEntity> entInInv = curPlayer.getInventory().getEntitiesInLoc();
        Set<GameEntity> entInLoc = curPlayer.getLocation().getEntitiesInLoc();

        Set<String> actSubjects = action.getSubjects();
        int numOfSub = 0;
        for (String curEnt : inputCommand.split("\\s+")) {
            //skip words that are not entities
            if (!gameEntities.containsKey(curEnt)) continue;
            //Extraneous entities, prevent a match
            if (!actSubjects.contains(curEnt)) return 0;
            GameEntity curEntity = gameEntities.get(curEnt);
            if (entInInv.contains(curEntity) || entInLoc.contains(curEntity)) {
                numOfSub++;
            }
        }
        return numOfSub;
    }

    private String executeBasicAction(String trigger, String curEntity) {
        GameEntity basicEntity = staticData.getGameEntities().get(curEntity);

        switch (trigger) {
            case "inventory":
            case "inv":
                return executeAction.inventory();
            case "get":
            case "drop":
                return executeAction.getOrDrop(trigger, basicEntity);
            case "goto":
                return executeAction.gotoLoc((Location) basicEntity);
            case "look":
                return executeAction.look();
            default:
                return "";
        }
    }
}
