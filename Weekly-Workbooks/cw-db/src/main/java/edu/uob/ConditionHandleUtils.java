package edu.uob;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConditionHandleUtils {
    public static boolean isConditionValid(String condition, Table table, List<String> attrNameAndValue, Deque<String> operator) {
        String[] conditions = condition.replace("(", "( ")
                .replace(")", " )")
                .split("\\s+");

        for (String s : conditions) {
            if (s.equals("(")) {
                operator.add(s);
            } else if (s.equals(")")) {
                while (!operator.isEmpty() && !operator.peek().equals("(")) {
                    attrNameAndValue.add(operator.pop());
                }
                operator.pop();
            } else if (CommandVerifyUtils.isComparator(s)) {
                if (!CommandVerifyUtils.isAttrName(attrNameAndValue.get(attrNameAndValue.size() - 1), table)) {
                    return false;
                }
                attrNameAndValue.add(s);
            } else if (CommandVerifyUtils.isBoolOperator(s)) {
                operator.push(s);
            } else {
                attrNameAndValue.add(s);
            }
        }
        return true;
    }

    public static boolean handleCondition(Table table, List<String> attrNameAndValue, List<Integer> selectRows) {
        List<String> colNames = table.getColNames();
        List<Integer> tmpSelectRows = new ArrayList<>();
        int curCol = -1;
        for (int i = 0; i < attrNameAndValue.size(); i++) {
            String con = attrNameAndValue.get(i);
            if (CommandVerifyUtils.isAttrName(con, table) && curCol == -1) {
                curCol = colNames.indexOf(con);
            } else if (CommandVerifyUtils.isComparator(con) && curCol != -1 && i < attrNameAndValue.size() - 1) {
                String value = attrNameAndValue.get(++i);
                compareCondition(table, con, value, curCol, tmpSelectRows);
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

    public static void compareCondition(Table table, String comparator, String value, int curCol, List<Integer> tmpSelectRows) {
        List<List<String>> rows = table.getRows();
        List<String> curColValue = new ArrayList<>();
        for (List<String> row : rows) {
            curColValue.add(row.get(curCol));
        }

        for (int i = 0; i < curColValue.size(); i++) {
            String curValue = curColValue.get(i);
            switch (comparator) {
                case "==":
                    if (curValue.equals(value)) {
                        tmpSelectRows.add(i);
                    }
                    break;
                case ">=":
                    if (curValue.compareTo(value) >= 0) {
                        tmpSelectRows.add((i));
                    }
                    break;
                case "<=":
                    if (curValue.compareTo(value) <= 0) {
                        tmpSelectRows.add((i));
                    }
                    break;
                case ">":
                    if (curValue.compareTo(value) > 0) {
                        tmpSelectRows.add((i));
                    }
                    break;
                case "<":
                    if (curValue.compareTo(value) < 0) {
                        tmpSelectRows.add((i));
                    }
                    break;
                case "!=":
                    if (!curValue.equals(value)) {
                        tmpSelectRows.add(i);
                    }
                    break;
                case "LIKE":
                    if (curValue.contains(value)) {
                        tmpSelectRows.add(i);
                    }
                    break;
            }
        }
    }
}
