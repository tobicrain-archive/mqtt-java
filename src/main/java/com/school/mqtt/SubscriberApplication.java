package com.school.mqtt;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.school.mqtt.util.Constants;
import com.school.mqtt.util.MQTTUtil;
import com.school.mqtt.util.MongoUtil;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class SubscriberApplication extends Application {

    static MongoClient mongoClient = MongoUtil.getMongoClient();
    static MongoDatabase database = MongoUtil.getMainDatabase(mongoClient);

    private final TextField topicTextField = new TextField();
    private final TextField ipAddressTextField = new TextField();
    private final Button stopMongodbButton = new Button("Stoppe MongoDB");
    private final Button startMqttButton = new Button("Starte MQTT");

    private final Button subscribeButton = new Button("Subscriben");
    private final ListView<String> messageListView = new ListView<>();
    private final VBox root = new VBox();
    private final Scene scene = new Scene(root, 400, 400);

    static MqttClient mqttClient;

    private void setupListeners() {
        stopMongodbButton.setOnAction(event -> MongoUtil.close(mongoClient));

        startMqttButton.setOnAction(event -> {
            try {
                if (((Button) event.getSource()).getText().equals("Starte MQTT")) {
                    String ip = ipAddressTextField.getText();
                    mqttClient = MQTTUtil.getMqttClient("tcp://"+ip+":1883");
                    MQTTUtil.connect(mqttClient);
                } else {
                    MQTTUtil.disconnect(mqttClient);
                }

                updateContent();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        });

        ipAddressTextField.textProperty().addListener((observable, oldValue, newValue) -> updateContent());

        subscribeButton.setOnAction(event -> {
            String t = topicTextField.getText();
            try {
                mqttClient.subscribe(t, (topic, message) -> {
                    System.out.println("Received message: " + new String(message.getPayload()));
                    Document document = Document.parse(new String(message.getPayload()));
                    database.getCollection(Constants.TABLE_NAME).insertOne(document);

                    messageListView.getItems().add(new String(message.getPayload()));
                });
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void updateContent() {
        subscribeButton.setDisable(mqttClient == null || !mqttClient.isConnected());
        startMqttButton.setDisable(ipAddressTextField.getText().isEmpty());
        startMqttButton.setText(mqttClient == null || !mqttClient.isConnected() ? "Starte MQTT" : "Stoppe MQTT");

        ipAddressTextField.setDisable(mqttClient != null && mqttClient.isConnected());
    }

    private void setupContent(Stage primaryStage) {
        primaryStage.setTitle("Meine JavaFX-Anwendung");
        topicTextField.setPromptText("Topic eingeben");
        updateContent();
    }

    private void setupLayout() {
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        root.getChildren().addAll(
                new Label("IP eingeben:"),
                ipAddressTextField,
                stopMongodbButton,
                startMqttButton,
                new Label("Topic eingeben:"),
                topicTextField,
                subscribeButton,
                messageListView
        );
    }

    @Override
    public void start(Stage primaryStage) {

        setupListeners();
        setupContent(primaryStage);
        setupLayout();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
