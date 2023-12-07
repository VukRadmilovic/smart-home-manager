package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.Measurement;
import com.ftn.uns.ac.rs.smarthome.models.devices.Device;
import com.ftn.uns.ac.rs.smarthome.models.dtos.DeviceDetailsDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.MeasurementsRequestDTO;

import java.util.List;

public interface IDeviceService {
    List<Device> findAll();
    List<DeviceDetailsDTO> findByOwnerId(Integer ownerId);
    void update(Device device);
    void setDeviceStillThere(int id);
    List<Measurement> getPaginatedByMeasurementNameAndDeviceIdInTimeRange(MeasurementsRequestDTO requestDTO);

}
