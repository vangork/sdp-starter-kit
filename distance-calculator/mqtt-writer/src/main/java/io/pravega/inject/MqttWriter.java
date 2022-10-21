package io.pravega.inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MqttWriter {

    public static void main(String[] args) throws Exception {
        ObjectNode message = null;
        System.out.println("Connecting to MQTT Broker");
        MQTT mqtt = new MQTT();
        mqtt.setHost(CommonParams.getBrokerUrl());
        System.out.println("MQTT_BROKER_URL: " + CommonParams.getBrokerUrl());
        System.out.println("MQTT_TOPIC: " + CommonParams.getTopic());
        System.out.println("MQTT_DATA_FILE: "+ CommonParams.getDataFile());
        System.out.println("MQTT_USE_AUTH: "+ CommonParams.getIfAuth());
        if (CommonParams.getIfAuth().equalsIgnoreCase("true"))
        {
            // set all SDP related parameters
            mqtt.setUserName(CommonParams.getUserName());
            mqtt.setPassword(CommonParams.getPassword());
            System.out.println("MQTT_USERNAME: "+ CommonParams.getUserName());
            System.out.println("MQTT_PASSWORD: "+ "********" );

        }
        System.out.println("MQTT_ALLOW_INSECURE: "+ CommonParams.getIfInsecure());
        if ( CommonParams.getIfInsecure().equalsIgnoreCase("true"))
        {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext insecureSc = SSLContext.getInstance("TLSv1.2");
            insecureSc.init(null, trustAllCerts , new java.security.SecureRandom());
            mqtt.setSslContext(insecureSc);

        }
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        System.out.println("Connected to mqtt server");
        // Subscribe to  fidelityAds topic
        //Topic[] topics = { new Topic(CommonParams.getTopic(), QoS.AT_LEAST_ONCE)};
        //connection.subscribe(topics);

        //  Coverst CSV  data to JSON
        String data = DataGenerator.convertCsvToJson(CommonParams.getDataFile());
        // Deserialize the JSON message.
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonArray = objectMapper.readTree(data);
        if (jsonArray.isArray()) {
            for (JsonNode node : jsonArray) {
                message = (ObjectNode) node;
                connection.publish(CommonParams.getTopic(), message.toString().getBytes(), QoS.AT_LEAST_ONCE, false);
                System.out.println(message);
                Thread.sleep(1000);
            }
        }
    }
}
