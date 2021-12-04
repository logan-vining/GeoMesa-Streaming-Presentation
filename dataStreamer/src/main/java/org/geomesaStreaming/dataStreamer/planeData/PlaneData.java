package org.geomesaStreaming.dataStreamer.planeData;

import org.geomesaStreaming.dataStreamer.DataElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaneData implements DataElement {
    private Map<String, Object> values;
    private final Map<String, String> types;
    private final String defaultIndexField = "loc";

    public PlaneData() {
        values = new HashMap<>();
        types = new HashMap<>();

        values.put("callsign", null);
        types.put("callsign", "String"); // 0
        values.put("loc", null);
        types.put("loc", "Point:srid=4326"); // 5 & 6
        values.put("time", null);
        types.put("time", "Date"); // 4
		values.put("velocity", null);
		types.put("velocity", "Double"); // 9
    }

    public Map<String, Object> getValues() { return values; }
    public Object getValue(String key) { return values.get(key); }

    public void setValue(String key, Object val) throws NoSuchFieldException {
        if (!values.containsKey(key)) { throw new NoSuchFieldException(); }
        else { values.put(key, val); }
    }

    public List<String> getFields() { return new ArrayList<>(values.keySet()); }
    public Map<String, String> getTypes() { return types; }
    public String getDefaultIndex() { return defaultIndexField; }
}
