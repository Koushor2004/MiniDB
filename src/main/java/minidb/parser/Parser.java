package minidb.parser;

import minidb.database.Database;
import minidb.table.Column;
import minidb.table.Row;
import minidb.table.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static final Pattern CREATE_TABLE = Pattern.compile(
            "CREATE TABLE (\\w+) \\((.+)\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern INSERT = Pattern.compile(
            "INSERT INTO (\\w+) VALUES \\((.+)\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SELECT = Pattern.compile(
            "SELECT \\* FROM (\\w+)(?: WHERE (\\w+)\\s*=\\s*(.+))?", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE = Pattern.compile(
            "UPDATE (\\w+) SET (\\w+)\\s*=\\s*(.*?)(?: WHERE (\\w+)\\s*=\\s*(.+))?", Pattern.CASE_INSENSITIVE);
    private static final Pattern DELETE = Pattern.compile(
            "DELETE FROM (\\w+)(?: WHERE (\\w+)\\s*=\\s*(.+))?", Pattern.CASE_INSENSITIVE);
    private static final Pattern DROP_TABLE = Pattern.compile(
            "DROP TABLE (\\w+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SHOW_TABLES = Pattern.compile(
            "SHOW TABLES", Pattern.CASE_INSENSITIVE);

    private final Database database;

    public Parser(Database database) {
        this.database = database;
    }

    public String execute(String command) {
        command = command.trim();
        if (command.endsWith(";")) {
            command = command.substring(0, command.length() - 1).trim();
        }

        Matcher m;

        m = CREATE_TABLE.matcher(command);
        if (m.matches()) {
            return createTable(m.group(1), m.group(2));
        }

        m = INSERT.matcher(command);
        if (m.matches()) {
            return insert(m.group(1), m.group(2));
        }

        m = SELECT.matcher(command);
        if (m.matches()) {
            return select(m.group(1), m.group(2), m.group(3));
        }

        m = UPDATE.matcher(command);
        if (m.matches()) {
            return update(m.group(1), m.group(2), m.group(3), m.group(4), m.group(5));
        }

        m = DELETE.matcher(command);
        if (m.matches()) {
            return delete(m.group(1), m.group(2), m.group(3));
        }

        m = DROP_TABLE.matcher(command);
        if (m.matches()) {
            database.dropTable(m.group(1));
            return "Table dropped: " + m.group(1);
        }

        m = SHOW_TABLES.matcher(command);
        if (m.matches()) {
            return String.join(", ", database.getTables().keySet());
        }

        throw new IllegalArgumentException("Unrecognized command: " + command);
    }

    private String createTable(String tableName, String columnDefs) {
        List<Column> columns = new ArrayList<>();
        for (String def : columnDefs.split(",")) {
            String[] parts = def.trim().split("\\s+");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid column definition: " + def);
            }
            Column.Type type = Column.Type.valueOf(parts[1].toUpperCase());
            columns.add(new Column(parts[0], type));
        }
        database.createTable(tableName, columns);
        return "Table created: " + tableName;
    }

    private String insert(String tableName, String valueList) {
        Table table = database.getTable(tableName);
        List<String> values = new ArrayList<>();
        for (String value : valueList.split(",")) {
            values.add(stripQuotes(value.trim()));
        }
        table.insert(values);
        return "1 row inserted into " + tableName;
    }

    private String select(String tableName, String whereColumn, String whereValue) {
        Table table = database.getTable(tableName);
        List<Row> rows = table.select(whereColumn, whereValue == null ? null : stripQuotes(whereValue.trim()));
        if (rows.isEmpty()) {
            return "No rows found";
        }
        StringBuilder sb = new StringBuilder();
        for (Row row : rows) {
            sb.append(row).append("\n");
        }
        return sb.toString().trim();
    }

    private String update(String tableName, String setColumn, String setValue, String whereColumn, String whereValue) {
        Table table = database.getTable(tableName);
        int updated = table.update(setColumn, stripQuotes(setValue.trim()),
                whereColumn, whereValue == null ? null : stripQuotes(whereValue.trim()));
        return updated + " row(s) updated in " + tableName;
    }

    private String delete(String tableName, String whereColumn, String whereValue) {
        Table table = database.getTable(tableName);
        int deleted = table.delete(whereColumn, whereValue == null ? null : stripQuotes(whereValue.trim()));
        return deleted + " row(s) deleted from " + tableName;
    }


    private String stripQuotes(String value) {
        if (value.length() >= 2 && value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
