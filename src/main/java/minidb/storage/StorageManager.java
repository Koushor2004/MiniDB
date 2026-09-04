package minidb.storage;

import minidb.database.Database;
import minidb.table.Column;
import minidb.table.Row;
import minidb.table.Table;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageManager {

    private static final String SCHEMA_PREFIX = "SCHEMA:";

    public static void loadDatabase(Database database, String dataDirPath) {
        File dataDir = new File(dataDirPath);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            return;
        }

        File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".db"));
        if (files == null) return;

        for (File file : files) {
            try {
                loadTable(database, file);
            } catch (Exception e) {
                System.out.println("Warning: Failed to load table file " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private static void loadTable(Database database, File file) throws IOException {
        String fileName = file.getName();
        String tableName = fileName.substring(0, fileName.length() - 3);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null || !headerLine.startsWith(SCHEMA_PREFIX)) {
                return;
            }

            String schemaDef = headerLine.substring(SCHEMA_PREFIX.length()).trim();
            List<Column> columns = new ArrayList<>();
            if (!schemaDef.isEmpty()) {
                for (String colDef : schemaDef.split(",")) {
                    String[] parts = colDef.trim().split("\\s+");
                    if (parts.length == 2) {
                        Column.Type type = Column.Type.valueOf(parts[1].toUpperCase());
                        columns.add(new Column(parts[0], type));
                    }
                }
            }

            Table table = database.createTable(tableName, columns);

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> rawTokens = parseCsvLine(line);
                if (rawTokens.size() == columns.size()) {
                    table.insert(rawTokens);
                }
            }
        }
    }

    public static void saveTable(Table table, String dataDirPath) {
        File dataDir = new File(dataDirPath);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File file = new File(dataDir, table.getName().toLowerCase() + ".db");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            StringBuilder sb = new StringBuilder(SCHEMA_PREFIX);
            List<Column> columns = table.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) sb.append(",");
                Column col = columns.get(i);
                sb.append(col.getName()).append(" ").append(col.getType());
            }
            writer.write(sb.toString());
            writer.newLine();

            for (Row row : table.getRows()) {
                StringBuilder rowSb = new StringBuilder();
                for (int i = 0; i < columns.size(); i++) {
                    if (i > 0) rowSb.append(",");
                    Column col = columns.get(i);
                    Object val = row.get(col.getName());
                    rowSb.append(toCsvValue(val));
                }
                writer.write(rowSb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Warning: Failed to save table " + table.getName() + ": " + e.getMessage());
        }
    }

    public static void deleteTableFile(String tableName, String dataDirPath) {
        File dataDir = new File(dataDirPath);
        File file = new File(dataDir, tableName.toLowerCase() + ".db");
        if (file.exists()) {
            file.delete();
        }
    }

    public static List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens;
    }

    public static String toCsvValue(Object val) {
        if (val == null) return "";
        String str = val.toString();
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }
}
