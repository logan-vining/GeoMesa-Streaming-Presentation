package org.geomesaStreaming.dataStreamer;

import java.util.List;
import java.util.Map;

public interface DataElement {
    Map<String, Object> getValues();
    Object getValue(String key);
    void setValue(String key, Object val) throws NoSuchFieldException;
    List<String> getFields();
    Map<String, String> getTypes();
    String getDefaultIndex();
}
