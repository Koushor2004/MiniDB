package minidb.database;
import minidb.table.Column;
import minidb.table.Table;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Database {

    private final String name;
    private final Map<String, Table> tables = new LinkedHashMap<>();

    public Database(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Table createTable(String tableName, List<Column> columns) {
        if (tables.containsKey(tableName.toLowerCase())) {
            throw new IllegalArgumentException("Table already exists: " + tableName);
        }
        Table table = new Table(tableName, columns);
        tables.put(tableName.toLowerCase(), table);
        return table;
    }

    public Table getTable(String tableName) {
        Table table = tables.get(tableName.toLowerCase());
        if (table == null) {
            throw new IllegalArgumentException("No such table: " + tableName);
        }
        return table;
    }

    public void dropTable(String tableName) {
        if (tables.remove(tableName.toLowerCase()) == null) {
            throw new IllegalArgumentException("No such table: " + tableName);
        }
    }

    public Map<String, Table> getTables() {
        return tables;
    }
}
