package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.TemperatureUnit;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.ThermometerDTO;

public interface IThermometerService {
    void register(ThermometerDTO thermometerDTO);
    void changeThermometerTempUnit(Integer id, TemperatureUnit unit);
}
