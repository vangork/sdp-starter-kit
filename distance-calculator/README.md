# Distance Calculator
This project includes the data injection/analytics/visualization, all components are able to be deployed by docker-compose.
 
Basically, the project uses the pre-recorded distance data from a sensor and simulates to write into a Pravega stream and then be processed by a simple logic implemented in Flink job, the computing result are continuously sink to a influxdb which is presented on Grafana dashboard later.

## Prerequisites
EMQX 3.2.7
https://www.emqx.io/downloads

Pravega 0.6.1
http://pravega.io/docs/latest/getting-started/

InfluxDB 1.7
https://docs.influxdata.com/influxdb/v1.7/introduction/installation/

Grafana 6.7.1
https://grafana.com/grafana/download

## Running Instruction
1. Keep EMQX, Pravega, Influxdb, Grafana running.

2. Navigate to the distance-calculator path and compile the whole project
```
mvn clean package
```

3. Start the pravega gateway
```
java -jar pravega-gateway\target\pravega-gateway-1.0.0.jar
```

4. Run calculator
```
java -jar calculator\target\calculator-1.0.0.jar
```

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

6. Open grafana portal, and import dashboard from visualization/dashboard.json

There you go!
