package edu.uob;

import java.io.IOException;
import java.util.Map;

public class DropTableController implements CommandHandleController{
    @Override
    public String handle(String command) throws IOException {
        String currentDB = DBServer.getCurrentDB();
        Map<String, DataBase> allDBs = DBServer.getAllDBs();

        String[] commandSplit = command.replace(";", "").split("\\s+");
        if (commandSplit.length != 3) return "[ERROR], command is invalid";
        String tableName = commandSplit[2];
        if (currentDB == null || !allDBs.get(currentDB).getTableInDB().containsKey(tableName)) {
            return "[ERROR], table name is invalid";
        }
        Map<String, Table> tableInDB = allDBs.get(currentDB).getTableInDB();
        Table curTable = tableInDB.get(tableName);

        if (!DataBaseIOUtils.deleteTableFile(allDBs.get(currentDB), curTable)) {
            return "[ERROR], fail to delete table";
        }

        curTable.clearTable();
        tableInDB.remove(tableName);
        allDBs.get(currentDB).setTableInDB(tableInDB);
        DBServer.setAllDBs(allDBs);
        return "[OK]";
    }
}
