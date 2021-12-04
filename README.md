# GeoMesa-Streaming-Presentation
An Apache Kafka & GeoMesa streaming pipeline. This project reads data from a public airline data set and streams every 10 seconds into a running Apache Kafka bus that uses Zookeeper.


## Running the Pipeline
This project uses Docker to run the project in containers, so to bring up Zookeeper, the Kafka broker, and Geoserver, run:
```
docker-compose up --build
```

This will first bring up Zookeeper on port `2181`, then the broker publishing data on port `9092`, and finally Geoserver, including the web app, at `localhost:8080/geoserver`.

If you would like to run these services individually, you can run the command:
```
docker-compose up [zookeeper | brokers | geoserver]
```
to run that singular piece, but keep in mind zookeeper must be running for the brokers to run, and both must be up for geoserver to run.


If you would like to run this project locally, follow the instructions laid out in the `install_docs` directory to run each isolated piece.
