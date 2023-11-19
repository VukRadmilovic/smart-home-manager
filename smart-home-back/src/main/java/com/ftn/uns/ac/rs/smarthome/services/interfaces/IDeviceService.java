package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.ThermometerDTO;

public interface IDeviceService {
    void register(ThermometerDTO thermometerDTO);
}
