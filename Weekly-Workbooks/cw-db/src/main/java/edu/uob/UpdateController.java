package edu.uob;

import java.io.IOException;
import java.util.*;
public class UpdateController implements CommandHandleController{
    @Override
    public String handle(String command) throws IOException {
        String currentDB = DBServer.getCurrentDB();
        Map<String, DataBase> allDBs = DBServer.getAllDBs();

        String[] commands = command.replace(";", "")
                .replace("'", "")
                .replace("\"", "")
                .split(" (?i)SET ");
        if (commands.length != 2) {
            return "[ERROR], command is invalid";
        }

        String[] command1 = commands[0].split("\\s+");
        if (!command1[0].equalsIgnoreCase("UPDATE")) {
            return "[ERROR], command is invalid";
        }
        String tableName = command1[1];
        if (currentDB == null || !allDBs.get(currentDB).getTableInDB().containsKey(tableName)) {
            return "[ERROR], table name is invalid";
        }
        Table curTable = allDBs.get(currentDB).getTableInDB().get(tableName);

        String[] command2 = commands[1].split(" (?i)WHERE ");
        String condition = command2[1];
        List<String> attrNameAndValue = new ArrayList<>();
        Deque<String> operator = new LinkedList<>();
        List<Integer> selectedRows = new ArrayList<>();
        if (!ConditionHandleUtils.isConditionValid(condition, curTable, attrNameAndValue, operator)) {
            return "[ERROR], command is invalid";
        } else if (!ConditionHandleUtils.handleCondition(curTable, attrNameAndValue, selectedRows)) {
            return "[ERROR], command is invalid";
        }

        String[] nameValueList = command2[0].split(" , ");
        if (!updateNameValue(curTable, nameValueList, selectedRows)) {
            return "[ERROR], command is invalid";
        }
        DataBaseIOUtils.writeTable2File(allDBs.get(currentDB), curTable);
        return "[OK]";
    }

    private boolean updateNameValue(Table table, String[] nameValueList, List<Integer> selectedRows){
        List<String> colNames = table.getColNames();
        for (String s : nameValueList) {
            String[] nameValue = s.split(" = ");
            if (nameValue.length != 2 || !CommandVerifyUtils.isAttrName(nameValue[0], table)) {
                return false;
            }
            int colIdx = colNames.indexOf(nameValue[0]);
            if (colIdx == 0) {
                return false;
            }
            for (int i : selectedRows) {
                table.getRows().get(i).set(colIdx, nameValue[1]);
            }
        }
        return true;
    }
}
