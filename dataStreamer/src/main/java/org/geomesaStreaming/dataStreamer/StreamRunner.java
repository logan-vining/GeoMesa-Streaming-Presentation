package org.geomesaStreaming.dataStreamer;

import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class StreamRunner implements Runnable {
	public volatile boolean keepRunning = true;
	
	private DataStore dataStore;
	private SimpleFeatureType sft;
	private DataFetcher dataFetcher;
	
	public StreamRunner(DataStore newStore, SimpleFeatureType newSft, DataFetcher newFetcher) {
		dataStore = newStore;
		sft = newSft;
		dataFetcher = newFetcher;
	}

    public void run() {
        try {
            SimpleFeatureStore prodFeatureStore = (SimpleFeatureStore) dataStore.getFeatureSource(sft.getTypeName());

            while (keepRunning) {
                List<DataElement> newData = dataFetcher.getDataElements();

                for (DataElement curElem : newData) {
                    SimpleFeature newFeature = SimpleFeatureHelper.makeFeatureFromData(sft, curElem);
                    prodFeatureStore.addFeatures(new ListFeatureCollection(sft, Collections.singletonList(newFeature)));

                    System.out.println("Wrote " + DataUtilities.encodeFeature(newFeature));
                }

                System.out.println("Type 'stop' to stop and quit");
                System.out.println();

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    keepRunning = false;
                }
            }

            System.out.println("Clearing features...");
            prodFeatureStore.removeFeatures(Filter.INCLUDE);
            System.out.println("Stream terminated");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}