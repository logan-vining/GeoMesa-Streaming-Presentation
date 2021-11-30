## Trying out the test client

### Start up Zookeeper, Kafka, and GeoServer

### CD to the GeoMesa-Streaming-Presentation/kafka_test_client dir

### Run the bundled-up JAR
``` java -cp kafka_test_client-1.0-SNAPSHOT-jar-with-dependencies.jar: com.Main ```
##### It will pause, just like the quick start
##### Go to GeoServer in your browser and set up and publish the layer the same way you would for the quick start
  * Go to "Layers", then "Add a new layer"
  * For the DataStore, use the same one you set up for the quick start
    * Since test client runs with Kafka/Zookeeper instances on the same port, to GeoServer it is coming from the same datastore
  * You should see a new option for a layer called "test" - publish this layer
    * This corresponds to the name of the SimpleFeatureType in the test client, and therefore also to the name of the Kafka topic
##### Use these values for the boundaries:
  * Min X: 80
  * Max X: 81
  * Min Y: 39
  * Max Y: 40
  * (use same values for lat/long as for x/y)
##### Use something like "geomesa_kafka_test" for the layer name

### Preview the layer the same way you did for the quick start

### Go back to where you're running the test client JAR and press [enter] to continue

### The test client will periodically output status information. Refresh the layer preview to see the dot move

### When the test client finishes running, it will wait for you to press [enter] again. Once you do this, it will clear the data out of the DataStore.

### For some reason unknown to me, it might hang up after pressing [enter] the last time. If needed, use ctrl + c to actually exit.
