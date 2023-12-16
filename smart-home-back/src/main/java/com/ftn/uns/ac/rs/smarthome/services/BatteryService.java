package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.models.Property;
import com.ftn.uns.ac.rs.smarthome.models.devices.Battery;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.BatteryDTO;
import com.ftn.uns.ac.rs.smarthome.repositories.DeviceRepository;
import com.ftn.uns.ac.rs.smarthome.repositories.PropertyRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IBatteryService;
import com.ftn.uns.ac.rs.smarthome.utils.S3API;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BatteryService extends GenericDeviceService<Battery, BatteryDTO> implements IBatteryService {
    public BatteryService(PropertyRepository propertyRepository,
                          DeviceRepository deviceRepository,
                          MessageSource messageSource,
                          S3API fileServerService) throws IOException {
        super(propertyRepository, deviceRepository, messageSource, fileServerService);
    }

    @Override
    protected Battery createDevice(BatteryDTO dto, Property property) {
        return new Battery(property, dto.getName(), dto.getPowerSource(), dto.getEnergyConsumption(),
                dto.getCapacity());
    }
}
