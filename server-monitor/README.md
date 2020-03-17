# nautilus-starter-kit-pcmon
To demo how to build a pc monitor with Nautilus

Architecture

IOT Client -> MQTT Server <- PravegaGateway -> PravegaServer <- Flink -> Influxdb <- Grafana

MQTT Brocker
https://docs.emqx.io/broker/v3/en/install.html#docker

1. docker pull emqx/emqx:v3.2.7
2. docker run -d --name emqx327 -e EMQX_ALLOW_ANONYMOUS=false -p 1883:1883 -p 8883:8883 -p 8083:8083 -p 8084:8084 -p 18083:18083 emqx/emqx:v3.2.7
1883    MQTT TCP 协议端口
8883	MQTT/TCP SSL 端口
8083	MQTT/WebSocket 端口
8084	MQTT/WebSocket with SSL 端口
8080	HTTP API 端口
18083	Dashboard 管理控制台端口

1. mvn exec:java -Dexec.mainClass=com.dellemc.appdev.starterkit.Pcmon -DMQTT_BROKER_URI=mqtt://127.0.0.1:1883 -DMQTT_USER=user -DMQTT_PASSWORD=pass -DMQTT_KEEP_ALIVE=60 -DMQTT_SUB_TOPIC=pcmon/cpu -DMQTT_SUB_CLIENT_ID=pravega-gateway-01 

2. curl -v --basic -u user:pass -H "Content-Type: application/json" -d "$(echo "`cat /proc/loadavg` `date +%s`" | awk '{ print "{\"qos\":0, \"retain\":false, \"topic\":\"pcmon/cpu\", \"client_id\":\"node1\", \"payload\":\x27{\"server\":\"node1\",\"timestamp\":\""$6"\",\"load\":\""$1" "$2" "$3"\"}\x27}" }')" http://127.0.0.1:18083/api/v3/mqtt/publish

{
    "server":"",
    "timestamp": "",
    "load":,
}

3. mvn exec:java -Dexec.mainClass=com.dellemc.appdev.starterkit.Flink