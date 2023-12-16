package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.BatteryDTO;

import java.io.IOException;

public interface IBatteryService {
    void register(BatteryDTO batteryDTO) throws IOException;
}
