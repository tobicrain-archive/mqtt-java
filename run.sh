#!/bin/bash

# Pfade zu den Jar-Dateien als Variablen speichern
CLASSES_PATH=./target/classes
MONGO_DRIVER_SYNC=:libs/mongodb-driver-sync-4.3.1.jar
BSON=:libs/bson-4.3.1.jar
MONGO_DRIVER_CORE=:libs/mongodb-driver-core-4.3.1.jar
PAHO_CLIENT=:libs/org.eclipse.paho.client.mqttv3-1.2.0.jar

# Verwenden Sie die Variablen im Befehl
java -classpath $CLASSES_PATH$MONGO_DRIVER_SYNC$BSON$MONGO_DRIVER_CORE$PAHO_CLIENT org.hhs.Main