package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Map;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    private LoadEntities loadEntities;
    private LoadAction loadAction;
    private CommandHandleUtil commandUtil;
    private StaticData staticData;

    public static void main(String[] args) throws IOException {
        StringBuilder entitiesSb = new StringBuilder();
        entitiesSb.append("config").append(File.separator).append("basic-entities.dot");
        StringBuilder actionsSb = new StringBuilder();
        actionsSb.append("config").append(File.separator).append("basic-actions.xml");
        File entitiesFile = Paths.get(entitiesSb.toString()).toAbsolutePath().toFile();
        File actionsFile = Paths.get(actionsSb.toString()).toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Instanciates a new server instance, specifying a game with some configuration files
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    */
    public GameServer(File entitiesFile, File actionsFile) {
        staticData = new StaticData();
        loadEntities = new LoadEntities(staticData);
        loadAction = new LoadAction(staticData);

        loadEntities.loadEntities(entitiesFile);
        loadAction.loadAction(actionsFile);
        Map<String, GameEntity> gameEntities = staticData.getGameEntities();
        if (!gameEntities.containsKey("storeroom")) {{
            gameEntities.put("storeroom", new Location("storeroom", null));
        }}
        staticData.setGameEntities(gameEntities);
        commandUtil = new CommandHandleUtil(staticData);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        StringBuilder playerName = commandUtil.extractName(command);
        String actionCommand = command.replace(playerName, "").trim();
        String playName = playerName.toString().replace(":", "").trim();
        if (!commandUtil.validPlayerName(playName)) {
            return "Player name is invalid";
        }
        staticData.setCurPlayer(commandUtil.handlePlayer(playName));
        return commandUtil.searchAction(actionCommand);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Starts a *blocking* socket server listening for new connections.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            StringBuilder portNum = new StringBuilder();
            portNum.append("Server listening on port ").append(portNumber);
            System.out.println(portNum);
            while (!Thread.interrupted()) {
                try {
                    this.blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                StringBuilder receiveMess = new StringBuilder();
                receiveMess.append("Received message from ").append(incomingCommand);
                System.out.println(receiveMess);
                String result = this.handleCommand(incomingCommand);
                writer.write(result);
                StringBuilder outPut = new StringBuilder();
                outPut.append("\n").append(END_OF_TRANSMISSION).append("\n");
                writer.write(outPut.toString());
                writer.flush();
            }
        }
    }
}
