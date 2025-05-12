package edu.uob;

import java.io.IOException;
import java.util.Map;

public class CreateDBController implements CommandHandleController{
    @Override
    public String handle(String command) throws IOException {
        Map<String, DataBase> allDBs = DBServer.getAllDBs();

        String[] commandSplit = command.replace(";", "").split("\\s+");
        if (commandSplit.length != 3) {
            return "[ERROR], command is invalid";
        }
        String dbName = commandSplit[2].toLowerCase();
        if (CommandVerifyUtils.isReservedWord(dbName) || !CommandVerifyUtils.isPlainText(dbName) || allDBs.containsKey(dbName)) {
            return "[ERROR], database name is invalid";
        }
        DataBase dataBase = new DataBase(dbName);
        allDBs.put(dbName, dataBase);
        DBServer.setAllDBs(allDBs);
        DataBaseIOUtils.createDBFolder(dataBase);
        return "[OK]";
    }
}
