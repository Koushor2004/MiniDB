package minidb.table;

import java.util.LinkedHashMap;
import java.util.Map;

public class Row {

    private final Map<String, Object> values = new LinkedHashMap<>();

    public void set(String column, Object value) {
        values.put(column, value);
    }

    public Object get(String column) {
        return values.get(column);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        return sb.append("}").toString();
    }
}
