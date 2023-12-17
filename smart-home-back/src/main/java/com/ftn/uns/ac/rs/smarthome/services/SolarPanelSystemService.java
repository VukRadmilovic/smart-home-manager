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
    public SolarPanelSystemService(PropertyRepository propertyRepository,
                                   DeviceRepository deviceRepository,
                                   MessageSource messageSource,
                                   S3API fileServerService) throws IOException {
        super(propertyRepository, deviceRepository, messageSource, fileServerService);
    }

    @Override
    protected SolarPanelSystem createDevice(SolarPanelSystemDTO dto, Property property) {
        return new SolarPanelSystem(dto, property);
    }
}
