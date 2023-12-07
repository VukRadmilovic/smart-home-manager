package com.ftn.uns.ac.rs.smarthomesockets.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftn.uns.ac.rs.smarthomesockets.models.Measurement;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class MqttMessageCallback implements MqttCallback {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public MqttMessageCallback(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

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
    @Override public void messageArrived(String topic, MqttMessage mqttMessage) throws JsonProcessingException {
        String message = new String(mqttMessage.getPayload());
        String[] data = message.split(",");
        String valueWithUnit = data[1];
        String deviceId = data[2];
        float value = Float.parseFloat(valueWithUnit.substring(0, valueWithUnit.length() - 1));
        Measurement measurement = new Measurement((new Date()).getTime(),value);
        String toSend = jsonMapper.writeValueAsString(measurement);
        messagingTemplate.convertAndSend("/thermometer/freshest/" + deviceId, toSend);
        System.out.println("Message received. ID:" + mqttMessage.getId() + ", Message: " + message);
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
