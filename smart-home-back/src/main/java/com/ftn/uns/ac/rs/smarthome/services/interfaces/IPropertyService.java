package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.dtos.PropertyDTO;

public interface IPropertyService {
    void registerProperty(PropertyDTO propertyDTO);
}