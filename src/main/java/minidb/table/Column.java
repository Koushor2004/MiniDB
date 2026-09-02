package minidb.table;

public class Column {

    public enum Type {
        INT, LONG, DOUBLE, FLOAT, BOOLEAN, BOOL, STRING
    }

    private final String name;
    private final Type type;

    public Column(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Object parseValue(String raw) {
        switch (type) {
            case INT:
                return Integer.parseInt(raw);
            case LONG:
                return Long.parseLong(raw);
            case DOUBLE:
            case FLOAT:
                return Double.parseDouble(raw);
            case BOOLEAN:
            case BOOL:
                if ("true".equalsIgnoreCase(raw)) return true;
                if ("false".equalsIgnoreCase(raw)) return false;
                throw new IllegalArgumentException("Invalid boolean value: " + raw);
            case STRING:
                return raw;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    @Override
    public String toString() {
        return name + " " + type;
    }
}
