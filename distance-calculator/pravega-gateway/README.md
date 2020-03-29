# Pravega Gateway
Ingest data from Mqtt Broker into pravega as gateway.

### Envs:
PRAVEGA_CONTROLLER: The URI to the pravega controller in the form "tcp://host:port". It is "tcp://127.0.0.1:9090" by default.

PRAVEGA_SCOPE: The scope name of the stream to read from. It is "distance-calculator" by default.

PRAVEGA_STREAM: The name of the stream to write. It is "distance-calculator-stream" by default.

MQTT_BROKER_URL: The URL to the mqtt broker. It is "tcp://127.0.0.1:1883" by default.

MQTT_TOPIC: The topic of mqtt broker to subscribe. It is "distance-calculator" by default.


