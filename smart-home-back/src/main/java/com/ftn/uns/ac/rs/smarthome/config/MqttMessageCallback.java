package com.ftn.uns.ac.rs.smarthome.config;

import com.ftn.uns.ac.rs.smarthome.services.InfluxService;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IDeviceService;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;


@Service
public class MqttMessageCallback implements MqttCallback {

    private final InfluxService influxService;
    private final IDeviceService deviceService;

    public MqttMessageCallback(InfluxService influxService,
                               IDeviceService deviceService) {
        this.influxService = influxService;
        this.deviceService = deviceService;
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
    //TODO: Change according to the 4.6 requirement listed in the specification.
    @Override public void messageArrived(String topic, MqttMessage mqttMessage) {
        String message = new String(mqttMessage.getPayload());
        String[] data = message.split(",");
        String measurementObject = data[0];
        String valueWithUnit = data[1];
        float value = Float.parseFloat(valueWithUnit.substring(0, valueWithUnit.length() - 1));
        char unit = valueWithUnit.charAt(valueWithUnit.length() - 1);
        String deviceId = data[2];
        influxService.save(measurementObject, value, new Date(),
                Map.of("deviceId", deviceId, "unit", String.valueOf(unit)));
        System.out.println("Message arrived: " + message + ", ID: " + mqttMessage.getId());

        if (measurementObject.equals("status")) {
            if (value == 1) {
                deviceService.setDeviceStillThere(Integer.parseInt(deviceId));
            }
        }
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
