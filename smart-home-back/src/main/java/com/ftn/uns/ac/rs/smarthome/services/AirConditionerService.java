package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.models.Property;
import com.ftn.uns.ac.rs.smarthome.models.devices.AirConditioner;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.AirConditionerDTO;
import com.ftn.uns.ac.rs.smarthome.repositories.DeviceRepository;
import com.ftn.uns.ac.rs.smarthome.repositories.PropertyRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IAirConditionerService;
import com.ftn.uns.ac.rs.smarthome.utils.S3API;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AirConditionerService extends GenericDeviceService<AirConditioner, AirConditionerDTO> implements IAirConditionerService {

    public AirConditionerService(PropertyRepository propertyRepository,
                                 DeviceRepository deviceRepository,
                                 MessageSource messageSource,
                                 S3API fileServerService) throws IOException {
        super(propertyRepository, deviceRepository, messageSource, fileServerService);
    }

    @Override
    protected AirConditioner createDevice(AirConditionerDTO dto, Property property) {
        return new AirConditioner(dto, property);
    }
}
