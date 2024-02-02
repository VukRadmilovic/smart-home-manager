package com.ftn.uns.ac.rs.smarthome.controllers;

import com.ftn.uns.ac.rs.smarthome.models.Measurement;
import com.ftn.uns.ac.rs.smarthome.models.TemperatureUnit;
import com.ftn.uns.ac.rs.smarthome.models.User;
import com.ftn.uns.ac.rs.smarthome.models.UserSearchInfo;
import com.ftn.uns.ac.rs.smarthome.models.dtos.*;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.*;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.*;
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
    private final ISolarPanelSystemService solarPanelSystemService;
    private final IBatteryService batteryService;
    private final IChargerService chargerService;
    private final ILampService lampService;
    private final IGateService gateService;
    private final ISprinklerSystemService sprinklerSystemService;

    private final IDeviceControlService deviceControlService;

    public DeviceController(IDeviceService deviceService,
                            MessageSource messageSource,
                            IThermometerService thermometerService,
                            IAirConditionerService airConditionerService,
                            IWashingMachineService washingMachineService,
                            ISolarPanelSystemService solarPanelSystemService,
                            IBatteryService batteryService,
                            IChargerService chargerService,
                            ILampService lampService,
                            IGateService gateService,
                            ISprinklerSystemService sprinklerSystemService,
                            IDeviceControlService deviceControlService) {
        this.deviceService = deviceService;
        this.messageSource = messageSource;
        this.thermometerService = thermometerService;
        this.airConditionerService = airConditionerService;
        this.washingMachineService = washingMachineService;
        this.solarPanelSystemService = solarPanelSystemService;
        this.batteryService = batteryService;
        this.chargerService = chargerService;
        this.lampService = lampService;
        this.gateService = gateService;
        this.sprinklerSystemService = sprinklerSystemService;
        this.deviceControlService = deviceControlService;
    }

    @PostMapping(value = "/registerThermometer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PostMapping(value = "/registerAirConditioner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PostMapping(value = "/registerWashingMachine", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PostMapping(value = "/registerSolarPanelSystem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerSolarPanelSystem(@Valid @ModelAttribute SolarPanelSystemDTO solarPanelSystemDTO) {
        try {
            this.solarPanelSystemService.register(solarPanelSystemDTO);
            return new ResponseEntity<>(messageSource.getMessage("device.registration.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/registerBattery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerBattery(@Valid @ModelAttribute BatteryDTO batteryDTO) {
        try {
            this.batteryService.register(batteryDTO);
            return new ResponseEntity<>(messageSource.getMessage("device.registration.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/registerCharger", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerCharger(@Valid @ModelAttribute ChargerDTO chargerDTO) {
        try {
            this.chargerService.register(chargerDTO);
            return new ResponseEntity<>(messageSource.getMessage("device.registration.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/registerLamp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerLamp(@Valid @ModelAttribute DeviceDTO lampDTO) {
        try {
            this.lampService.register(lampDTO);
            return new ResponseEntity<>(messageSource.getMessage("device.registration.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/registerGate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerGate(@Valid @ModelAttribute GateDTO gateDTO) {
        try {
            this.gateService.register(gateDTO);
            return new ResponseEntity<>(messageSource.getMessage("device.registration.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/registerSprinklerSystem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerSprinklerSystem(@Valid @ModelAttribute SprinklerSystemDTO sprinklerSystemDTO) {
        try {
            this.sprinklerSystemService.register(sprinklerSystemDTO);
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
    public ResponseEntity<?> getAllOwnersDevices() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<DeviceDetailsDTO> devices = this.deviceService.findByOwnerId(user.getId());
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @GetMapping(value = "/shared")
    public ResponseEntity<?> getAllSharedDevicesWithOwner() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<DeviceDetailsDTO> devices = this.deviceControlService.findByShared(user.getId());
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


    @PutMapping(value = "/sps/{id}/off")
    public ResponseEntity<?> turnOffSolarPanelSystem(@PathVariable("id") Integer id) {
        try {
            this.solarPanelSystemService.turnOffSolarPanelSystem(id);
            return new ResponseEntity<>(messageSource.getMessage("sps.turn.off.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }

    @PutMapping(value = "/sps/{id}/on")
    public ResponseEntity<?> turnOnSolarPanelSystem(@PathVariable("id") Integer id) {
        try {
            this.solarPanelSystemService.turnOnSolarPanelSystem(id);
            return new ResponseEntity<>(messageSource.getMessage("sps.turn.on.success", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
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

    @PutMapping(value = "/shareControl/{deviceId}")
        public ResponseEntity<?> editDeviceControl(@PathVariable("deviceId") Integer deviceId, @Valid @RequestBody DeviceControlDTO deviceControl) {
        try {
            this.deviceControlService.editDeviceControl(deviceId, deviceControl.getDetails());
            return new ResponseEntity<>(messageSource.getMessage("deviceControl.added", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }

    @PutMapping(value = "/shareControl/property/{propertyId}")
    public ResponseEntity<?> editDeviceControlForProperty(@PathVariable("propertyId") Integer propertyId, @Valid @RequestBody DeviceControlDTO deviceControl) {
        try {
            this.deviceControlService.editDeviceControlForProperty(propertyId, deviceControl.getDetails());
            return new ResponseEntity<>(messageSource.getMessage("deviceControl.added", null, Locale.getDefault()), HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }

    @GetMapping(value = "/shareControl/get/{deviceId}")
    public ResponseEntity<?> getSharedControlByDeviceId(@PathVariable("deviceId") Integer deviceId) {
        try {
            List<UserSearchInfo> usersInfo = this.deviceControlService.getDeviceControlUserInfo(deviceId);
            return new ResponseEntity<>(usersInfo, HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }

    @GetMapping(value = "/shareControl/get/property/{propertyId}")
    public ResponseEntity<?> getSharedControlByPropertyId(@PathVariable("propertyId") Integer propertyId) {
        try {
            List<UserSearchInfo> usersInfo = this.deviceControlService.getDeviceControlUserInfoForProperty(propertyId   );
            return new ResponseEntity<>(usersInfo, HttpStatus.OK);
        } catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }
}
