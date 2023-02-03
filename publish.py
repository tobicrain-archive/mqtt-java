#!/usr/bin/env python3
#publishd die Temperatur und die Feuchtigkeit vom Sensor
#Tosques 28.1.2023

#import der notwendigen Biblioteken
import paho.mqtt.client as mqtt
import adafruit_dht
import time
from influxdb import InfluxDBClient

#notwenide Variablen
dht22gpioPin=4

#IP-Adresse Broker
broker='127.0.0.1'
port=1883

publish_topic="daten"

retain_message=False

#für TemperaturSensor Adafruit bekannt machen
dhtDevice=adafruit_dht.DHT22(dht22gpioPin)

def on_connect(client, userdata, flags, rc):
	#print(rc)
	if rc==0:
		print("broker {} verbunden - returnCode={}".format(broker,rc))
	else:
		print("broker {} kann nicht verbunden werden - returnCode{}".format(broker, rc))

def on_message(client, userdata, message):
	global m
	print("jetzt in on_message")
	m="message_received ", str(message.payload.decode("utf-8"))
	print("message ", m)

#MQTT-Verbindung
client=mqtt.Client("MeinMQTT-Client")
client.on_connect=on_connect #Callback-Funktion einbinden
client.on_message=on_message
client.connect(broker, port)
client.loop_start()

print("Verbindung zu Broker {} wird aufgebaut".format(broker))

#publish-Abschnitt
while True:
	try:
		temperatur = dhtDevice.temperature
		print("Topic:{}/temperatur -- Temperatur={}".format(publish_topic,temperatur))
		client.publish("{}/temperatur".format(publish_topic), "{:.1f}".format(temperatur))

		#hier anpassen/erweitern, dass die Feuchtigkeit gepublisht wird

		time.sleep(5)
	except RuntimeError as error:
		print(error.args[0])
		time.sleep(5)

	except KeyboardInterrupt:
		client.disconnect();
		client.loop_stop