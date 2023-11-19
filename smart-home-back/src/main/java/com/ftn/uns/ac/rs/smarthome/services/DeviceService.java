package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.models.Property;
import com.ftn.uns.ac.rs.smarthome.models.devices.Device;
import com.ftn.uns.ac.rs.smarthome.models.devices.Thermometer;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.ThermometerDTO;
import com.ftn.uns.ac.rs.smarthome.repositories.DeviceRepository;
import com.ftn.uns.ac.rs.smarthome.repositories.PropertyRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IDeviceService;
import com.ftn.uns.ac.rs.smarthome.utils.ImageCompressor;
import com.ftn.uns.ac.rs.smarthome.utils.S3API;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;

@Service
public class DeviceService implements IDeviceService {
    private final DeviceRepository deviceRepository;
    private final PropertyRepository propertyRepository;
    private final MessageSource messageSource;
    private final S3API fileServerService;

    public DeviceService(DeviceRepository deviceRepository,
                         PropertyRepository propertyRepository,
                         MessageSource messageSource,
                         S3API fileServerService) {
        this.deviceRepository = deviceRepository;
        this.propertyRepository = propertyRepository;
        this.messageSource = messageSource;
        this.fileServerService = fileServerService;
    }

    public void register(@Valid ThermometerDTO dto) {
        Optional<Property> property = propertyRepository.findById(dto.getPropertyId());
        if (property.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("property.notFound", null, Locale.getDefault()));
        }
        Thermometer thermometer = new Thermometer(property.get(), dto.getName(), dto.getDescription(),
                dto.getPowerSource(), dto.getEnergyConsumption(), dto.getTemperatureUnit());
        Thermometer savedThermometer = deviceRepository.save(thermometer);
        Path filepath = Paths.get("src/main/resources/temp", dto.getImage().getOriginalFilename());
        try {
            dto.getImage().transferTo(filepath);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("image.upload.failed", null, Locale.getDefault()));
        }
        File file = new File(filepath.toString());
        File compressed = ImageCompressor.compressImage(file, 0.1f, "d" + savedThermometer.getId());
        String[] tokens = compressed.getName().split("/");
        String key = tokens[tokens.length - 1];
        String type = dto.getImage().getContentType();
        String bucket = "images";
        fileServerService.put(bucket, "devices/" + key, compressed, type);
        String pathToImage = "http://127.0.0.1:9000/" + bucket + '/' + "devices/" + key;
        savedThermometer.setImage(pathToImage);
        deviceRepository.save(savedThermometer);
    }
}
