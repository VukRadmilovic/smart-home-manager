package com.ftn.uns.ac.rs.smarthomesockets.services;

import com.ftn.uns.ac.rs.smarthomesockets.config.MqttConfiguration;
import lombok.Getter;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

@Getter
@Service
public class MqttService {

    private final MqttConfiguration mqttConfiguration;

    public MqttService(MqttConfiguration mqttConfiguration) throws MqttException {
        this.mqttConfiguration = mqttConfiguration;
        this.mqttConfiguration.getClient().subscribe("measurements",2);
        this.mqttConfiguration.getClient().subscribe("ac",2);
        this.mqttConfiguration.getClient().subscribe("status/ac",2);
        this.mqttConfiguration.getClient().subscribe("status/sps", 2);
    }

    public void publishMeasurementMessageLite(String message, String topic) throws MqttException {
        this.mqttConfiguration.getClient().publish(topic, new MqttMessage(message.getBytes()));
    }

}
