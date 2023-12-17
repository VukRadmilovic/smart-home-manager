package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.GateDTO;

import java.io.IOException;

public interface IGateService {
    void register(GateDTO gateDTO) throws IOException;
}
