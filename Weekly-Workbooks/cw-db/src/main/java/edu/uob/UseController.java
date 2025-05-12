package edu.uob;

import java.io.IOException;
import java.util.Map;

public class UseController implements CommandHandleController{
    @Override
    public String handle(String command) throws IOException {
        Map<String, DataBase> allDBs = DBServer.getAllDBs();

        String[] commandSplit = command.replace(";", "").split("\\s+");

        if (commandSplit.length != 2) {
            return "[ERROR], command is invalid";
        }
        String dbName = commandSplit[1];
        if (!allDBs.containsKey(dbName)) {
            return "[ERROR], database name is invalid";
        }
        DBServer.setCurrentDB(dbName);
        return "[OK]";
    }
}
