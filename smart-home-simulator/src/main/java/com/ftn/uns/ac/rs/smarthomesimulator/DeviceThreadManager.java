package com.ftn.uns.ac.rs.smarthomesimulator;

import com.ftn.uns.ac.rs.smarthomesimulator.models.TemperatureUnit;
import com.ftn.uns.ac.rs.smarthomesimulator.models.devices.*;
import com.ftn.uns.ac.rs.smarthomesimulator.services.MqttService;
import com.ftn.uns.ac.rs.smarthomesimulator.services.interfaces.IDeviceService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceThreadManager {
    private final Map<Integer, Thread> deviceThreadMap = new ConcurrentHashMap<>();
    private final Set<Integer> nonSimulatedDevices = ConcurrentHashMap.newKeySet();
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
        } else if (device.getClass().equals(AirConditioner.class)) {
            AirConditioner ac = (AirConditioner) device;
            addDeviceThread(device.getId(),
                    new ThermometerThread(TemperatureUnit.CELSIUS,
                            mqttService, device.getId()).getNewSimulatorThread());
        } else if (device.getClass().equals(WashingMachine.class)) {
            WashingMachine machine = (WashingMachine) device;
            addDeviceThread(device.getId(),
                    new ThermometerThread(TemperatureUnit.FAHRENHEIT,
                            mqttService, device.getId()).getNewSimulatorThread());
        } else if (device.getClass().equals(SolarPanelSystem.class)) {
            SolarPanelSystem system = (SolarPanelSystem) device;
            addDeviceThread(device.getId(),
                    new ThermometerThread(TemperatureUnit.CELSIUS,
                            mqttService, device.getId()).getNewSimulatorThread());
        } else if (device.getClass().equals(Battery.class)) {
            Battery battery = (Battery) device;
            addDeviceThread(device.getId(),
                    new ThermometerThread(TemperatureUnit.CELSIUS,
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

    public void addNonSimulatedDevice(Integer deviceId) {
        nonSimulatedDevices.add(deviceId);
    }

    public boolean isSimulatedDevice(Integer deviceId) {
        return !nonSimulatedDevices.contains(deviceId);
    }

    public void shutOffDevice(Integer deviceId) {
        if (deviceThreadMap.containsKey(deviceId)) {
            addNonSimulatedDevice(deviceId);
            removeDeviceThread(deviceId);
        }
    }
}
