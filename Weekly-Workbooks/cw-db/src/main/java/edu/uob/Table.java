package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    private String tableName;
    private List<String> colNames;
    private Integer currentId;
    private List<List<String>> rows = new ArrayList<>();

    public Table(String tableName, List<String> colNames) {
        this.tableName = tableName;
        this.colNames = colNames;
        this.currentId = 1;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getColNames() {
        return colNames;
    }

    public void setColNames(List<String> colNames) {
        this.colNames = colNames;
    }

    public Integer getCurrentId() {
        return currentId;
    }

    public void setCurrentId(Integer currentId) {
        this.currentId = currentId;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }

    public void clearTable() {
        rows.clear();
        colNames.clear();
    }
}
