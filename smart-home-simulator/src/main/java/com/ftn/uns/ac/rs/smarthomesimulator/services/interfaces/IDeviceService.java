package com.ftn.uns.ac.rs.smarthomesimulator.services.interfaces;

import com.ftn.uns.ac.rs.smarthomesimulator.models.devices.Device;

import java.util.List;

public interface IDeviceService {
    List<Device> findAll();
}
