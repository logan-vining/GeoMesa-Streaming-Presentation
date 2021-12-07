# GeoMesa-Streaming-Presentation
An Apache Kafka & GeoMesa streaming pipeline. This project reads data from a public airline data set and streams every 10 seconds into a running Apache Kafka bus that uses Zookeeper.

## Public Airline Data
The airplane data used in this project comes from the OpenSky Public REST API found here: https://opensky-network.org/api/states/all.

## Running the Pipeline
This project uses Docker to run the project in containers, so to bring up Zookeeper, the Kafka broker, and Geoserver, run:
```
docker-compose up --build
```

This will first bring up Zookeeper on port `2181`, then the broker publishing data on port `9092`, and finally Geoserver, including the web app, at [localhost:8080/geoserver](localhost:8080/geoserver).

## Working in Geoserver
After going to [localhost:8080/geoserver](localhost:8080/geoserver), login (login is at the top of the screen) with username `admin` and password `geoserver`. Once you've logged in, you should see an expanded nav menu in the left pane.

#### Streaming data and Creating the Layer
To start streaming our airplane data, first cd into the `dataStreamer` directory, then run the following command:
```
java -cp target/dataStreamer-0.1-jar-with-dependencies.jar org.geomesaStreaming.dataStreamer.planeData.PlaneDataStreamer
```

You should see POINTS start being produced, followed by a slight delay (because of the public APIs rate of deliver of ~10 seconds).

Once you are producing data, go back to Geoserver in your browser to start creating the layer:
1) Click the `Layer` option in the left pane
2) Next click `Add a new layer` button right above the list of layers
3) The layer needs to be created from a data store, so for the `Add layer from` dropdown, select `Airplane Streaming`
4) You should a layer listed now named `opensky_plane_data`, click `Publish` for that layer
5) That should bring you to the `Edit Layer` screen. The only change to make here is under the `Bounding Boxes` section, first click `Compute from data` to automatically set the Native Bounding Box for the layer, then click `Compute from native bounds` to set the coordinates for the Lat/Lon Bounding Box.
6) Click `Save` and you should now see the `opensky_plane_data` layer listed!

#### Adding the layer to a Layer Group
Once the layer is created, if you were click the `OpenLayers` option for that layer from the `Layer Preview` menu, you would just see a bunch of red dots floating in space. We need to add this data to a base that will give it some context. To do this:
1) First click the `Layer Groups` option from the left nav menu
2) Then click the `worldwide_plane_data_map` Layer Group from the list to edit
3) A couple things to note, we've set the bounding box for this Layer Group to be Montana, and set the coordinate system to `EPSG:4326` to match the coordinate systems used by both layers.
4) Under the `Layers` section, you should see just the `states_and_provinces` layer listed.
5) To add our plane data layer, click `Add Layer`, then select the `opensky_plane_data` layer
6) Make sure the drawing order has the `states_and_provinces` layer first, then the `opensky_plane_data` layer, or the dots will draw behind the map
7) Finally, `Save` this Layer Group


#### Viewing the Layers
With everything created and grouped, you can go to the `Layer Preview`, scroll to the bottom to the `worldwide_plane_data_map` layer group, and click the `OpenLayers` option to see this map in action in your browser. Currently, you will need to manually refresh your browser every 10 seconds to see the dots update, but by doing so, you will see Geoserver consuming data and displaying it's updates!

#### Adding a Layer Filter
Because the producer is constantly sending new data points, the layer just reads all of that data in and displays it on the map. However, we can add a layer filter to only display certain data points at once. This filter example will only display airplane data within the past minute:
1) Start by opening the `opensky_plane_data` layer, then scroll to the very bottom and you should see a `Restrict the features on layer by CQL filter` (CQL according to the Geoserver website stands for Common Query Language, but to make ourselves feel cooler, we decided to think of it as the Chess Query Language used to query Chess databases)
2) Enter the following filter:
```
dateDifference(now(), time, 'm') <= 1
```
3) Go preview the `worldwide_plane_data_map` Layer Group again, and now after every refresh, you should only ever see 6 points for a particular flight

##### Understanding the filter
So what does this filter do? It basically is just doing a time difference between the current date time and the time column being sent as part of the layer. Using `m` for minutes as the time unit, this filter is just checking if a data point is from within the last minute or not, and if so, display it as part of the layer.



## Notes on running docker-compose
If you would like to run these services individually, you can run the command:
```
docker-compose up [zookeeper | brokers | geoserver]
```
to run that singular piece, but keep in mind zookeeper must be running for the brokers to run, and both must be up for geoserver to run.


If you would like to run this project locally, follow the instructions laid out in the `install_docs` directory to run each isolated piece.
