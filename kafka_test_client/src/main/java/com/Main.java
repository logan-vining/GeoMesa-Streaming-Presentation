package com;

import org.geotools.data.*;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.util.factory.Hints;
import org.locationtech.geomesa.utils.geotools.SimpleFeatureTypes;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        DataStore producer = makeDataStore("localhost:2181", "localhost:9092", "0");
        DataStore consumer = makeDataStore("localhost:2181", "localhost:9092", "1");

        SimpleFeatureType myType = makeSft();
        System.out.println("Created type: " + myType.getTypeName());
        producer.createSchema(myType);
        System.out.println("Created schema: " + DataUtilities.encodeType(myType));
        System.out.println("Press <enter> to continue");
        System.in.read();

        writeFeatures(producer, consumer, myType);
        System.out.println("Done");
    }

    private static DataStore makeDataStore(String zookeeperAddr, String kafkaAddr, String consumerCount) throws IOException {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("kafka.zookeepers", zookeeperAddr);
        parameters.put("kafka.brokers", kafkaAddr);
        parameters.put("kafka.consumer.count", consumerCount);

        DataStore newStore = DataStoreFinder.getDataStore(parameters);
        if (newStore == null) {
            throw new RuntimeException("Could not create data store with provided parameters");
        }
        return newStore;
    }

    private static SimpleFeatureType makeSft() {
        SimpleFeatureType newType = SimpleFeatureTypes.createType(
                "test", "name:String,time:Timestamp,*newLoc:Point:srid=4326"
        );
        return newType;
    }

    private static void writeFeatures(DataStore producer, DataStore consumer, SimpleFeatureType type) throws IOException {
        int numFeatures = 2000;
        Random rand = new Random();

        SimpleFeatureStore prodFeatureStore = (SimpleFeatureStore) producer.getFeatureSource(type.getTypeName());
        SimpleFeatureSource consumFeatureSource = consumer.getFeatureSource(type.getTypeName());

        for (int i = 0; i < numFeatures; i++) {
            List<Object> values = new ArrayList<>();

            values.add("iter" + i); //name
            values.add(new Timestamp(System.currentTimeMillis())); //time

            Double newX = 80.0 + rand.nextDouble(),
                    newY = 39 + rand.nextDouble();
            String pointStr = "POINT (" + newX.toString() + " " + newY.toString() + ")";
            values.add(pointStr);

            SimpleFeature nextFeature = buildFeature(values, type);
            prodFeatureStore.addFeatures(new ListFeatureCollection(type, Collections.singletonList(nextFeature)));

            if (i % 50 == 0) {
                System.out.println("Wrote " + DataUtilities.encodeFeature(nextFeature));

                SimpleFeatureIterator consumIter = consumFeatureSource.getFeatures().features();
                System.out.println("Current consumer state: ");
                while (consumIter.hasNext()) {
                    System.out.println(DataUtilities.encodeFeature(consumIter.next()));
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        System.out.println("Wrote all features");
        System.out.println("Press <enter> to clear DataStore");
        System.in.read();
        prodFeatureStore.removeFeatures(Filter.INCLUDE);
        System.out.println("Features Removed");
    }

    private static SimpleFeature buildFeature(List<Object> values, SimpleFeatureType type) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);
        for (Object newVal : values) featureBuilder.add(newVal);
        //this is necessary to enforce using the provided FID to update a feature
        featureBuilder.featureUserData(Hints.USE_PROVIDED_FID, Boolean.TRUE);

        SimpleFeature newFeature = featureBuilder.buildFeature("id-01");
        return newFeature;
    }
}
