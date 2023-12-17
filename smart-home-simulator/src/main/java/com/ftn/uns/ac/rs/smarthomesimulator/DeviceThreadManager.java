package com.ftn.uns.ac.rs.smarthomesimulator;

import com.ftn.uns.ac.rs.smarthomesimulator.models.ACCommand;
import com.ftn.uns.ac.rs.smarthomesimulator.models.Command;
import com.ftn.uns.ac.rs.smarthomesimulator.models.enums.TemperatureUnit;
import com.ftn.uns.ac.rs.smarthomesimulator.models.devices.*;
import com.ftn.uns.ac.rs.smarthomesimulator.services.MqttService;
import com.ftn.uns.ac.rs.smarthomesimulator.services.interfaces.IDeviceService;
import com.ftn.uns.ac.rs.smarthomesimulator.threads.ACThread;
import com.ftn.uns.ac.rs.smarthomesimulator.threads.SolarPanelSystemThread;
import com.ftn.uns.ac.rs.smarthomesimulator.threads.ThermometerThread;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@Component
public class DeviceThreadManager {
    private final Map<Integer, Thread> deviceThreadMap = new ConcurrentHashMap<>();

    private final Set<Integer> nonSimulatedDevices = ConcurrentHashMap.newKeySet();

    private final MqttService mqttService;

    public DeviceThreadManager(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    public void addDeviceThread(Device device, Command command) {
        switch (device.getClass().getSimpleName()) {
            case "Thermometer":
                addDeviceThreadInternal(device.getId(),
                        new ThermometerThread(((Thermometer) device).getTemperatureUnit(),
                                mqttService, device.getId()).getNewSimulatorThread());
                break;
            case "AirConditioner":
                AirConditioner ac = (AirConditioner) device;
                addDeviceThreadInternal(device.getId(),
                        new ACThread(ac, (ACCommand) command).getNewSimulatorThread());
                break;
            case "SolarPanelSystem":
                SolarPanelSystem system = (SolarPanelSystem) device;
                addDeviceThreadInternal(device.getId(),
                        new SolarPanelSystemThread(system).getNewSimulatorThread());
            default:
                break;
        }
    }

    private void addDeviceThreadInternal(Integer deviceId, Thread thread) {
        deviceThreadMap.put(deviceId, thread);
    }

    public Thread getDeviceThread(Integer deviceId) {
        return deviceThreadMap.get(deviceId);
    }

    public boolean isSimulatedDevice(Integer deviceId) {
        return !nonSimulatedDevices.contains(deviceId);
    }

}
