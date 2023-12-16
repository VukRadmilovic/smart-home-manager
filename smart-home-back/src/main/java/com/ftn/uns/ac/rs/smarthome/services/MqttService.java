package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.config.MqttConfiguration;
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
        this.mqttConfiguration.getClient().subscribe("$share/group/measurements",2);
        this.mqttConfiguration.getClient().subscribe("$share/group/statuses",2);
        this.mqttConfiguration.getClient().subscribe("$share/group/states",2);
    }

    public void publishCommandMessage(String message) throws MqttException {
        this.mqttConfiguration.getClient().publish("commands", new MqttMessage(message.getBytes()));
    }
}
