package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.config.MqttConfiguration;
import lombok.Getter;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.stereotype.Service;

@Getter
@Service
public class MqttService {

    private final MqttConfiguration mqttConfiguration;

    public MqttService(MqttConfiguration mqttConfiguration) throws Exception {
        this.mqttConfiguration = mqttConfiguration;
        subscribe();
    }

    public void subscribe() throws MqttException {
        this.mqttConfiguration.getClient().subscribe("$share/group/topic",2);
    }
}
