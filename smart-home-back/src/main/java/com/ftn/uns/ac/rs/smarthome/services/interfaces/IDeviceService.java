package com.ftn.uns.ac.rs.smarthome.services.interfaces;

import com.ftn.uns.ac.rs.smarthome.models.Measurement;
import com.ftn.uns.ac.rs.smarthome.models.devices.Device;
import com.ftn.uns.ac.rs.smarthome.models.dtos.CommandsDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.CommandsRequestDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.DeviceDetailsDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.MeasurementsStreamRequestDTO;

import java.util.List;

public interface IDeviceService {
    List<Device> findAll();
    List<DeviceDetailsDTO> findByOwnerId(Integer ownerId);
    void update(Device device);
    void setDeviceStillThere(int id);
    List<List<Measurement>> getStreamByMeasurementNameAndDeviceIdInTimeRange(MeasurementsStreamRequestDTO requestDTO);
    CommandsDTO getCommandsByTimeRangeAndUserId(CommandsRequestDTO request);
}
