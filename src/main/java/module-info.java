module com.school.mqtt {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.eclipse.paho.client.mqttv3;
    requires org.mongodb.driver.core;

    opens com.school.mqtt to javafx.fxml;
    exports com.school.mqtt;
}