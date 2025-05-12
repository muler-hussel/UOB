package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;
    private static Map<String, DataBase> allDBs = new HashMap<>();
    private static String currentDB;
    private static final String[] allComparators = {"==", ">", "<", ">=", "<=", "!=", "LIKE"};
    private static final Map<String, CommandHandleController> commandHandlers = new HashMap<>(){{
        put("USE", new UseController());
        put("CREATEDATABASE", new CreateDBController());
        put("CREATETABLE", new CreateTableController());
        put("INSERT", new InsertController());
        put("SELECT", new SelectController());
        put("UPDATE", new UpdateController());
        put("ALTER", new AlterController());
        put("DELETE", new DeleteController());
        put("DROPDATABASE", new DropDBController());
        put("DROPTABLE", new DropTableController());
        put("JOIN", new JoinController());
    }};

    public static Map<String, DataBase> getAllDBs() {
        return allDBs;
    }

    public static void setAllDBs(Map<String, DataBase> allDBs) {
        DBServer.allDBs = allDBs;
    }

    public static String getCurrentDB() {
        return currentDB;
    }

    public static void setCurrentDB(String currentDB) {
        DBServer.currentDB = currentDB;
    }

    public static String[] getAllComparators() {
        return allComparators;
    }

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        DataBaseIOUtils.loadAllDBs();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) throws IOException {
        if (command.charAt(command.length() - 1) != ';') return "[ERROR], command should end with ';'";

        for (String comparator : allComparators) {
            command = command.replace(comparator, " " + comparator + " ");
        }

        String[] commandSplit = command.replace(";", "").split("\\s+");
        String commandType = commandSplit[0].toUpperCase();
        String commandKey = commandType;
        if (commandType.equals("CREATE") || commandType.equals("DROP")) {
            String createType = commandSplit[1].toUpperCase();
            if (createType.equals("DATABASE") || createType.equals("TABLE")) {
                commandKey += createType;
            }
        }

        CommandHandleController commandHandler = commandHandlers.get(commandKey);
        if (commandHandler != null) {
            return commandHandler.handle(command);
        }
        return "[ERROR]";
    }



    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
