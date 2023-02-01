package org.hhs;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.UUID;

public class Main {

    static MongoClient getMongoClient() {
        return MongoClients.create(String.format(Constants.CONNECTION_STRING, Constants.HOST, Constants.PORT));
    }

    static MongoDatabase getMainDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(Constants.DATABASE_NAME);
    }

    public static void main(String[] args) {


        String broker = "tcp://localhost:1883";
        String publishClient = "publish_client";

        MongoClient mongoClient = getMongoClient();

        MongoDatabase database = getMainDatabase(mongoClient);

        System.out.println("Connected to database: " + Constants.DATABASE_NAME);

        // list all collections
        for (String name : database.listCollectionNames()) {
            System.out.println(name);
        }

        if (!database.listCollectionNames().into(new ArrayList<String>()).contains(Constants.TABLE_NAME)) {
            database.createCollection(Constants.TABLE_NAME);
        }

        // list all documents in collection temperatures
        for (Document document : database.getCollection(Constants.TABLE_NAME).find()) {
            System.out.println(document);
        }

        String publisherId = UUID.randomUUID().toString();
        try {
            // subscribe to mqtt broker 10.100.240.12 on topic "temperature"
            MqttClient client = new MqttClient(broker, publishClient, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            client.connect(options);
            System.out.println("Connected to broker: " + broker);
            client.subscribe("temperature", (topic, message) -> {
                System.out.println("Received message: " + new String(message.getPayload()));
                Document document = Document.parse(new String(message.getPayload()));
                database.getCollection(Constants.TABLE_NAME).insertOne(document);
            });

            client.publish("temperature", "test".getBytes(), 0, false);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }
}