package edu.uob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoadAction {
    private StaticData staticData;
    Map<String, Set<GameAction>> customAction ;

    public LoadAction(StaticData staticData) {
        this.staticData = staticData;
        this.customAction = staticData.getCustomAction();
    }

    public void loadAction(File actionsFile) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile.getPath());
            NodeList actions = document.getDocumentElement().getChildNodes();
            for (int i = 1; i < actions.getLength(); i+=2) {
                Element action = (Element) actions.item(i);
                Element triggers = (Element) action.getElementsByTagName("triggers").item(0);
                //get keyPhrases
                NodeList keyPhrases = triggers.getElementsByTagName("keyphrase");
                this.createCusAct(keyPhrases, action);
            }
            staticData.setCustomAction(customAction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //figure out if are custom actions, then create
    private void createCusAct(NodeList keyPhrases, Element action) {
        Set<String> basicAction = staticData.getBasicAction();
        for (int j = 0; j < keyPhrases.getLength(); j++) {
            Set<GameAction> actionSet;
            Element keyPhrase = (Element) keyPhrases.item(j);
            //get trigger
            String curTrigger = keyPhrase.getTextContent().toLowerCase();
            //trigger should not be basic actions
            if (basicAction.contains(curTrigger)) {
                continue;
            }
            actionSet = customAction.getOrDefault(curTrigger, new HashSet<>());
            GameAction newAction = this.createAction(action);
            if (newAction != null) {
                actionSet.add(newAction);
            }
            //one trigger, a set of actions
            customAction.put(curTrigger, actionSet);
        }
    }

    //create new action
    private GameAction createAction(Element curAction) {
        Set<String> subjectsSet = new HashSet<>();
        Set<String> consumedSet = new HashSet<>();
        Set<String> producedSet = new HashSet<>();

        this.fillInSet("subjects", subjectsSet, curAction);
        this.fillInSet("consumed", consumedSet, curAction);
        this.fillInSet("produced", producedSet, curAction);

        Element narration = (Element) curAction.getElementsByTagName("narration").item(0);
        if (!subjectsSet.isEmpty()) {
            return new GameAction(subjectsSet, consumedSet, producedSet, narration.getTextContent());
        }
        return null;
    }

    //fill in subjects, consumed, produced entities of the action according to keyPhrases
    private void fillInSet(String tagName, Set<String> entitySet, Element curAction) {
        Element curEntities = (Element) curAction.getElementsByTagName(tagName).item(0);
        NodeList entityList = curEntities.getElementsByTagName("entity");
        for (int i = 0; i < entityList.getLength(); i++) {
            Element curEntity = (Element) entityList.item(i);
            entitySet.add(curEntity.getTextContent().toLowerCase());
        }
    }
}
