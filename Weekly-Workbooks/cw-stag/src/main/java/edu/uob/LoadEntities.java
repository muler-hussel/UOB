package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoadEntities {
    private StaticData staticData;
    private Map<String, GameEntity> gameEntities;

    public LoadEntities(StaticData staticData) {
        this.staticData = staticData;
        this.gameEntities = staticData.getGameEntities();
    }

    public void loadEntities(File entitiesFile) {
        try {
            Reader entitiesReader = new FileReader(entitiesFile);
            Parser entitiesParser = new Parser();
            entitiesParser.parse(entitiesReader);
            //curGraph is layout
            for (Graph curGraph : entitiesParser.getGraphs()) {
                this.loadLocOrEdge(curGraph);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLocOrEdge(Graph curGraph) {
        //subGraph = locations or path
        for (Graph subGraph : curGraph.getSubgraphs()) {
            if (subGraph.getId().getId().equals("locations")) {
                this.createEntities(subGraph);
            } else {
                for (Edge curEdge : subGraph.getEdges()) {
                    this.setEdges(curEdge);
                }
            }
        }
        staticData.setGameEntities(gameEntities);
    }

    private void createEntities(Graph subGraph) {
        Set<String> basicAction = staticData.getBasicAction();
        //subLocation = cluster
        for (Graph subLocation : subGraph.getSubgraphs()) {
            Location curLocation = null;
            Node locNode = subLocation.getNodes(true).get(0); //real Location Class
            if (!locNode.isSubgraph()) {
                String locationName = locNode.getId().getId().toLowerCase();
                String locationDescription = locNode.getAttribute("description");
                if (basicAction.contains(locationName) || basicAction.contains(locationDescription)) {
                    continue;
                }
                curLocation = new Location(locationName, locationDescription);
                gameEntities.put(locationName, curLocation);
                if (subGraph.getSubgraphs().get(0).equals(subLocation)) { //first loaction
                    staticData.setStartLoc(curLocation);
                }
            }
            //different types of entity in location
            for (Graph entityInLoc : subLocation.getSubgraphs()) {
                if (curLocation != null) this.createEntity(entityInLoc, curLocation);
            }
        }
    }

    private void setEdges(Edge curEdge) {
        String sourceNodeId = curEdge.getSource().getNode().getId().getId().toLowerCase();
        String targetNodeId = curEdge.getTarget().getNode().getId().getId().toLowerCase();
        if (gameEntities.containsKey(sourceNodeId) && gameEntities.containsKey(targetNodeId)) {
            GameEntity source = gameEntities.get(sourceNodeId);
            GameEntity target = gameEntities.get(targetNodeId);
            if ((source instanceof Location) && (target instanceof Location)) {
                Set<Location> targetLocations = ((Location) source).getTargetLocations();
                targetLocations.add((Location) target);
            }
            gameEntities.put(sourceNodeId, source);
        }
    }

    private void createEntity(Graph entityInLoc, Location curLoc) {
        String entityType = entityInLoc.getId().getId();
        Set<GameEntity> entitiesInLoc = curLoc.getEntitiesInLoc();
        Set<String> basicAction = staticData.getBasicAction();

        for (Node curEntity : entityInLoc.getNodes(true)) { //entity in node
            String entityName = curEntity.getId().getId().toLowerCase();
            String entDescription = curEntity.getAttribute("description");
            if (basicAction.contains(entityName) || basicAction.contains(entDescription)) continue;
            GameEntity newEntity = null;
            switch (entityType) {
                case "artefacts":
                    newEntity = new Artefact(entityName, entDescription, curLoc);
                    break;
                case "furniture":
                    newEntity = new Furniture(entityName, entDescription, curLoc);
                    break;
                case "characters":
                    newEntity = new Character(entityName, entDescription, curLoc);
                    break;
            }
            gameEntities.put(entityName, newEntity);
            entitiesInLoc.add(newEntity);
        }
    }
}
