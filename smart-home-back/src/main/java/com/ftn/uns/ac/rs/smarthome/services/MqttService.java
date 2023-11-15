package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.config.MqttConfiguration;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class MqttService {

    private final MqttConfiguration mqttConfiguration;

    public MqttService(MqttConfiguration mqttConfiguration) throws Exception {
        this.mqttConfiguration = mqttConfiguration;
        this.mqttConfiguration.getClient().subscribe("$share/group/measurements",2);
    }
}
