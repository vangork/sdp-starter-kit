package com.dellemc.appdev.starterkit;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.sql.Timestamp;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GatewayCallback implements MqttCallback {
    private MqttSub mqttSub;
    private PravegaClient pravegaWriter;

    public GatewayCallback(MqttSub mqttSub, PravegaClient pravegaWriter) {
        this.mqttSub = mqttSub;
        this.pravegaWriter = pravegaWriter;
    }
    /****************************************************************/
	/* Methods to implement the MqttCallback interface              */
	/****************************************************************/

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    @Override
	public void connectionLost(Throwable cause) {
		// Called when the connection to the server has been lost.
		// An application may choose to implement reconnection
		// logic at this point. This sample simply reconnects.
		System.out.println("[Error] [mqtt] Connection to " + mqttSub.brokerUri + " was lost. Error: " + cause);
		System.exit(1);
	}

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    @Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// Called when a message has been delivered to the
		// server. The token passed in here is the same one
		// that was passed to or returned from the original call to publish.
		// This allows applications to perform asynchronous
		// delivery without blocking until delivery completes.
		//
		// This sample demonstrates asynchronous deliver and
		// uses the token.waitForCompletion() call in the main thread which
		// blocks until the delivery has completed.
		// Additionally the deliveryComplete method will be called if
		// the callback is set on the client
		//
		// If the connection to the server breaks before delivery has completed
		// delivery of a message will complete after the client has re-connected.
		// The getPendingTokens method will provide tokens for any messages
		// that are still to be delivered.
	}

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    @Override
	public void messageArrived(String topic, MqttMessage message) {
		// Called when a message arrives from the server that matches any
		// subscription made by the client
		String time = new Timestamp(System.currentTimeMillis()).toString();
        System.out.printf("[Info] [gateway] At %s received topic '%s' qos '%s' message '%s'\n", time, topic, 
                          message.getQos(), new String(message.getPayload()));

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			ServerStatus serverStatus = objectMapper.readValue(message.getPayload(), ServerStatus.class);
			pravegaWriter.write(serverStatus.server, serverStatus);
		}
		catch (Exception e) {
            System.out.println("[Error] [gateway] Failed to write event. Error: " + e.toString());
		}
	}
}