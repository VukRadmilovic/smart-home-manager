package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.models.DeviceControl;
import com.ftn.uns.ac.rs.smarthome.models.Property;
import com.ftn.uns.ac.rs.smarthome.models.User;
import com.ftn.uns.ac.rs.smarthome.models.UserSearchInfo;
import com.ftn.uns.ac.rs.smarthome.models.devices.Device;
import com.ftn.uns.ac.rs.smarthome.models.dtos.DeviceControlDetails;
import com.ftn.uns.ac.rs.smarthome.repositories.DeviceControlRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IDeviceControlService;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IDeviceService;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IPropertyService;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IUserService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.*;

@Service
public class DeviceControlService implements IDeviceControlService {

    private final DeviceControlRepository deviceControlRepository;
    private final IDeviceService deviceService;
    private final MessageSource messageSource;
    private final IPropertyService propertyService;
    private final IUserService userService;

    public DeviceControlService(DeviceControlRepository deviceControlRepository,
                                IDeviceService deviceService,
                                MessageSource messageSource,
                                IPropertyService propertyService,
                                IUserService userService) {
        this.deviceControlRepository = deviceControlRepository;
        this.deviceService = deviceService;
        this.messageSource = messageSource;
        this.propertyService = propertyService;
        this.userService = userService;
    }

    @Override
    public void editDeviceControl(Integer deviceId, List<DeviceControlDetails> details) {
        Optional<Device> device = deviceService.getById(deviceId);
        if(device.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("device.notFound", null, Locale.getDefault()));
        }
        for(DeviceControlDetails detail : details) {
            Optional<User> user = userService.getById(detail.getUserId());
            Optional<DeviceControl>dc = deviceControlRepository.findByDevice_IdAndOwner_Id(deviceId, detail.getUserId());
            if(user.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("user.notFound", null, Locale.getDefault()));
            }
            if(detail.getAction().equals("a")) {
                if(dc.isEmpty())
                    deviceControlRepository.save(new DeviceControl(user.get(), device.get()));
            }
            if(detail.getAction().equals("d")) {
                dc.ifPresent(deviceControlRepository::delete);
            }
        }
    }

    @Override
    public void editDeviceControlForProperty(Integer propertyId, List<DeviceControlDetails> details) {
        Optional<Property> property = propertyService.getById(propertyId);
        if(property.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("property.notFound", null, Locale.getDefault()));
        }

        for (DeviceControlDetails detail : details) {
            Optional<User> user = userService.getById(detail.getUserId());
            if (user.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("user.notFound", null, Locale.getDefault()));
            }
            if (detail.getAction().equals("a")) {
                for(Device device : property.get().getDevices()) {
                    if(deviceControlRepository.findByDevice_IdAndOwner_Id(device.getId(), user.get().getId()).isEmpty())
                        deviceControlRepository.save(new DeviceControl(user.get(), device));
                }
            }
            if (detail.getAction().equals("d")) {
                for(Device device : property.get().getDevices()) {
                    Optional<DeviceControl> dc = deviceControlRepository.findByDevice_IdAndOwner_Id(device.getId(), detail.getUserId());
                    dc.ifPresent(deviceControlRepository::delete);
                }
            }
        }
    }

    @Override
    public List<UserSearchInfo> getDeviceControlUserInfo(Integer deviceId) {
        List<UserSearchInfo> list = new ArrayList<>();
        List<DeviceControl> controls = deviceControlRepository.findByDevice_Id(deviceId);
        for(DeviceControl control : controls) {
            list.add(new UserSearchInfo(control.getOwner().getId(), control.getOwner().getUsername(),
                    control.getOwner().getName() + " " + control.getOwner().getSurname()));
        }
        return list;
    }

    @Override
    public List<UserSearchInfo> getDeviceControlUserInfoForProperty(Integer propertyId) {
        List<UserSearchInfo> list = new ArrayList<>();
        Optional<Property> property = propertyService.getById(propertyId);
        if(property.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("property.notFound", null, Locale.getDefault()));
        }
        Map<Integer, Integer> userDeviceControlCount = new HashMap<>();
        Map<Integer, UserSearchInfo> userInfo = new HashMap<>();
        for(DeviceControl control : deviceControlRepository.findByDevice_Property_Id(propertyId)) {
            Integer userId = control.getOwner().getId();
            userInfo.putIfAbsent(userId, new UserSearchInfo(userId, control.getOwner().getUsername(), control.getOwner().getName() + " " + control.getOwner().getSurname()));
            if(userDeviceControlCount.get(userId) == null) {
                userDeviceControlCount.put(userId, 1);
            }
            else {
                userDeviceControlCount.put(userId, userDeviceControlCount.get(userId) + 1);
            }
        }
        for(Map.Entry<Integer, Integer> entry : userDeviceControlCount.entrySet()) {
            if(entry.getValue() == property.get().getDevices().size())
                list.add(userInfo.get(entry.getKey()));
        }
        System.out.println(list);
        return list;
    }
}
