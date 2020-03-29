package io.pravega.example.writers;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;

import java.net.URI;

public class PravegaWriter {

    public static void main(String[] args) {
        try {
            URI controllerURI = Parameters.getControllerURI();
            String scope = Parameters.getScope();
            String streamName = Parameters.getStreamName();
            String routingKey = Parameters.getRoutingKeyAttributeName();
            String MQTT_BROKER_URL = Parameters.getBrokerUrl();
            String MQTT_TOPIC = Parameters.getTopic();

            System.out.println("Connecting to MQTT Broker");
            System.out.println("MQTT_BROKER_URL: " + MQTT_BROKER_URL);
            System.out.println("MQTT_TOPIC: " + MQTT_TOPIC);
            MQTT mqtt = new MQTT();
            mqtt.setHost(MQTT_BROKER_URL);
            mqtt.setClientId("pravega-gateway");
            mqtt.setCleanSession(false);
            BlockingConnection connection = mqtt.blockingConnection();
            connection.connect();
            System.out.println("Connected to MQTT blocker " + MQTT_BROKER_URL);

            // Subscribe to MQTT topic
            Topic[] topics = {new Topic(MQTT_TOPIC, QoS.AT_LEAST_ONCE)};
            connection.subscribe(topics);

            if (Parameters.isPravegaStandalone()) {
                try {
                    Utils.createStream(scope, streamName, controllerURI);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ClientConfig clientConfig = ClientConfig.builder()
                    .controllerURI(controllerURI)
                    .build();
            EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(scope, clientConfig);
            EventStreamWriter<JsonNode> writer = clientFactory.createEventWriter(streamName,
                    new JsonNodeSerializer(),
                    EventWriterConfig.builder().build());
            while(true) {
                Message record = connection.receive(1, TimeUnit.SECONDS);
                if (record != null) {
                    record.ack();
                    String message = new String(record.getPayload());
                    System.out.println("Writing message: " + message + " with routing-key: " + routingKey + " to stream " + scope + "/" + streamName);
                    // Deserialize the JSON message.
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode tree = objectMapper.readTree(message);
                    try {
                        writer.writeEvent(routingKey, tree);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
