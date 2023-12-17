package com.ftn.uns.ac.rs.smarthome.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftn.uns.ac.rs.smarthome.StillThereDevicesManager;
import com.ftn.uns.ac.rs.smarthome.models.ACStateChange;
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
import java.util.HashMap;
import java.util.Map;


@Service
public class MqttMessageCallback implements MqttCallback {

    private final InfluxService influxService;
    private final IDeviceService deviceService;
    private final StillThereDevicesManager stillThereDevicesManager;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public MqttMessageCallback(InfluxService influxService,
                               IDeviceService deviceService,
                               StillThereDevicesManager stillThereDevicesManager) {
        this.influxService = influxService;
        this.deviceService = deviceService;
        this.stillThereDevicesManager = stillThereDevicesManager;
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

    @Override public void messageArrived(String topic, MqttMessage mqttMessage) {
        String message = new String(mqttMessage.getPayload());
        if(topic.contains("states")) {
            try {
                Map<String,String> map = new HashMap<>();
                ACStateChange stateChange = jsonMapper.readValue(message, ACStateChange.class);
                if(stateChange.getExtraInfo() != null)
                    map = stateChange.getExtraInfo();
                map.put("userId",stateChange.getUserId().toString());
                map.put("deviceId", stateChange.getDeviceId().toString());
                influxService.save("states", stateChange.getChange(), new Date(), map);
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        else {
            String[] data = message.split(",");
            String measurementObject = data[0];
            String valueWithUnit = data[1];
            float value = Float.parseFloat(valueWithUnit.substring(0, valueWithUnit.length() - 1));
            char unit = valueWithUnit.charAt(valueWithUnit.length() - 1);
            String deviceIdStr = data[2];
            influxService.save(measurementObject, value, new Date(),
                    Map.of("deviceId", deviceIdStr, "unit", String.valueOf(unit)));
            System.out.println("Message arrived: " + message + ", ID: " + mqttMessage.getId());

            int deviceId = Integer.parseInt(deviceIdStr);
            if (measurementObject.equals("status") && value >= 1 &&
                    stillThereDevicesManager.isntThere(deviceId)) {
                deviceService.setDeviceStillThere(deviceId);
                stillThereDevicesManager.add(deviceId);
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
