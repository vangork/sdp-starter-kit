package com.dellemc.appdev.starterkit;

import java.net.URI;

// All parameters will come from environment variables. This makes it easy
// to configure on Docker, Mesos, Kubernetes, etc.
public class Parameters {
    private static String getEnvVar(String name, String defaultValue) {
        //String value = System.getenv(name);
        String value = System.getProperty(name);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }

    public static String getMqttBrokerUri() {
        return getEnvVar("MQTT_BROKER_URI", "mqtt://localhost:1883");
    }
    public static boolean getMqttCleanSession() {
        return Boolean.parseBoolean(getEnvVar("MQTT_CLEAN_SESSION", "true"));
    }
    public static String getMqttUser() {
        return getEnvVar("MQTT_USER", "");
    }
    public static String getMqttPassword() {
        return getEnvVar("MQTT_PASSWORD", "");
    }
    public static int getMqttKeepAlive() {
        return Integer.parseInt(getEnvVar("MQTT_KEEP_ALIVE", "60"));
    }
    public static String getMqttSubTopic() {
        return getEnvVar("MQTT_SUB_TOPIC", "#");
    }
    public static int getMqttSubQos() {
        return Integer.parseInt(getEnvVar("MQTT_SUB_QOS", "0"));
    }

    public static String getMqttSubCliendId() {
        return getEnvVar("MQTT_SUB_CLIENT_ID", "mqttsub01");
    }

    public static URI getPravegaUri() {
        return URI.create(getEnvVar("PRAVEGA_CONTROLLER_URI", "tcp://127.0.0.1:9090"));
    }
    public static String getPravegaScope() {
        return getEnvVar("PRAVEGA_SCOPE", "server-monitor");
    }
    public static String getPravegaStream() {
        return getEnvVar("PRAVEGA_STREAM", "cpuinfo");
    }
    public static String getPravegaReaderGroup() {
        return getEnvVar("PRAVEGA_READER_GROUP", "server-monitor-readers");
    }
    public static String getPravegaReaderId() {
        return getEnvVar("PRAVEGA_READER_GROUP", "server-monitor-reader-01");
    }

    public static String getInfluxDbUri() {
        return getEnvVar("INFLUXDB_URI", "http://monitoring-influxdb.default.svc.cluster.local:8086");
    }
    public static String getInfluxDbUser() {
        return getEnvVar("INFLUXDB_USER", "root");
    }
    public static String getInfluxDbPassword() {
        return getEnvVar("INFLUXDB_PASSWORD", "root");
    }
    public static String getInfluxDbDatabase() {
        return getEnvVar("INFLUXDB_DATABASE", "server-monitor-db");
    }
}
