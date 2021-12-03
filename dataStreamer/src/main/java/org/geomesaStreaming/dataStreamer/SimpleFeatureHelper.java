package org.geomesaStreaming.dataStreamer;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.geomesa.utils.geotools.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import java.util.Map;

public class SimpleFeatureHelper {
    public static SimpleFeatureType makeSftFromData(String name, DataElement newData) {
        String featureString = "";
        Map<String, String> fieldTypes = newData.getTypes();

        for (String curField : fieldTypes.keySet()) {
            String add = curField + ":" + fieldTypes.get(curField);
            if (curField.equals(newData.getDefaultIndex())) add = "*" + add;
            if (featureString.length() > 0) featureString += ",";
            featureString += add;
        }

        SimpleFeatureType newType = SimpleFeatureTypes.createType(name, featureString);
        return newType;
    }

    public static SimpleFeature makeFeatureFromData(SimpleFeatureType sft, DataElement dataElement) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(sft);

        for (AttributeDescriptor curAttr : sft.getAttributeDescriptors()) {
            featureBuilder.add(dataElement.getValue(curAttr.getLocalName()));
        }

        return featureBuilder.buildFeature(null);
    }
}
