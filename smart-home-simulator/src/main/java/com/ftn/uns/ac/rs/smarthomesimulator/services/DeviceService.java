package com.ftn.uns.ac.rs.smarthomesimulator.services;


import com.ftn.uns.ac.rs.smarthomesimulator.models.devices.Device;
import com.ftn.uns.ac.rs.smarthomesimulator.repositories.DeviceRepository;
import com.ftn.uns.ac.rs.smarthomesimulator.services.interfaces.IDeviceService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService implements IDeviceService {
    private final DeviceRepository deviceRepository;
    private final MessageSource messageSource;

    public DeviceService(DeviceRepository deviceRepository,
                         MessageSource messageSource) {
        this.deviceRepository = deviceRepository;
        this.messageSource = messageSource;
    }


    @Override
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }
}
