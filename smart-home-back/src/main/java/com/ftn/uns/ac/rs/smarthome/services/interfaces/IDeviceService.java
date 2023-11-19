package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.devices.Device;

import java.util.List;

public interface IDeviceService {
    List<Device> findAll();
    void update(Device device);
    void setDeviceStillThere(int id);
}
