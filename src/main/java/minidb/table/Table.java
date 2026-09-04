package minidb.table;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private final String name;
    private final List<Column> columns;
    private final List<Row> rows = new ArrayList<>();

    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Row> getRows() {
        return rows;
    }

    public Column getColumn(String columnName) {
        for (Column column : columns) {
            if (column.getName().equalsIgnoreCase(columnName)) {
                return column;
            }
        }
        return null;
    }

    public void insert(List<String> rawValues) {
        if (rawValues.size() != columns.size()) {
            throw new IllegalArgumentException(
                    "Expected " + columns.size() + " values but got " + rawValues.size());
        }
        Row row = new Row();
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            row.set(column.getName(), column.parseValue(rawValues.get(i)));
        }
        rows.add(row);
    }

    public List<Row> select(String whereColumn, String operator, String whereValue) {
        if (whereColumn == null) {
            return new ArrayList<>(rows);
        }
        Column column = getColumn(whereColumn);
        if (column == null) {
            throw new IllegalArgumentException("No such column: " + whereColumn);
        }
        Object target = column.parseValue(whereValue);
        List<Row> result = new ArrayList<>();
        for (Row row : rows) {
            if (evaluateCondition(row.get(column.getName()), operator, target)) {
                result.add(row);
            }
        }
        return result;
    }

    public int delete(String whereColumn, String operator, String whereValue) {
        if (whereColumn == null) {
            int count = rows.size();
            rows.clear();
            return count;
        }
        Column column = getColumn(whereColumn);
        if (column == null) {
            throw new IllegalArgumentException("No such column: " + whereColumn);
        }
        Object target = column.parseValue(whereValue);
        int before = rows.size();
        rows.removeIf(row -> evaluateCondition(row.get(column.getName()), operator, target));
        return before - rows.size();
    }

    public int update(String setColumn, String rawSetValue, String whereColumn, String operator, String rawWhereValue) {
        Column setCol = getColumn(setColumn);
        if (setCol == null) {
            throw new IllegalArgumentException("No such column: " + setColumn);
        }
        Object newTargetValue = setCol.parseValue(rawSetValue);

        if (whereColumn == null) {
            for (Row row : rows) {
                row.set(setCol.getName(), newTargetValue);
            }
            return rows.size();
        }

        Column whereCol = getColumn(whereColumn);
        if (whereCol == null) {
            throw new IllegalArgumentException("No such column: " + whereColumn);
        }
        Object whereTargetValue = whereCol.parseValue(rawWhereValue);

        int updatedCount = 0;
        for (Row row : rows) {
            if (evaluateCondition(row.get(whereCol.getName()), operator, whereTargetValue)) {
                row.set(setCol.getName(), newTargetValue);
                updatedCount++;
            }
        }
        return updatedCount;
    }

    @SuppressWarnings("unchecked")
    private boolean evaluateCondition(Object rowValue, String operator, Object targetValue) {
        if (operator == null) {
            operator = "=";
        }
        if (rowValue == null || targetValue == null) {
            if ("=".equals(operator) || "==".equals(operator)) {
                return rowValue == targetValue;
            }
            if ("!=".equals(operator) || "<>".equals(operator)) {
                return rowValue != targetValue;
            }
            return false;
        }

        if (rowValue instanceof Comparable && targetValue instanceof Comparable) {
            int cmp = ((Comparable<Object>) rowValue).compareTo(targetValue);
            switch (operator) {
                case "=":
                case "==":
                    return cmp == 0;
                case "!=":
                case "<>":
                    return cmp != 0;
                case ">":
                    return cmp > 0;
                case "<":
                    return cmp < 0;
                case ">=":
                    return cmp >= 0;
                case "<=":
                    return cmp <= 0;
                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        }

        switch (operator) {
            case "=":
            case "==":
                return rowValue.equals(targetValue);
            case "!=":
            case "<>":
                return !rowValue.equals(targetValue);
            default:
                throw new IllegalArgumentException("Operator '" + operator + "' requires comparable values");
        }
    }
}

