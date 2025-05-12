package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class DataBase {
    private String dbName;
    private Map<String, Table> tableInDB;

    public DataBase(String dbName) {
        this.dbName = dbName;
        this.tableInDB = new HashMap<>();
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Map<String, Table> getTableInDB() {
        return tableInDB;
    }

    public void setTableInDB(Map<String, Table> tableInDB) {
        this.tableInDB = tableInDB;
    }
}
