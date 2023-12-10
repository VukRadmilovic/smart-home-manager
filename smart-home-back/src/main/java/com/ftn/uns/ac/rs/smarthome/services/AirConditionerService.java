package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.config.MqttConfiguration;
import com.ftn.uns.ac.rs.smarthome.models.Property;
import com.ftn.uns.ac.rs.smarthome.models.devices.AirConditioner;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.AirConditionerDTO;
import com.ftn.uns.ac.rs.smarthome.repositories.DeviceRepository;
import com.ftn.uns.ac.rs.smarthome.repositories.PropertyRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IAirConditionerService;
import com.ftn.uns.ac.rs.smarthome.utils.ImageCompressor;
import com.ftn.uns.ac.rs.smarthome.utils.S3API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

@Service
public class AirConditionerService implements IAirConditionerService {
    private static final Logger log = LoggerFactory.getLogger(AirConditionerService.class);
    private final PropertyRepository propertyRepository;
    private final DeviceRepository deviceRepository;
    private final MessageSource messageSource;
    private final S3API fileServerService;
    private final Properties env;

    public AirConditionerService(PropertyRepository propertyRepository,
                                 DeviceRepository deviceRepository,
                                 MessageSource messageSource,
                                 S3API fileServerService) throws IOException {

        this.propertyRepository = propertyRepository;
        this.deviceRepository = deviceRepository;
        this.messageSource = messageSource;
        this.fileServerService = fileServerService;
        this.env = new Properties();
        env.load(MqttConfiguration.class.getClassLoader().getResourceAsStream("application.properties"));
    }

    public void register(@Valid AirConditionerDTO dto) throws IOException {
        Optional<Property> property = propertyRepository.findById(dto.getPropertyId());
        if (property.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("property.notFound", null, Locale.getDefault()));
        }

        AirConditioner airConditioner = new AirConditioner(dto, property.get());
        AirConditioner savedAC;
        try {
            savedAC = deviceRepository.save(airConditioner);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("device.name.unique", null, Locale.getDefault()));
        }

        log.info("AC saved: {}", savedAC);
        log.info("AC ID: {}", savedAC.getId());
        String path = env.getProperty("tempfolder.path");
        Path filepath = Paths.get(path, dto.getImage().getOriginalFilename());
        try {
            dto.getImage().transferTo(filepath);
        } catch (Exception e) {
            log.error("Error while uploading image: {}", e.getMessage());
            deviceRepository.deleteById(savedAC.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("image.upload.failed", null, Locale.getDefault()));
        }
        File file = new File(filepath.toString());
        File compressed;
        try {
            compressed = ImageCompressor.compressImage(file, 0.1f, "d" + savedAC.getId());
        } catch (ResponseStatusException e) {
            log.error("Error while compressing image: {}", e.getMessage());
            deviceRepository.deleteById(savedAC.getId());
            throw e;
        }

        String[] tokens = compressed.getName().split("/");
        String key = tokens[tokens.length - 1];
        String type = dto.getImage().getContentType();
        String bucket = "images";
        fileServerService.put(bucket, "devices/" + key, compressed, type).thenApply(lol ->
                        compressed.delete())
                .exceptionally(e -> false);
        String pathToImage = "http://127.0.0.1:9000/" + bucket + '/' + "devices/" + key;
        savedAC.setImage(pathToImage);
        deviceRepository.save(savedAC);
    }
}
