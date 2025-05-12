package edu.uob;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JoinController implements CommandHandleController{
    @Override
    public String handle(String command) throws IOException {
        String currentDB = DBServer.getCurrentDB();
        Map<String, DataBase> allDBs = DBServer.getAllDBs();

        String[] commandSplit = command.replace(";", "")
                .split(" (?i)ON ");
        if (commandSplit.length != 2) return "[ERROR], command is invalid";

        String[] tableNames = commandSplit[0].split("\\s+");
        if (tableNames.length != 4
                || !tableNames[0].equalsIgnoreCase("JOIN")
                || !tableNames[2].equalsIgnoreCase("AND")) {
            return "[ERROR], command is invalid";
        }
        String tableNameLeft = tableNames[1];
        String tableNameRight = tableNames[3];
        if (currentDB == null
                || !allDBs.get(currentDB).getTableInDB().containsKey(tableNameLeft)
                || !allDBs.get(currentDB).getTableInDB().containsKey(tableNameRight)
                || tableNameLeft.equals(tableNameRight)) {
            return "[ERROR], table name is invalid";
        }
        Table tableLeft = allDBs.get(currentDB).getTableInDB().get(tableNameLeft);
        Table tableRight = allDBs.get(currentDB).getTableInDB().get(tableNameRight);

        String[] attrNames = commandSplit[1].split(" (?i)AND ");
        if (attrNames.length != 2) return "[ERROR], attribute name is invalid";
        String attrLeft = attrNames[0];
        String attrRight = attrNames[1];
        if (!CommandVerifyUtils.isAttrName(attrLeft, tableLeft) || !CommandVerifyUtils.isAttrName(attrRight, tableRight)) {
            return "[ERROR], attribute name is invalid";
        }

        return joinTables(tableLeft, tableRight, attrLeft, attrRight);
    }

    private String joinTables(Table tableLeft, Table tableRight, String attrLeft, String attrRight) {
        List<String> colsLeft = tableLeft.getColNames();
        List<String> colsRight = tableRight.getColNames();
        String tableNameLeft = tableLeft.getTableName();
        String tableNameRight = tableRight.getTableName();
        int colLeft = colsLeft.indexOf(attrLeft);
        int colRight = colsRight.indexOf(attrRight);

        StringBuilder ret = new StringBuilder();
        ret.append("id").append("\t");
        for (String col : colsLeft) {
            if (!col.equals(attrLeft) && !col.equals("id")) {
                ret.append(tableNameLeft + '.' + col).append(("\t"));
            }
        }
        for (String col : colsRight) {
            if (!col.equals(attrRight) && !col.equals("id")) {
                ret.append(tableNameRight + '.' + col).append(("\t"));
            }
        }

        ret.append("\n");
        joinRows(tableLeft, tableRight, colLeft, colRight, ret);

        return "[OK]" + ret;
    }

    private void joinRows(Table tableLeft, Table tableRight, int colLeft, int colRight, StringBuilder ret) {
        List<List<String>> rowsLeft = tableLeft.getRows();
        List<List<String>> rowsRight = tableRight.getRows();
        int curId = 1;

        for (List<String> row : rowsLeft) {
            String matchCol = row.get(colLeft);
            for (List<String> r : rowsRight) {
                if (r.get(colRight).equals(matchCol)) {
                    ret.append(curId++).append("\t");
                    for (int i = 0; i < row.size(); i++) {
                        if (i != 0 && i != colLeft) {
                            ret.append(row.get(i)).append("\t");
                        }
                    }
                    for (int i = 0; i < r.size(); i++) {
                        if (i != 0 && i != colRight) {
                            ret.append(r.get(i)).append("\t");
                        }
                    }
                }
            }
            ret.append("\n");
        }
    }
}
