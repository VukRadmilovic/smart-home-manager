package com.ftn.uns.ac.rs.smarthome.controllers;

import com.ftn.uns.ac.rs.smarthome.models.dtos.PropertyDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.UserInfoRegister;
import com.ftn.uns.ac.rs.smarthome.repositories.UserRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IPropertyService;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IUserService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Locale;

@CrossOrigin("http://localhost:5173/")
@RestController
@RequestMapping(value = "/api/property")
@Validated
public class PropertyController {
    private final AuthenticationManager authenticationManager;
    private final IUserService userService;
    private final MessageSource messageSource;
    private final IPropertyService propertyService;

    public PropertyController(AuthenticationManager authenticationManager,
                              IUserService userService,
                              MessageSource messageSource,
                              IPropertyService propertyService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.messageSource = messageSource;
        this.propertyService = propertyService;
    }

    @PostMapping(value = "/registerProperty", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@Valid @ModelAttribute PropertyDTO propertyDTO) {
        try{
            this.propertyService.registerProperty(propertyDTO);
            return new ResponseEntity<>(messageSource.getMessage("registration.success", null, Locale.getDefault()), HttpStatus.OK);
        }
        catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }
}