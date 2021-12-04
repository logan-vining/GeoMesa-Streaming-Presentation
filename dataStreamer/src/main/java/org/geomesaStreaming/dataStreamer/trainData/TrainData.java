package org.geomesaStreaming.dataStreamer.trainData;

import org.geomesaStreaming.dataStreamer.DataElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainData implements DataElement {
    private Map<String, Object> values;
    private final Map<String, String> types;
    private final String defaultIndexField = "loc";

    public TrainData() {
        values = new HashMap<>();
        types = new HashMap<>();

        values.put("train_id", null);
        types.put("train_id", "String");
        values.put("loc", null);
        types.put("loc", "Point:srid=4326");
        values.put("time", null);
        types.put("time", "Date");
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
