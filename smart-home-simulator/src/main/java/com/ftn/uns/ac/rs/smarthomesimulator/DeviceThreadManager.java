package com.ftn.uns.ac.rs.smarthomesimulator;

import com.ftn.uns.ac.rs.smarthomesimulator.models.devices.Device;
import com.ftn.uns.ac.rs.smarthomesimulator.models.devices.Thermometer;
import com.ftn.uns.ac.rs.smarthomesimulator.services.MqttService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceThreadManager {
    private final Map<Integer, Thread> deviceThreadMap = new ConcurrentHashMap<>();
    private final MqttService mqttService;

    public DeviceThreadManager(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    public void addDeviceThread(Device device) {
        if (device.getClass().equals(Thermometer.class)) {
            addDeviceThread(device.getId(),
                    new ThermometerThread(((Thermometer) device).getTemperatureUnit(),
                            mqttService, device.getId()).getNewSimulatorThread());
        }
    }

    private void addDeviceThread(Integer deviceId, Thread thread) {
        deviceThreadMap.put(deviceId, thread);
    }

    public Thread getDeviceThread(Integer deviceId) {
        return deviceThreadMap.get(deviceId);
    }

    public void removeDeviceThread(Integer deviceId) {
        deviceThreadMap.remove(deviceId);
    }
}
