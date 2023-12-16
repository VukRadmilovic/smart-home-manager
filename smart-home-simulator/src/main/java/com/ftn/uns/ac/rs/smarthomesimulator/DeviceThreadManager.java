package com.ftn.uns.ac.rs.smarthomesimulator;

import com.ftn.uns.ac.rs.smarthomesimulator.models.ACCommand;
import com.ftn.uns.ac.rs.smarthomesimulator.models.Command;
import com.ftn.uns.ac.rs.smarthomesimulator.models.enums.TemperatureUnit;
import com.ftn.uns.ac.rs.smarthomesimulator.models.devices.*;
import com.ftn.uns.ac.rs.smarthomesimulator.services.MqttService;
import com.ftn.uns.ac.rs.smarthomesimulator.services.interfaces.IDeviceService;
import com.ftn.uns.ac.rs.smarthomesimulator.threads.ACThread;
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
    private final IDeviceService deviceService;


    public DeviceThreadManager(MqttService mqttService,
                               IDeviceService deviceService) {
        this.mqttService = mqttService;
        this.deviceService = deviceService;
    }

    public void addDeviceThread(Device device, Command command) {
        if (device.getClass().equals(Thermometer.class)) {
            addDeviceThreadInternal(device.getId(),
                    new ThermometerThread(((Thermometer) device).getTemperatureUnit(),
                            mqttService, device.getId()).getNewSimulatorThread());
        } else if (device.getClass().equals(AirConditioner.class)) {
            AirConditioner ac = (AirConditioner) device;
            addDeviceThreadInternal(device.getId(),
                    new ACThread(ac,(ACCommand) command).getNewSimulatorThread());
        } else if (device.getClass().equals(WashingMachine.class)) {
            WashingMachine machine = (WashingMachine) device;
            addDeviceThreadInternal(device.getId(),
                    new ThermometerThread(TemperatureUnit.FAHRENHEIT,
                            mqttService, device.getId()).getNewSimulatorThread());
        } else if (device.getClass().equals(SolarPanelSystem.class)) {
            SolarPanelSystem system = (SolarPanelSystem) device;
            addDeviceThreadInternal(device.getId(),
                    new ThermometerThread(TemperatureUnit.CELSIUS,
                            mqttService, device.getId()).getNewSimulatorThread());
        } else if (device.getClass().equals(Battery.class)) {
            Battery battery = (Battery) device;
            addDeviceThreadInternal(device.getId(),
                    new ThermometerThread(TemperatureUnit.CELSIUS,
                            mqttService, device.getId()).getNewSimulatorThread());
        } else if (device.getClass().equals(Charger.class)) {
            Charger charger = (Charger) device;
            addDeviceThreadInternal(device.getId(),
                    new ThermometerThread(TemperatureUnit.CELSIUS,
                            mqttService, device.getId()).getNewSimulatorThread());
        } else if (device.getClass().equals(Lamp.class)) {
            Lamp lamp = (Lamp) device;
            addDeviceThreadInternal(device.getId(),
                    new ThermometerThread(TemperatureUnit.CELSIUS,
                            mqttService, device.getId()).getNewSimulatorThread());
        } else if (device.getClass().equals(Gate.class)) {
            Gate gate = (Gate) device;
            addDeviceThreadInternal(device.getId(),
                    new ThermometerThread(TemperatureUnit.CELSIUS,
                            mqttService, device.getId()).getNewSimulatorThread());
        } else if (device.getClass().equals(SprinklerSystem.class)) {
            SprinklerSystem system = (SprinklerSystem) device;
            addDeviceThreadInternal(device.getId(),
                    new ThermometerThread(TemperatureUnit.CELSIUS,
                            mqttService, device.getId()).getNewSimulatorThread());
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
