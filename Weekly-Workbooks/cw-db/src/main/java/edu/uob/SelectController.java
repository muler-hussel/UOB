package edu.uob;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectController implements CommandHandleController{
    @Override
    public String handle(String command) throws IOException {
        String currentDB = DBServer.getCurrentDB();
        Map<String, DataBase> allDBs = DBServer.getAllDBs();

        String ret = "";
        String[] commandSplit = command.replace(";", "")
                .replace("'", "")
                .replace("\"", "")
                .split(" (?i)FROM ");
        if (commandSplit.length < 2) {
            return "[ERROR], command is invalid";
        }

        String[] selectedCols = commandSplit[0].split("\\s+");
        if (selectedCols.length < 2) {
            return "[ERROR], command is invalid";
        }

        String[] tableAndCondition = commandSplit[1].split(" (?i)WHERE ");
        String tableName = tableAndCondition[0];
        if (currentDB == null || !allDBs.get(currentDB).getTableInDB().containsKey(tableName)) {
            return "[ERROR], table name is invalid";
        }
        Table curTable = allDBs.get(currentDB).getTableInDB().get(tableName);

        List<String> attrNameAndValue = new ArrayList<>();
        Deque<String> operator = new LinkedList<>();
        String condition;
        if (tableAndCondition.length == 2) {
            condition = tableAndCondition[1];
            if (!ConditionHandleUtils.isConditionValid(condition, curTable, attrNameAndValue, operator)) {
                return "[ERROR], command is invalid";
            } else {
                ret = selectRows(curTable, selectedCols, attrNameAndValue);
            }
        } else if (tableAndCondition.length > 2) {
            return "[ERROR], command is invalid";
        } else {
            ret = selectRows(curTable, selectedCols, attrNameAndValue);
        }
        return ret;
    }

    private String selectRows(Table table, String[] selectedCols, List<String> attrNameAndValue) {
        boolean isStarExist = false;
        for (String s : selectedCols) {
            if (s.equals("*")) {
                isStarExist = true;
            } else if (!s.equalsIgnoreCase("SELECT") && !CommandVerifyUtils.isAttrName(s, table)) {
                return "[ERROR], command is invalid";
            }
        }

        int[] selectedColsIdx;
        List<String> colNames = table.getColNames();
        if (isStarExist) {
            selectedColsIdx = new int[colNames.size()];
            for (int i = 0; i < colNames.size(); i++) {
                selectedColsIdx[i] = i;
            }
        } else {
            selectedColsIdx = new int[selectedCols.length - 1];
            for (int i = 1; i < selectedCols.length; i++) {
                selectedColsIdx[i - 1] = colNames.indexOf(selectedCols[i]);
            }
        }

        StringBuilder ret = new StringBuilder();
        List<List<String>> rows = table.getRows();
        for (int col : selectedColsIdx) {
            ret.append(colNames.get(col)).append("\t");
        }
        ret.append("\n");
        if (attrNameAndValue.isEmpty()) {
            for (List<String> row : rows) {
                for (int col : selectedColsIdx) {
                    ret.append(row.get(col)).append("\t");
                }
                ret.append("\n");
            }
        } else {
            List<Integer> selectRows = new ArrayList<>();
            if (handleCondition (table, attrNameAndValue, selectRows)) {
                for (int row : selectRows) {
                    for (int col : selectedColsIdx) {
                        ret.append(rows.get(row).get(col)).append("\t");
                    }
                    ret.append("\n");
                }
                return "[OK]" + ret;
            } else {
                return "[ERROR], command is invalid";
            }
        }

        return "[OK]" + ret;
    }

    private boolean handleCondition (Table table, List<String> attrNameAndValue, List<Integer> selectRows) {
        List<String> colNames = table.getColNames();
        List<Integer> tmpSelectRows = new ArrayList<>();
        int curCol = -1;
        for (int i = 0; i < attrNameAndValue.size(); i++) {
            String con = attrNameAndValue.get(i);
            if (CommandVerifyUtils.isAttrName(con, table) && curCol == -1) {
                curCol = colNames.indexOf(con);
            } else if (CommandVerifyUtils.isComparator(con) && curCol != -1 && i < attrNameAndValue.size() - 1) {
                String value = attrNameAndValue.get(++i);
                ConditionHandleUtils.compareCondition(table, con, value, curCol, tmpSelectRows);
                if (selectRows.isEmpty()) {
                    selectRows.addAll(tmpSelectRows);
                    tmpSelectRows.clear();
                }
                curCol = -1;
            } else if (CommandVerifyUtils.isBoolOperator(con)) {
                if (con.equals("AND")) {
                    selectRows.retainAll(tmpSelectRows);
                } else {
                    selectRows = Stream.concat(selectRows.stream(), tmpSelectRows.stream())
                            .distinct()
                            .sorted()
                            .collect(Collectors.toList());
                }
                tmpSelectRows = new ArrayList<>();
            } else {
                return false;
            }
        }
        return true;
    }
}
