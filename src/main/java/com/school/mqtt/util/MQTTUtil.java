package com.school.mqtt.util;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTUtil {
    public static MqttClient getMqttClient(String ip) throws MqttException {
        return new MqttClient(ip, Constants.PUBLISH_CLIENT, new MemoryPersistence());
    }

    public static void connect(MqttClient mqttClient) throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        mqttClient.connect(options);
        System.out.println("Connected to broker: " + Constants.BROKER);
    }

    public static void disconnect(MqttClient mqttClient) throws MqttException {
        mqttClient.disconnect();
        System.out.println("Disconnected from broker: " + Constants.BROKER);
    }
}
