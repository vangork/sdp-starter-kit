# Distance Calculator
This project includes the data injection/analytics/visualization, all components are able to be deployed by docker-compose.
 
Basically, the project uses the pre-recorded distance data from a sensor and simulates to write into a Pravega stream and then be processed by a simple logic implemented in Flink job, the computing result are continuously sink to a influxdb which is presented on Grafana dashboard later.

## Prerequisites
Kubernetes Cluster running with SDP instance

## Running Instruction
1. Refer to `Distance Calculator Demo` inside https://streamingdataplatform.github.io/code-hub/demos/ for detailed instructions on setup and configuration

2. Navigate to the distance-calculator path and compile the whole project
```
mvn clean package
```

3. Setup a flink cluster and run the `calculator\target\calculator-1.0.0.jar` as a flink application .

4. Open grafana portal, and import dashboard from `visualization/dashboard.json` by specifying appropriate datasource.

5. Simulate the mqtt writer by injecting pre-recorded data

    Set the environment variables to match broker configuration
    ```
    set MQTT_BROKER_URL=tls://1.1.1.1:8883
    set MQTT_ALLOW_INSECURE=true
    set MQTT_DATA_FILE=C:\\mqtt-writer\\Distance.csv
    set MQTT_USE_AUTH=true
    set MQTT_USERNAME=default
    set MQTT_PASSWORD=default   
    ```
    Then execute the jar file with the command
```
java -jar mqtt-writer\target\mqtt-writer-1.0.0.jar
```


There you go!
