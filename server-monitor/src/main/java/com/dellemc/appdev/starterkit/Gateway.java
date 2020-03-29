package com.dellemc.appdev.starterkit;

import java.net.URI;

public class Gateway
{
    public static void main( String[] args )
    {
		// turn off logger
		// org.apache.log4j.BasicConfigurator.configure();
		// org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);

		// mqtt params
		String mqttBrokerUri = Parameters.getMqttBrokerUri();
		boolean mqttCleanSession = Parameters.getMqttCleanSession();
		String mqttUser = Parameters.getMqttUser();
		String mqttPassword = Parameters.getMqttPassword();
		int mqttKeepAlive = Parameters.getMqttKeepAlive();
		String mqttSubTopic = Parameters.getMqttSubTopic();
		int mqttsubQos = Parameters.getMqttSubQos();
		String clientId = Parameters.getMqttSubCliendId();
		
		// pravega params
		URI pravegaControllerUri = Parameters.getPravegaUri();
		String pravegaScope = Parameters.getPravegaScope();
		String pravegaStream = Parameters.getPravegaStream();
		
		try {
			MqttSub mqttSub = new MqttSub(mqttBrokerUri, clientId, mqttCleanSession, mqttUser, mqttPassword, mqttKeepAlive);
			PravegaClient pravegaClient = new PravegaClient(pravegaControllerUri, pravegaScope, pravegaStream);
			GatewayCallback gatewayCallback = new GatewayCallback(mqttSub, pravegaClient);
			pravegaClient.init();
			mqttSub.connect(mqttSubTopic, mqttsubQos, gatewayCallback);
		} catch(Exception e) {
			// Display full details of any exception that occurs
			System.out.println("msg " + e.getMessage());
			System.out.println("loc " + e.getLocalizedMessage());
			System.out.println("cause " + e.getCause());
			System.out.println("excep " + e);
			e.printStackTrace();
		}
    }
}
