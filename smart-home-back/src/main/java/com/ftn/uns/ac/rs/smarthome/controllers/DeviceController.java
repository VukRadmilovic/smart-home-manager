package com.ftn.uns.ac.rs.smarthome.controllers;

import com.ftn.uns.ac.rs.smarthome.models.Measurement;
import com.ftn.uns.ac.rs.smarthome.models.TemperatureUnit;
import com.ftn.uns.ac.rs.smarthome.models.User;
import com.ftn.uns.ac.rs.smarthome.models.dtos.CommandsDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.CommandsRequestDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.DeviceDetailsDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.MeasurementsStreamRequestDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.AirConditionerDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.ThermometerDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.WashingMachineDTO;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IAirConditionerService;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IDeviceService;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IThermometerService;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IWashingMachineService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@CrossOrigin("http://localhost:5173/")
@RestController
@RequestMapping("/api/devices")
@Validated
public class DeviceController {
    private final IDeviceService deviceService;
    private final MessageSource messageSource;
    private final IThermometerService thermometerService;
    private final IAirConditionerService airConditionerService;
    private final IWashingMachineService washingMachineService;

    public DeviceController(IDeviceService deviceService,
                            MessageSource messageSource,
                            IThermometerService thermometerService,
                            IAirConditionerService airConditionerService,
                            IWashingMachineService washingMachineService) {
        this.deviceService = deviceService;
        this.messageSource = messageSource;
        this.thermometerService = thermometerService;
        this.airConditionerService = airConditionerService;
        this.washingMachineService = washingMachineService;
    }

    @PostMapping(value = "/registerThermo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerThermometer(@Valid @ModelAttribute ThermometerDTO thermometerDTO) {
        try {
            this.thermometerService.register(thermometerDTO);
            return new ResponseEntity<>(messageSource.getMessage("device.registration.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/registerAC", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerAirConditioner(@Valid @ModelAttribute AirConditionerDTO airConditionerDTO) {
        try {
            this.airConditionerService.register(airConditionerDTO);
            return new ResponseEntity<>(messageSource.getMessage("device.registration.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/registerWM", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerWashingMachine(@Valid @ModelAttribute WashingMachineDTO washingMachineDTO) {
        try {
            this.washingMachineService.register(washingMachineDTO);
            return new ResponseEntity<>(messageSource.getMessage("device.registration.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    @GetMapping(value = "/ownerAll")
    public ResponseEntity<?> changeTemperatureUnit() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<DeviceDetailsDTO> devices = this.deviceService.findByOwnerId(user.getId());
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @GetMapping(value = "/measurements", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<Measurement>> getMeasurements(@RequestParam Long from, @RequestParam Long to, @RequestParam Integer deviceId, @RequestParam String measurement) {
        try {
            MeasurementsStreamRequestDTO dto = new MeasurementsStreamRequestDTO(from, to, 5000,0,deviceId,measurement);
            List<List<Measurement>> measurements = this.deviceService.getStreamByMeasurementNameAndDeviceIdInTimeRange(dto);
            return Flux.fromIterable(measurements);
        } catch(ResponseStatusException ex) {
            return Flux.error(new ResponseStatusException(ex.getStatus(), ex.getMessage()));
        }
    }

    @GetMapping(value = "/commands")
    public ResponseEntity<?> getCommands(@RequestParam Long from,
                                                   @RequestParam Long to,
                                                   @RequestParam Integer deviceId,
                                                   @RequestParam Long page,
                                                   @RequestParam Long size,
                                                   @RequestParam Boolean firstFetch,
                                                   @RequestParam Integer userId) {
        try {
            CommandsRequestDTO dto = new CommandsRequestDTO(from, to, deviceId,page,size,firstFetch,userId);
            CommandsDTO measurements = this.deviceService.getCommandsByTimeRangeAndUserId(dto);
            return new ResponseEntity<>(measurements, HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }
}
