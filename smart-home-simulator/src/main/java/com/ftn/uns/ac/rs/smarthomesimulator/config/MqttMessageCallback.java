package com.ftn.uns.ac.rs.smarthomesimulator.config;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.stereotype.Service;


@Service
public class MqttMessageCallback implements MqttCallback {

    @Override
    /*
     * Callback in case the server disconnected from MQTT client.
     */
    public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
        System.out.println("Disconnected");
    }

    /*
     * Callback in case an error occurred regarding MQTT client.
     */
    @Override
    public void mqttErrorOccurred(MqttException e) {
        System.out.println("MQTT error occurred.");
    }

    /*
     * Callback in case the app received a message from one of the topics it's subscribed to.
     * These include the "measurements" and "statuses" topics.
     */
    //TODO: Change according to the 4.6 requirement listed in the specification.
    @Override public void messageArrived(String topic, MqttMessage mqttMessage) {
        System.out.println("Message received. ID:" + mqttMessage.getId() + ", Message: " + mqttMessage);
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
