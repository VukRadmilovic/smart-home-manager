package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.models.Measurement;
import com.ftn.uns.ac.rs.smarthome.models.devices.Device;
import com.ftn.uns.ac.rs.smarthome.models.devices.Thermometer;
import com.ftn.uns.ac.rs.smarthome.models.dtos.DeviceDetailsDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.MeasurementsRequestDTO;
import com.ftn.uns.ac.rs.smarthome.repositories.DeviceRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class DeviceService implements IDeviceService {
    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;
    private final InfluxService influxService;
    private final MessageSource messageSource;

    public DeviceService(DeviceRepository deviceRepository,
                         InfluxService influxService,
                         MessageSource messageSource) {
        this.deviceRepository = deviceRepository;
        this.influxService = influxService;
        this.messageSource = messageSource;
    }

    @Override
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    @Override
    public List<DeviceDetailsDTO> findByOwnerId(Integer ownerId) {
        List<Device> ownersDevices =  deviceRepository.findByOwnerId(ownerId);
        List<DeviceDetailsDTO> devicesDetails = new ArrayList<>();
        for(Device device : ownersDevices) {
            String type = "";
            if(device instanceof Thermometer) type = "THERMOMETER";
            DeviceDetailsDTO details = new DeviceDetailsDTO(
                    device.getId(),
                    type,
                    device.getName(),
                    device.getPowerSource(),
                    device.getEnergyConsumption(),
                    device.getImage(),
                    device.getProperty().getName()
            );
            devicesDetails.add(details);
        }
        System.out.println(devicesDetails);
        return devicesDetails;
    }

    @Override
    public void update(Device device) {
        deviceRepository.save(device);
    }

    @Override
    public void setDeviceStillThere(int id) {
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) {
            log.error("Device with id {} not found", id);
            return;
        }
        device.get().setStillThere(true);
        deviceRepository.save(device.get());
    }

    @Override
    public List<Measurement> getPaginatedByMeasurementNameAndDeviceIdInTimeRange(MeasurementsRequestDTO requestDTO) {
        if(requestDTO.getFrom() >= requestDTO.getTo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("dateRange.invalid", null, Locale.getDefault()));
        }
        if(deviceRepository.findById(requestDTO.getDeviceId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("device.notFound", null, Locale.getDefault()));
        }
        return influxService.findPaginatedByMeasurementNameAndDeviceIdInTimeRange(requestDTO);
    }
}
