package org.hhs;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTUtil {
    public static MqttClient getMqttClient() throws MqttException {
        return new MqttClient(Constants.BROKER, Constants.PUBLISH_CLIENT, new MemoryPersistence());
    }

    public static void connect(MqttClient mqttClient) throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        mqttClient.connect(options);
        System.out.println("Connected to broker: " + Constants.BROKER);
    }
}
