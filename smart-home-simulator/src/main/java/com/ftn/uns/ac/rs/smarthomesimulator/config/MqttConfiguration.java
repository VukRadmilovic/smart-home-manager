package com.ftn.uns.ac.rs.smarthomesimulator.config;

import lombok.Getter;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MqttConfiguration {
    private final Properties env;
    private final String broker;
    private final String uniqueClientIdentifier;
    @Getter
    private final MqttClient client;

    public MqttConfiguration() throws Exception {
        this.env = new Properties();
        env.load(MqttConfiguration.class.getClassLoader().getResourceAsStream("application.properties"));
        this.broker = String.format("tcp://%s:%s", env.getProperty("mqtt.host"), env.getProperty("mqtt.port"));
        this.uniqueClientIdentifier = env.getProperty("mqtt.client_id");;
        this.client = this.mqttClient();
    }


    private MqttClient mqttClient() throws MqttException {
        MqttClient client = new MqttClient(this.broker, this.uniqueClientIdentifier, new MemoryPersistence());
        client.setCallback(new MessageCallback());
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setCleanStart(false);
        options.setAutomaticReconnect(true);
        options.setUserName(this.env.getProperty("mqtt.username"));
        options.setPassword(Objects.requireNonNull(this.env.getProperty("mqtt.password")).getBytes());
        client.connect(options);
        return client;
    }

    public static class MessageCallback implements MqttCallback {
        @Override
        public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
            System.out.println("Disconnected");
        }

        @Override
        public void mqttErrorOccurred(MqttException e) {
            System.out.println("MQTT error occurred.");
        }

        @Override public void messageArrived(String topic, MqttMessage mqttMessage) {
            String message = new String(mqttMessage.getPayload());
            System.out.println("Message arrived: " + message);
        }

        @Override
        public void deliveryComplete(IMqttToken iMqttToken) {
            System.out.println("Delivery complete, message ID: " + iMqttToken.getMessageId());
        }

        @Override
        public void connectComplete(boolean b, String s) {
            System.out.println("Connect complete, status:" + b + " " + s);
        }

        @Override
        public void authPacketArrived(int i, MqttProperties mqttProperties) {
            System.out.println("Auth packet arrived , status:" +  i + " " + mqttProperties.getAuthenticationMethod());
        }
    }
}
