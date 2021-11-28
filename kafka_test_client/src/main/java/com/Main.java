package com;

import org.geotools.data.*;
import org.locationtech.geomesa.utils.geotools.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {
        DataStore myStore = makeDataStore("localhost:2181", "localhost:9092");

        SimpleFeatureType myType = makeSft();
        System.out.println("Type name: " + myType.getTypeName());
        myStore.createSchema(myType);
        writeFeatures(myStore, myType);
    }

    private static DataStore makeDataStore(String zookeeperAddr, String kafkaAddr) throws IOException {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("kafka.zookeepers", zookeeperAddr);
        parameters.put("kafka.brokers", kafkaAddr);

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

    private static void writeFeatures(DataStore dataStore, SimpleFeatureType type) {
        int numFeatures = 1000;
        Random rand = new Random();

        try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                dataStore.getFeatureWriterAppend(type.getTypeName(), Transaction.AUTO_COMMIT)) {

            for (int i = 0; i < numFeatures; i++) {
                SimpleFeature nextFeature = writer.next();
                nextFeature.setAttribute("name", "iter" + i);
                nextFeature.setAttribute("time", new Timestamp(System.currentTimeMillis()));

                Double newX = 80.0 + rand.nextDouble(),
                        newY = 39 + rand.nextDouble();
                String pointStr = "POINT (" + newX.toString() + " " + newY.toString() + ")";
                nextFeature.setAttribute("newLoc", pointStr);
                writer.write();

                if (i % 50 == 0) {
                    System.out.println("Wrote " + DataUtilities.encodeFeature(nextFeature));
                }

                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
