package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.models.Property;
import com.ftn.uns.ac.rs.smarthome.models.TemperatureUnit;
import com.ftn.uns.ac.rs.smarthome.models.devices.Device;
import com.ftn.uns.ac.rs.smarthome.models.devices.Thermometer;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.ThermometerDTO;
import com.ftn.uns.ac.rs.smarthome.repositories.DeviceRepository;
import com.ftn.uns.ac.rs.smarthome.repositories.PropertyRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IThermometerService;
import com.ftn.uns.ac.rs.smarthome.utils.ImageCompressor;
import com.ftn.uns.ac.rs.smarthome.utils.S3API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.paho.mqttv5.common.MqttException;
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
public class ThermometerService implements IThermometerService {
    private static final Logger log = LoggerFactory.getLogger(ThermometerService.class);
    private final PropertyRepository propertyRepository;
    private final MessageSource messageSource;
    private final S3API fileServerService;
    private final MqttService mqttService;
    private final DeviceRepository deviceRepository;

    public ThermometerService(PropertyRepository propertyRepository,
                              MessageSource messageSource,
                              S3API fileServerService,
                              MqttService mqttService,
                              DeviceRepository deviceRepository) {
        this.propertyRepository = propertyRepository;
        this.messageSource = messageSource;
        this.fileServerService = fileServerService;
        this.mqttService = mqttService;
        this.deviceRepository = deviceRepository;
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

    @Override
    public void changeThermometerTempUnit(Integer id, TemperatureUnit unit) {
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("device.notFound", null, Locale.getDefault()));
        }
        if (!(device.get() instanceof Thermometer thermometer)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("device.notThermometer", null, Locale.getDefault()));
        }
        thermometer.setTemperatureUnit(unit);
        deviceRepository.save(thermometer);
        try {
            mqttService.publishCommandMessage(String.valueOf(id));
        } catch (MqttException e) {
            log.error("Error while publishing command message to topic: {}", e.getMessage());
        }
    }
}
