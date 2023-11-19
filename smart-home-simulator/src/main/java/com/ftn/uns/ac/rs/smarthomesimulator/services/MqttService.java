package com.ftn.uns.ac.rs.smarthomesimulator.services;

import com.ftn.uns.ac.rs.smarthomesimulator.config.MqttConfiguration;
import lombok.Getter;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MqttService {

    @Getter
    private final List<String> pubStatusesTopics;
    @Getter
    private final List<String> pubMeasurementsTopics;
    @Getter
    private final List<String> subTopics;
    @Getter
    private int pubTopicIndex;
    private final MqttConfiguration mqttConfiguration;

    public MqttService(MqttConfiguration mqttConfiguration) throws MqttException {
        this.pubStatusesTopics = new ArrayList<>();
        this.pubMeasurementsTopics = new ArrayList<>();
        this.subTopics = new ArrayList<>();
        this.pubTopicIndex = 0;
        this.mqttConfiguration = mqttConfiguration;
        this.mqttConfiguration.getClient().subscribe("commands", 2);
    }

    public void publishMeasurementMessageLite(String message) throws MqttException {
        this.mqttConfiguration.getClient().publish("measurements", new MqttMessage(message.getBytes()));
    }

    public void publishStatusMessageLite(String message) throws MqttException {
        this.mqttConfiguration.getClient().publish("statuses", new MqttMessage(message.getBytes()));
    }

    private void addCommandsSubTopic(long uuid) throws MqttException {
        String topicName = "commands_" + uuid;
        this.subTopics.add(topicName);
        this.mqttConfiguration.getClient().subscribe(topicName,1);
    }

    private void addMeasurementsPubTopic(long uuid) {
       this.pubMeasurementsTopics.add("measurements_" + uuid);
    }

    private void addStatusesPubTopic(long uuid) {
        this.pubStatusesTopics.add("statuses_" + uuid);
    }

    private void changePubTopicIndex() {
        synchronized (this) {
            pubTopicIndex = (pubTopicIndex + 1) % 3;
        }
    }

    public void registerNewServerTopics(long uuid) throws MqttException {
        this.addCommandsSubTopic(uuid);
        this.addMeasurementsPubTopic(uuid);
        this.addStatusesPubTopic(uuid);
    }

    public void publishStatusMessage(String message) throws MqttException {
        this.mqttConfiguration.getClient().publish(this.pubStatusesTopics.get(pubTopicIndex), new MqttMessage(message.getBytes()));
        System.out.println("Sent status message to " + this.pubStatusesTopics.get(pubTopicIndex) + " topic.");
        changePubTopicIndex();
    }

    public void publishMeasurementMessage(String message) throws MqttException {
        this.mqttConfiguration.getClient().publish(this.pubMeasurementsTopics.get(pubTopicIndex), new MqttMessage(message.getBytes()));
        System.out.println("Sent status message to " + this.pubMeasurementsTopics.get(pubTopicIndex) + " topic.");
        changePubTopicIndex();
    }
}
