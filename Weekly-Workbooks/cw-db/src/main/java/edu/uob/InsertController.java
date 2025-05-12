package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsertController implements CommandHandleController{

    @Override
    public String handle(String command) throws IOException {
        String currentDB = DBServer.getCurrentDB();
        Map<String, DataBase> allDBs = DBServer.getAllDBs();

        String[] commandSplit = command.replace(";", "")
                .replace("'", "")
                .replace("\"", "")
                .split(" (?i)VALUES ");

        if (commandSplit.length != 2) {
            return "[ERROR], table name is invalid";
        }

        String[] tableNamePart = commandSplit[0].split("\\s+");
        if (tableNamePart.length != 3
                || !tableNamePart[0].equalsIgnoreCase("INSERT")
                || !tableNamePart[1].equalsIgnoreCase("INTO")) {
            return "[ERROR], table name is invalid";
        }
        String tableName = tableNamePart[2];
        if (currentDB == null || !allDBs.get(currentDB).getTableInDB().containsKey(tableName)) {
            return "[ERROR], table name is invalid";
        }
        Table curTable = allDBs.get(currentDB).getTableInDB().get(tableName);

        String[] valueList = commandSplit[1].replace("(", "")
                .replace(")", "")
                .replace("'", "")
                .replace(",", "")
                .split("\\s+");
        if (valueList.length != curTable.getColNames().size() - 1) {
            return "[ERROR], please type in correct numbers of values";
        }
        insertRow(valueList, curTable);
        DataBaseIOUtils.writeTable2File(allDBs.get(currentDB), curTable);
        return "[OK]";
    }

    private static void insertRow(String[] commandSplit, Table curTable) {
        int currentId = curTable.getCurrentId();
        curTable.setCurrentId(currentId + 1);
        List<String> valueList = new ArrayList<>();
        valueList.add(String.valueOf(currentId));
        for (int i = 0; i < commandSplit.length; i++) {
            valueList.add(commandSplit[i]);
        }
        List<List<String>> rows = curTable.getRows();
        rows.add(valueList);
        curTable.setRows(rows);
    }
}
