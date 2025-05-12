package edu.uob;

import java.util.List;

public class CommandVerifyUtils {
    public static boolean isPlainText(String dbName) {
        for (char c : dbName.toCharArray()) {
            if (!Character.isDigit(c) && !Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isReservedWord(String plainText) {
        String[] reservedWord = {"USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "INSERT", "INTO", "VALUES",
                "SELECT", "FROM", "WHERE", "UPDATE", "SET", "DELETE", "JOIN", "AND", "ON", "ADD", "TRUE", "FALSE",
                "NULL", "OR", "LIKE"};
        for (String word : reservedWord) {
            if (plainText.toUpperCase().equals(word)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isComparator(String comparator) {
        String[] allComparators = DBServer.getAllComparators();
        for (String s : allComparators) {
            if (s.equalsIgnoreCase(comparator)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBoolOperator(String boolOperator) {
        return boolOperator.equalsIgnoreCase("AND") || boolOperator.equalsIgnoreCase("OR");
    }

    public static boolean isAttrName(String attrName, Table table) {
        List<String> colNames = table.getColNames();
        for (String colName : colNames) {
            if (colName.equalsIgnoreCase(attrName)) {
                return true;
            }
        }
        return false;
    }
}
