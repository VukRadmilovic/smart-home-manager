package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.AirConditionerDTO;

import java.io.IOException;

public interface IAirConditionerService {
    void register(AirConditionerDTO airConditionerDTO) throws IOException;
}
