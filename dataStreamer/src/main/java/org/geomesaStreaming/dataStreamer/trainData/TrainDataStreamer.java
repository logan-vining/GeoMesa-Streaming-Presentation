package org.geomesaStreaming.dataStreamer.trainData;

import org.geomesaStreaming.dataStreamer.DataElement;
import org.geomesaStreaming.dataStreamer.DataFetcher;
import org.geomesaStreaming.dataStreamer.SimpleFeatureHelper;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainDataStreamer {
    public static void main(String[] args) {
        try {
            runTrainDemo();
        } catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        }
    }

    private static void runTrainDemo() throws IOException {
        String zookeeperAddr = "localhost:2181", kafkaAddr = "localhost:9092";
        DataStore prodStore = makeDataStore(zookeeperAddr, kafkaAddr, "0"); //Kafka producer
        System.out.println("Loaded datastore: " + prodStore.toString());
        System.out.println();

        SimpleFeatureType trainSft = SimpleFeatureHelper.makeSftFromData("irish_rail_data", new TrainData());
        prodStore.createSchema(trainSft);
        System.out.println("Created schema: " + DataUtilities.encodeType(trainSft));
        System.out.println();

        System.out.println("Starting stream...");
        System.out.println();

        SimpleFeatureStore featureStore = streamTrainData(prodStore, trainSft);

        System.out.println("Clearing features...");
        featureStore.removeFeatures(Filter.INCLUDE);
        System.out.println("Goodbye");
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

    private static SimpleFeatureStore streamTrainData(DataStore dataStore, SimpleFeatureType sft) throws IOException {
        DataFetcher trainDataFetcher = new TrainDataFetcher();
        SimpleFeatureStore prodFeatureStore = (SimpleFeatureStore) dataStore.getFeatureSource(sft.getTypeName());

        while (true) {
            List<DataElement> newData = trainDataFetcher.getDataElements();

            for (DataElement curElem : newData) {
                SimpleFeature newFeature = SimpleFeatureHelper.makeFeatureFromData(sft, curElem);
                prodFeatureStore.addFeatures(new ListFeatureCollection(sft, Collections.singletonList(newFeature)));

                System.out.println("Wrote " + DataUtilities.encodeFeature(newFeature));
            }

            System.out.println();
            System.out.println("Streaming Irish Rail train data; use ctrl+c to exit");
            System.out.println();

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return prodFeatureStore;
            }
        }
    }
}
