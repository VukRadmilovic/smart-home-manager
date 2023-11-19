package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.devices.Device;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.ThermometerDTO;

import java.util.List;

public interface IDeviceService {
    void register(ThermometerDTO thermometerDTO);
    List<Device> findAll();
    void update(Device device);
    void setDeviceStillThere(int id);
}
