package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.models.Property;
import com.ftn.uns.ac.rs.smarthome.models.devices.SolarPanelSystem;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.SolarPanelSystemDTO;
import com.ftn.uns.ac.rs.smarthome.repositories.DeviceRepository;
import com.ftn.uns.ac.rs.smarthome.repositories.PropertyRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.ISolarPanelSystemService;
import com.ftn.uns.ac.rs.smarthome.utils.S3API;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SolarPanelSystemService extends GenericDeviceService<SolarPanelSystem, SolarPanelSystemDTO> implements ISolarPanelSystemService {
    private final MqttService mqttService;

    public SolarPanelSystemService(PropertyRepository propertyRepository,
                                   DeviceRepository deviceRepository,
                                   MessageSource messageSource,
                                   S3API fileServerService,
                                   MqttService mqttService) throws IOException {
        super(propertyRepository, deviceRepository, messageSource, fileServerService);
        this.mqttService = mqttService;
    }

    @Override
    protected SolarPanelSystem createDevice(SolarPanelSystemDTO dto, Property property) {
        return new SolarPanelSystem(dto, property);
    }

    @Override
    public void turnOffSolarPanelSystem(Integer id) {
        SolarPanelSystem solarPanelSystem = (SolarPanelSystem) deviceRepository.findById(id).orElse(null);
        if (solarPanelSystem != null) {
            solarPanelSystem.setIsOn(false);
            deviceRepository.save(solarPanelSystem);

            try {
                mqttService.publishMessageToTopic("OFF", "commands/sps/" + solarPanelSystem.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void turnOnSolarPanelSystem(Integer id) {
        SolarPanelSystem solarPanelSystem = (SolarPanelSystem) deviceRepository.findById(id).orElse(null);
        if (solarPanelSystem != null) {
            solarPanelSystem.setIsOn(true);
            deviceRepository.save(solarPanelSystem);

            try {
                mqttService.publishMessageToTopic("ON", "commands/sps/" + solarPanelSystem.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
