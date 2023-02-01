package org.hhs;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {
    static MongoClient mongoClient = MongoUtil.getMongoClient();
    static MongoDatabase database = MongoUtil.getMainDatabase(mongoClient);

    static MqttClient mqttClient;

    static {
        try {
            mqttClient = MQTTUtil.getMqttClient();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {

        MongoUtil.debugPrint(database);

        try {
            MQTTUtil.connect(mqttClient);

            mqttClient.subscribe("temperature", (topic, message) -> {
                System.out.println("Received message: " + new String(message.getPayload()));
                Document document = Document.parse(new String(message.getPayload()));
                database.getCollection(Constants.TABLE_NAME).insertOne(document);
            });

            mqttClient.publish("temperature", "test".getBytes(), 0, false);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }
}