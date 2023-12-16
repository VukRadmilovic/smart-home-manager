package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.SolarPanelSystemDTO;

import java.io.IOException;

public interface ISolarPanelSystemService {
    void register(SolarPanelSystemDTO solarPanelSystemDTO) throws IOException;
}
