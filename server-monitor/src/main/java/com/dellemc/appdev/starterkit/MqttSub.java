package com.dellemc.appdev.starterkit;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class MqttSub
{
    // Private variables
	private MqttClient 			client;

	// Public properties
    public String 				brokerUri;
	public MqttConnectOptions  	conOpt;

    /**
	 * Constructs an instance of the sample client wrapper
	 * @param brokerUri the url of the server to connect to
	 * @param clientId the client id to connect with
	 * @param cleanSession clear state at end of connection or not (durable or non-durable subscriptions)
     * @param user the username to connect with
     * @param password the password for the user
	 * @throws MqttException
	 */
    public MqttSub(String brokerUri, String clientId, boolean cleanSession, String user, String password, int keepAlive) throws MqttException {
		this.brokerUri = brokerUri;

    	try {
    		// Construct the connection options object that contains connection parameters
    		// such as cleanSession and LWT
	    	conOpt = new MqttConnectOptions();
	    	conOpt.setCleanSession(cleanSession);
	    	if(password != null ) {
	    	  	conOpt.setPassword(password.toCharArray());
	    	}
	    	if(user != null) {
	    	  	conOpt.setUserName(user);
			}

			// Set Mqtt connection keep alive interval
			conOpt.setKeepAliveInterval(keepAlive);

    		// Construct an MQTT blocking mode client
			client = new MqttClient(this.brokerUri, clientId);

		} catch (MqttException e) {
            System.out.println("[Error] [mqtt] Unable to set up client. Error: " + e.toString());
            throw e;
		}
	}

    /**
     * Subscribe to a topic on an MQTT server
     * Once subscribed this method waits for the messages to arrive from the server
     * that match the subscription. It continues listening for messages until the enter key is
     * pressed.
     * @param topic to subscribe to (can be wild carded)
     * @param qos the maximum quality of service to receive messages at for this subscription
     * @throws MqttException
     */
    public void connect(String topic, int qos, MqttCallback callback) throws MqttException {
		// Set this wrapper as the callback handler
		client.setCallback(callback);

		try {
			// Connect to the MQTT server
			client.connect(conOpt);
			System.out.printf("[Info] [mqtt] Connected to '%s' with client ID '%s'\n", brokerUri, client.getClientId());
			
			// Subscribe to the requested topic
			// The QoS specified is the maximum level that messages will be sent to the client at.
			// For instance if QoS 1 is specified, any messages originally published at QoS 2 will
			// be downgraded to 1 when delivering to the client but messages published at 1 and 0
			// will be received at the same level they were published at.
			System.out.printf("[Info] [mqtt] Subscribing to topic '%s' with qos '%s'\n", topic, qos);
			client.subscribe(topic, qos);
		}
		catch (MqttException e) {
            System.out.println("[Error] [mqtt] Unable to connect or subscribe. Error: " + e.toString());
            throw e;
		}
	}

    public void disconnect() throws MqttException {
		client.disconnect();
		System.out.println("[Info] [mqtt] Disconnected");
		client.close();
	}
}