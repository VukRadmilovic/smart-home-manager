package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.SprinklerSystemDTO;

import java.io.IOException;

public interface ISprinklerSystemService {
    void register(SprinklerSystemDTO sprinklerSystemDTO) throws IOException;
}
