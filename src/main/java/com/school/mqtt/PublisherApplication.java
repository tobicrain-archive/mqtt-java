package com.school.mqtt;

import com.school.mqtt.util.MQTTUtil;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class PublisherApplication extends Application {

    TextField ipAddressInput = new TextField();
    TextField topicInput = new TextField();
    TextField messageInput = new TextField();
    Button startButton = new Button("Starte MQTT");
    Button publishButton = new Button("Publishen");

    static MqttClient mqttClient;

    public static void main(String[] args) {
        launch(args);
    }

    private void setupContent(Stage primaryStage) {
        primaryStage.setTitle("MQTT Application");

        ipAddressInput.setPromptText("IP-Adresse");
        topicInput.setPromptText("Topic eingeben");
        messageInput.setPromptText("Nachricht eingeben");
    }

    private void updateContent() {
        if (mqttClient.isConnected()) {
            startButton.setText("Stoppe MQTT");
            ipAddressInput.setDisable(true);
            publishButton.setDisable(false);
        } else {
            startButton.setText("Starte MQTT");
            ipAddressInput.setDisable(false);
            publishButton.setDisable(true);
        }
    }

    private void setupListeners() {
        startButton.setOnAction(event -> {
            String ipAddress = ipAddressInput.getText();
            try {
                if (((Button) event.getSource()).getText().equals("Starte MQTT")) {
                    mqttClient = MQTTUtil.getMqttClient("tcp://"+ipAddress+":1883");
                    MQTTUtil.connect(mqttClient);
                    System.out.println("Connected to MQTT Broker");
                } else {
                    MQTTUtil.disconnect(mqttClient);
                    System.out.println("Disconnected from MQTT Broker");
                }

                updateContent();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        });

        // Füge Event-Handler zum Publish-Button hinzu
        publishButton.setOnAction(event -> {
            String topic = topicInput.getText();
            String message = messageInput.getText();

            try {
                mqttClient.publish(topic, message.getBytes(), 0, false);
                System.out.println("Published message: " + message);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void start(Stage primaryStage) {


        setupListeners();

        setupContent(primaryStage);

        // Erstelle Layout mit GridPane
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setVgap(10);
        layout.setHgap(10);

        // Füge Input-Felder und Buttons zum Layout hinzu
        layout.add(ipAddressInput, 0, 0);
        layout.add(startButton, 1, 0);
        layout.add(topicInput, 0, 1);
        layout.add(messageInput, 0, 2);
        layout.add(publishButton, 1, 2);

        // Erstelle Szene und füge Layout hinzu
        Scene scene = new Scene(layout, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
