package edu.uob;

import java.io.IOException;
import java.util.Map;

public class DropDBController implements CommandHandleController{
    @Override
    public String handle(String command) throws IOException {
        Map<String, DataBase> allDBs = DBServer.getAllDBs();

        String[] commandSplit = command.replace(";", "").split("\\s+");
        if (commandSplit.length != 3) return "[ERROR], command is invalid";
        String dbName = commandSplit[2];
        if (!allDBs.containsKey(dbName)) {
            return "[ERROR], database not exist";
        }

        DataBase curDB = allDBs.get(dbName);
        Map<String, Table> tableInDB = curDB.getTableInDB();
        for (Table table : tableInDB.values()) {
            table.clearTable();
            if (!DataBaseIOUtils.deleteTableFile(curDB, table)) {
                return "[ERROR], fail to delete table";
            }
        }
        allDBs.remove(dbName);
        DBServer.setAllDBs(allDBs);
        if (!DataBaseIOUtils.deleteFolder(curDB)) {
            return "[ERROR], fail to delete database";
        }
        return "[OK]";
    }
}
