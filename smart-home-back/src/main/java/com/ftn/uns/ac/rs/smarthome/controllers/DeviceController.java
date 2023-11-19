package com.ftn.uns.ac.rs.smarthome.controllers;

import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.ThermometerDTO;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IDeviceService;
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

    public DeviceController(IDeviceService deviceService,
                            MessageSource messageSource) {
        this.deviceService = deviceService;
        this.messageSource = messageSource;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerThermometer(@Valid @ModelAttribute ThermometerDTO thermometerDTO) {
        try {
            this.deviceService.register(thermometerDTO);
            return new ResponseEntity<>(messageSource.getMessage("device.registration.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }
}
