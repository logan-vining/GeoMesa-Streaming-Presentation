package org.geomesaStreaming.dataStreamer.planeData;

import org.geomesaStreaming.dataStreamer.BasicHttp;
import org.geomesaStreaming.dataStreamer.DataElement;
import org.geomesaStreaming.dataStreamer.DataFetcher;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PlaneDataFetcher implements DataFetcher {
    private final String dataUrl = "https://opensky-network.org/api/states/all";
    public List<DataElement> getDataElements() {
        List<DataElement> newData;
		
		newData = parseToPlaneData(BasicHttp.getResponseAsString(dataUrl));

        return newData;
    }

    private List<DataElement> parseToPlaneData(String jsonResponse) {
        List<DataElement> planeData = new ArrayList<>();

        JsonParser parser = new JsonParser();
		JsonObject responseObj = parser.parse(jsonResponse).getAsJsonObject();
		JsonArray allStateVectors = responseObj.getAsJsonArray("states");
		
		for (int i = 0; i <= allStateVectors.size() - 1; i++) {
			JsonArray stateVector = allStateVectors.get(i).getAsJsonArray();
			
			PlaneData newDataElement = new PlaneData();

			if (!stateVector.get(0).isJsonNull() && !stateVector.get(5).isJsonNull() && !stateVector.get(6).isJsonNull()) {
				try {
					newDataElement.setValue("callsign", stateVector.get(0).getAsString());

					String pointStr = "POINT (" + stateVector.get(5).getAsString() + " " + stateVector.get(6).getAsString() + ")";
					newDataElement.setValue("loc", pointStr);

					if (!stateVector.get(9).isJsonNull()) newDataElement.setValue("velocity", stateVector.get(9).getAsDouble());
					if (!stateVector.get(4).isJsonNull()) newDataElement.setValue("time", new Date(stateVector.get(4).getAsLong() * 1000));
					//System.out.println(newDataElement.getValue("time"));
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
					newDataElement = null;
				}

				planeData.add(newDataElement);
			}
		}
		
		return planeData;
	}
}
