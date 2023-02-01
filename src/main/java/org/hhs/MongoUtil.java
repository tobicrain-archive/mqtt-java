package org.hhs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

public class MongoUtil {

    static MongoClient getMongoClient() {
        MongoClient client = MongoClients.create(String.format(Constants.CONNECTION_STRING, Constants.HOST, Constants.PORT));
        System.out.println("Connected to database: " + Constants.DATABASE_NAME);
        return client;
    }

    static MongoDatabase getMainDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(Constants.DATABASE_NAME);
    }

    static void debugPrint(MongoDatabase database) {
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
    }
}
