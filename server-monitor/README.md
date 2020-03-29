# Server Monitor
To demo how to build a pc monitor with Streaming Data Platform

## Architecture

IOT Client -> MQTT Server <- PravegaGateway -> PravegaServer <- Flink -> Influxdb <- Grafana

## Prerequisites
* MQTT Brocker 3.2.7
https://docs.emqx.io/broker/v3/en/install.html#docker
|端口  | 说明 |
| :--- | :--- |
|1883  |  MQTT TCP 协议端口 |
|8883  |  MQTT/TCP SSL 端口 |
|8083  |  MQTT/WebSocket 端口 |
|8084  |  MQTT/WebSocket with SSL 端口 |
|8080  |  HTTP API 端口 |
|18083 |  Dashboard 管理控制台端口 |

* Pravega 0.6.1
http://pravega.io/docs/latest/getting-started/

* InfluxDB 1.7
https://docs.influxdata.com/influxdb/v1.7/introduction/installation/

* Grafana 6.7.1
https://grafana.com/grafana/download
默认监听端口300: http://127.0.0.1:3000

## Running Instruction
1. Keep EMQX, Pravega, Influxdb, Grafana running

2. Navigate to the distance-calculator path and compile the whole project
```
mvn clean package
```

3. start the pravega gateway
```
java -cp target\server-monitor-1.0-SNAPSHOT.jar com.dellemc.appdev.starterkit.Gateway
```

3. Run flink job
```
java -cp target\server-monitor-1.0-SNAPSHOT.jar com.dellemc.appdev.starterkit.Flink
```

4. send server loadavg info to mqtt broker, the payload structure shows as following:

{
    "server":"",
    "timestamp": "",
    "load":,
}

```
#!/bin/bash
while :
do
    curl -v --basic -u admin:public -H "Content-Type: application/json" -d "$(echo "`cat /proc/loadavg` `date +%s`" | awk '{ print "{\"qos\":0, \"retain\":false, \"topic\":\"server-monitor\", \"client_id\":\"node1\", \"payload\":\x27{\"server\":\"node1\",\"timestamp\":\""$6"\",\"load\":\""$1" "$2" "$3"\"}\x27}" }')" http://127.0.0.1:18083/api/v3/mqtt/publish
    sleep .5
done
```

5. Open grafana portal, and import dashboard from dashboard.json

There you go!