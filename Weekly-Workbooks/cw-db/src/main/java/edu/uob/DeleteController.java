package edu.uob;

import java.io.IOException;
import java.util.*;

public class DeleteController implements CommandHandleController{
    @Override
    public String handle(String command) throws IOException {
        String currentDB = DBServer.getCurrentDB();
        Map<String, DataBase> allDBs = DBServer.getAllDBs();

        String[] commands = command.replace(";", "")
                .replace("'", "")
                .replace("\"", "")
                .split(" (?i)FROM ");

        if (commands.length != 2 || !commands[0].equalsIgnoreCase("DELETE")) {
            return "[ERROR], command is invalid";
        }

        String[] commandSplit = commands[1].split(" (?i)WHERE ");
        if (commandSplit.length != 2) {
            return "[ERROR], command is invalid";
        }

        String tableName = commandSplit[0];
        if (currentDB == null || !allDBs.get(currentDB).getTableInDB().containsKey(tableName)) {
            return "[ERROR], table name is invalid";
        }
        Table curTable = allDBs.get(currentDB).getTableInDB().get(tableName);

        String condition = commandSplit[1];
        List<String> attrNameAndValue = new ArrayList<>();
        Deque<String> operator = new LinkedList<>();
        List<Integer> selectedRows = new ArrayList<>();
        if (!ConditionHandleUtils.isConditionValid(condition, curTable, attrNameAndValue, operator)) {
            return "[ERROR], command is invalid";
        } else if (!ConditionHandleUtils.handleCondition(curTable, attrNameAndValue, selectedRows)) {
            return "[ERROR], command is invalid";
        }

        List<List<String>> rows = curTable.getRows();
        selectedRows.sort((a, b) -> b - a);
        for (int i : selectedRows) {
            rows.remove(i);
        }
        curTable.setRows(rows);
        DataBaseIOUtils.writeTable2File(allDBs.get(currentDB), curTable);
        return "[OK]";
    }
}
