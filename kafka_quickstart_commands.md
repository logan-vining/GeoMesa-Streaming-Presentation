## Running Kafka Quick Start with All Components Installed

### check maven version:
``` mvn -v ```

### start zookeeper for kafka
##### run from kafka root dir
``` bin/zookeeper-server-start.sh config/zookeeper.properties ```

### start kafka instance
##### run from kafka root dir
``` bin/kafka-server-start.sh config/server.properties ```

### check kafka version
##### run from kafka root dir
``` bin/kafka-topics.sh --version ```

### run maven clean and install
##### run from geomesa-tutorials repo root dir
``` mvn clean install -pl geomesa-tutorials-kafka/geomesa-tutorials-kafka-quickstart -am ```

### run the quickstart tutorial
##### (does not use GeoServer)
##### run from geomesa-tutorials repo root dir
```
java -cp geomesa-tutorials-kafka/geomesa-tutorials-kafka-quickstart/target/geomesa-tutorials-kafka-quickstart-3.3.0.jar \
    org.geomesa.example.kafka.KafkaQuickStart \
    --kafka.brokers localhost:9092 \
    --kafka.zookeepers localhost:2181
```

##### optionally add the clean up flag to the above command
``` --cleanup ```
