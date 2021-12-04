package org.geomesaStreaming.dataStreamer.trainData;

import org.geomesaStreaming.dataStreamer.SimpleFeatureHelper;
import org.geomesaStreaming.dataStreamer.StreamRunner;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

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

		StreamRunner streamRunner = new StreamRunner(prodStore, trainSft, new TrainDataFetcher());
		Thread streamThread = new Thread(streamRunner);
		streamThread.start();
		
		System.out.println("Streaming Irish Rail train data; type 'stop' to stop and quit");
		System.out.println();
		
		Scanner input = new Scanner(System.in);
		while (!input.next().toLowerCase().equals("stop"));
		
		streamRunner.keepRunning = false;
		streamThread.interrupt();
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
}
