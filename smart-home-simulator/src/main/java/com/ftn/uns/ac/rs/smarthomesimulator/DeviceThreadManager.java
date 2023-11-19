package com.ftn.uns.ac.rs.smarthomesimulator;

import com.ftn.uns.ac.rs.smarthomesimulator.models.devices.Device;
import com.ftn.uns.ac.rs.smarthomesimulator.models.devices.Thermometer;
import com.ftn.uns.ac.rs.smarthomesimulator.services.MqttService;
import com.ftn.uns.ac.rs.smarthomesimulator.services.interfaces.IDeviceService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceThreadManager {
    private final Map<Integer, Thread> deviceThreadMap = new ConcurrentHashMap<>();
    private final MqttService mqttService;
    private final IDeviceService deviceService;

    public DeviceThreadManager(MqttService mqttService,
                               IDeviceService deviceService) {
        this.mqttService = mqttService;
        this.deviceService = deviceService;
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

    private void removeDeviceThread(Integer deviceId) {
        getDeviceThread(deviceId).interrupt();
        deviceThreadMap.remove(deviceId);
    }

    public void reloadDeviceThread(int id) {
        removeDeviceThread(id);
        deviceService.findById(id).ifPresent(this::addDeviceThread);
    }
}
