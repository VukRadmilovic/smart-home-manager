package com.ftn.uns.ac.rs.smarthomesimulator.services;

import com.ftn.uns.ac.rs.smarthomesimulator.config.MqttConfiguration;
import com.ftn.uns.ac.rs.smarthomesimulator.config.MqttMessageCallback;
import lombok.Getter;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

@Getter
@Service
public class MqttService {
    private final MqttConfiguration mqttConfiguration;
    private final MqttMessageCallback mqttMessageCallback;

    public MqttService(MqttMessageCallback mqttMessageCallback) throws Exception {
        this.mqttMessageCallback = mqttMessageCallback;
        this.mqttConfiguration = new MqttConfiguration(mqttMessageCallback);
        //this.mqttConfiguration.getClient().subscribe("command/ac",2);
    }

    public void publishMessageLite(String message, String topic) throws MqttException {
        this.mqttConfiguration.getClient().publish(topic, new MqttMessage(message.getBytes()));
    }

    public void publishStatusMessageLite(String message) throws MqttException {
        this.mqttConfiguration.getClient().publish("statuses", new MqttMessage(message.getBytes()));
    }

    public void publishPowerConsumptionMessage(String message) throws MqttException {
        this.mqttConfiguration.getClient().publish("consumed", new MqttMessage(message.getBytes()));
    }
}
