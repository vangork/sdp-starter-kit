package io.pravega.inject;

// All parameters will come from environment variables. This makes it easy
// to configure on Docker, Mesos, Kubernetes, etc.
public class CommonParams {
    // By default, we will connect to a standalone Pravega running on localhost.
    public static  String getBrokerUrl() {
        return getEnvVar("MQTT_BROKER_URL", "tcp://127.0.0.1:1883");
    }
    public static  String getTopic() {
        return getEnvVar("MQTT_TOPIC", "distance-calculator");
    }
    public static  String getDataFile() {
        return getEnvVar("MQTT_DATA_FILE", "Distance.csv");
    }
    public static  String getUserName() {
        return getEnvVar("MQTT_USERNAME", "default");
    }
    public static  String getPassword() {
        return getEnvVar("MQTT_PASSWORD", "default");
    }
    public static  String getIfAuth() {
        return getEnvVar("MQTT_USE_AUTH", "false");
    }
    public static  String getIfInsecure() {
        return getEnvVar("MQTT_ALLOW_INSECURE", "false");
    }
    private static String getEnvVar(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}