package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateTableController implements CommandHandleController{
    @Override
    public String handle(String command) throws IOException {
        String currentDB = DBServer.getCurrentDB();
        Map<String, DataBase> allDBs = DBServer.getAllDBs();

        String[] commandSplit = command.replace(";", "").split("\\s+");

        String tableName = commandSplit[2].toLowerCase();
        if (currentDB == null || CommandVerifyUtils.isReservedWord(tableName) || !CommandVerifyUtils.isPlainText(tableName)) {
            return "[ERROR], table name is invalid";
        }
        if (allDBs.get(currentDB).getTableInDB().containsKey(tableName)) {
            return "[ERROR], table already exist";
        }

        Table newTable;
        if (commandSplit.length == 3) {
            newTable = new Table(tableName, null);
        } else {
            List<String> colNames = new ArrayList<>();
            colNames.add("id");
            for (int i = 3; i < commandSplit.length; i++) {
                colNames.add(commandSplit[i].replace("(", "")
                        .replace(")", "")
                        .replace(",", "")
                        .trim());
            }
            newTable = new Table(tableName, colNames);
        }
        Map<String, Table> tableInDB = allDBs.get(currentDB).getTableInDB();
        tableInDB.put(tableName, newTable);
        allDBs.get(currentDB).setTableInDB(tableInDB);
        DataBaseIOUtils.writeTable2File(allDBs.get(currentDB), newTable);
        return "[OK]";
    }
}
