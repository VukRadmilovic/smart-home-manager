package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.dtos.PropertyDTO;

import java.util.List;

public interface IPropertyService {
    void registerProperty(PropertyDTO propertyDTO);

    List<PropertyDTO> getProperty(String username);

    Object getAllProperty();

    void approveProperty(Integer id);

    void denyProperty(Integer id);

    Object getAllUnapprovedProperty();
}