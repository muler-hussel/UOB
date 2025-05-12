package edu.uob;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataBaseIOUtils {
    public static void loadAllDBs() throws IOException {
        Map<String, DataBase> allDBs = DBServer.getAllDBs();
        File databases = new File("databases");
        if (!databases.exists()) {
            databases.mkdirs();
        }

        for (File db : Objects.requireNonNull(databases.listFiles(File::isDirectory))) {
            String dbName = db.getName();
            if (allDBs.containsKey(dbName)) {
                continue;
            }
            DataBase dataBase = new DataBase(dbName);

            for (File table : Objects.requireNonNull(db.listFiles())) {
                String fileName = table.getName();
                if (!fileName.contains(".tab")) {
                    continue;
                }
                Map<String, Table> tableInDB = dataBase.getTableInDB();
                String tableName = fileName.replace(".tab", "");
                if (tableInDB.containsKey(tableName)) {
                    continue;
                }
                Table curTable = readFile2DB(dbName, tableName);
                tableInDB.put(tableName, curTable);
                dataBase.setTableInDB(tableInDB);
            }
            allDBs.put(dbName, dataBase);
        }
        DBServer.setAllDBs(allDBs);
    }

    public static Table readFile2DB(String dbName, String tableName) throws IOException {
        Map<String, DataBase> allDBs = DBServer.getAllDBs();
        String filePath = "databases" + File.separator + dbName + File.separator + tableName + ".tab";
        File inputFile = new File(filePath);

        if (!inputFile.exists()) {
            throw new FileNotFoundException("File doesn't exist");
        }

        FileReader fileReader = new FileReader(inputFile);
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String headLine = bufferedReader.readLine();
            if (headLine == null) {
                throw new IOException("This is an empty file");
            }
            String[] headLineSplit = headLine.split("\t");
            DataBase dataBase = new DataBase(dbName);
            allDBs.put(dbName, dataBase);
            Table newTable = new Table(tableName, Arrays.stream(headLineSplit).toList());
            Map<String, Table> tableInDB = dataBase.getTableInDB();
            tableInDB.put(tableName, newTable);
            dataBase.setTableInDB(tableInDB);
            DBServer.setAllDBs(allDBs);

            String readLine;
            List<List<String>> rows = newTable.getRows();
            while ((readLine = bufferedReader.readLine()) != null) {
                if (readLine.isEmpty()) {
                    break;
                }
                String[] readLineSplit = readLine.split("\t");
                if (!readLineSplit[0].chars().allMatch(Character::isDigit)) {
                    throw new IOException("Not a valid file");
                }
                int currentId = Integer.parseInt(readLineSplit[0]);
                newTable.setCurrentId(currentId);
                List<String> newRow = Arrays.stream(readLineSplit).toList();
                rows.add(newRow);
            }
            newTable.setRows(rows);
            return newTable;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public static void writeTable2File(DataBase db, Table table) throws IOException {
        //String filePath = folderPath + File.separator + table.getTableName() + "1" + ".tab";
        String filePath = "databases" + File.separator + db.getDbName()
                + File.separator + table.getTableName() + ".tab";
        File outputFile = new File(filePath);

        FileWriter fileWriter = new FileWriter(outputFile);
        try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)){
            String headLine = String.join("\t", table.getColNames());
            bufferedWriter.write(headLine);
            bufferedWriter.newLine();

            List<List<String>> rows = table.getRows();
            for (List<String> list : rows) {
                //String row = String.join("\t", list) + "1";
                String row = String.join("\t", list);
                bufferedWriter.write(row);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public static void createDBFolder(DataBase db) {
        String folderPath = "databases" + File.separator + db.getDbName();
        File folder = new File(folderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static boolean deleteFolder(DataBase db) {
        String folderPath = "databases" + File.separator + db.getDbName();
        File folder = new File(folderPath);

        if (folder.exists()) {
            return folder.delete();
        }
        return false;
    }

    public static boolean deleteTableFile(DataBase db, Table table) {
        String filePath = "databases" + File.separator + db.getDbName()
                + File.separator + table.getTableName() + ".tab";
        File delFile = new File(filePath);

        if (delFile.exists()) {
            return delFile.delete();
        }
        return false;
    }
}
