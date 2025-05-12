package edu.uob;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AlterController implements CommandHandleController{
    @Override
    public String handle(String command) throws IOException {
        String currentDB = DBServer.getCurrentDB();
        Map<String, DataBase> allDBs = DBServer.getAllDBs();

        String[] commandSplit = command.replace(";", "").split("\\s+");

        if (commandSplit.length != 5 || !commandSplit[1].equalsIgnoreCase("TABLE")) {
            return "[ERROR], command is invalid";
        }

        String tableName = commandSplit[2];
        if (currentDB == null || !allDBs.get(currentDB).getTableInDB().containsKey(tableName)) {
            return "[ERROR], table name is invalid";
        }
        Table curTable = allDBs.get(currentDB).getTableInDB().get(tableName);

        String alterationType = commandSplit[3].toUpperCase();
        if (!alterationType.equals("ADD") && !alterationType.equals("DROP")) {
            return "[ERROR], alteration type is invalid";
        }

        String attrName = commandSplit[4];
        if (!CommandVerifyUtils.isPlainText(attrName)) {
            return "[ERROR], attribute name is invalid";
        }

        switch (alterationType) {
            case "ADD":
                if (CommandVerifyUtils.isAttrName(attrName, curTable)) {
                    return "[ERROR], attribute name exists";
                }
                addCol(curTable, attrName);
                break;
            case "DROP":
                if (!CommandVerifyUtils.isAttrName(attrName, curTable)) {
                    return "[ERROR], attribute name does not exist";
                }
                if (attrName.equalsIgnoreCase("id")) {
                    return "[ERROR], cannot delete id";
                }
                deleteCol(curTable, attrName);
                break;
        }
        DataBaseIOUtils.writeTable2File(allDBs.get(currentDB), curTable);
        return "[OK]";
    }

    private void addCol(Table table, String attrName) {
        List<String> colNames = table.getColNames();
        colNames.add(attrName);
        table.setColNames(colNames);

        List<List<String>> rows = table.getRows();
        for (List<String> row : rows) {
            row.add("");
        }
        table.setRows(rows);
    }

    private void deleteCol(Table table, String attrName) {
        List<String> colNames = table.getColNames();
        int colIdx = colNames.indexOf(attrName);
        colNames.remove(attrName);
        table.setColNames(colNames);

        List<List<String>> rows = table.getRows();
        for (List<String> row : rows) {
            row.remove(colIdx);
        }
        table.setRows(rows);
    }
}
