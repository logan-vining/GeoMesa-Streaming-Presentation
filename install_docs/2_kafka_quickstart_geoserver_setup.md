## General Instructions for GeoMesa Kafka Setup for GeoServer
### GeoServer provides mapping/visualization functionality

##### Remember to adjust the commands as needed to work with your specific installation directories

1. Install GeoServer, "Stable" version
  * Follow instructions here: https://docs.geoserver.org/stable/en/user/installation/linux.html
  * Use this command to add the environment variable:
  ``` export GEOSERVER_HOME=<path to geoserver install dir>/geoserver-2.20.1 ```
  * Use this command to check the environment variable:
  ``` printenv GEOSERVER_HOME ```
  * Run Geoserver:
  ``` bin/startup.sh ```
  * After starting up GeoServer, wait until “Started @#####ms” message
  * Check everything worked by visiting http://localhost:8080/geoserver in a web browser
2. Install the WPS plugin for GeoServer
  * Follow instructions here: https://docs.geoserver.org/stable/en/user/services/wps/install.html
  * Restart GeoServer by hitting ctrl+C in the terminal and re-running startup.sh
  * In the browser interface, check that WPS installed properly
3. Get the GeoMesa installation file for Kafka: geomesa-kafka_2.12-3.3.0-bin.tar.gz
  * https://github.com/locationtech/geomesa/releases/tag/geomesa-3.3.0
4. Un-tar the GeoMesa for Kafka file
  ``` tar -xzvf geomesa-kafka_2.12-3.3.0-bin.tar.gz ```
5. From the directory where you un-tarred in step 4, extract the contents of the Kafka plugin .tar to the GeoServer library directory (the next steps are covered here, section 18.1.4.2: http://www.geomesa.org/documentation/stable/user/kafka/install.html#install-kafka-geoserver)
  ```
  tar -xzvf \
    geomesa-kafka_2.12-3.3.0/dist/gs-plugins/geomesa-kafka-gs-plugin_2.12-3.3.0-install.tar.gz \
    -C <path to geoserver install dir>/geoserver-2.20.1/webapps/geoserver/WEB-INF/lib
  ```
6. Run the install-dependencies.sh script
  ``` geomesa-kafka_2.12-3.3.0/bin/install-dependencies.sh <path to geoserver install dir>/geoserver-2.20.1/webapps/geoserver/WEB-INF/lib ```
7. Restart GeoServer
8. Using the GeoServer browser interface, register the GeoMesa Kafka DataStore with GeoServer
  * Follow instructions here: https://www.geomesa.org/documentation/stable/tutorials/geomesa-quickstart-kafka.html
  * Use the default credentials of "admin" and "geoserver" to log in
9. Publish the "tdrive-quickstart" layer in GeoServer
  * Follow instructions here: https://www.geomesa.org/documentation/stable/tutorials/geomesa-quickstart-kafka.html
10. Click on the "Layer Preview" link on the left-hand side. You should see the "tdrive-quickstart" layer in the list. Click on the "OpenLayers" link in the "Common Formats" column to view the layer. Refresh the layer page as the tutorial runs to see the red dot moving around.