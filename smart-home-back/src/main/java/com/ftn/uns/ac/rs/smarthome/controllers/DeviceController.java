package com.ftn.uns.ac.rs.smarthome.controllers;

import com.ftn.uns.ac.rs.smarthome.models.TemperatureUnit;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.ThermometerDTO;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IDeviceService;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IThermometerService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Locale;

@CrossOrigin("http://localhost:5173/")
@RestController
@RequestMapping("/api/devices")
@Validated
public class DeviceController {
    private final IDeviceService deviceService;
    private final MessageSource messageSource;
    private final IThermometerService thermometerService;

    public DeviceController(IDeviceService deviceService,
                            MessageSource messageSource,
                            IThermometerService thermometerService) {
        this.deviceService = deviceService;
        this.messageSource = messageSource;
        this.thermometerService = thermometerService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerThermometer(@Valid @ModelAttribute ThermometerDTO thermometerDTO) {
        try {
            this.thermometerService.register(thermometerDTO);
            return new ResponseEntity<>(messageSource.getMessage("device.registration.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }

    @PutMapping(value = "/thermometer/{id}/{unit}")
    public ResponseEntity<?> changeTemperatureUnit(@PathVariable("id") Integer id, @PathVariable("unit") TemperatureUnit unit) {
        try {
            this.thermometerService.changeThermometerTempUnit(id, unit);
            return new ResponseEntity<>(messageSource.getMessage("thermometer.temperature.unit.change.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }
}
