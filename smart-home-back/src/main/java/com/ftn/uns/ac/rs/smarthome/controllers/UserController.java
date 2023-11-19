package com.ftn.uns.ac.rs.smarthome.controllers;

import com.ftn.uns.ac.rs.smarthome.models.User;
import com.ftn.uns.ac.rs.smarthome.models.dtos.LoginDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.TokenDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.UserInfoDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.UserInfoRegister;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IUserService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import java.util.Locale;

@CrossOrigin("http://localhost:5173/")
@RestController
@RequestMapping(value = "/api/user")
@Validated
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final IUserService userService;
    private final MessageSource messageSource;

    public UserController(AuthenticationManager authenticationManager,
                          IUserService userService,
                          MessageSource messageSource) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@Valid @ModelAttribute UserInfoRegister userInfoRegister) {
        try{
            this.userService.register(userInfoRegister);
            return new ResponseEntity<>(messageSource.getMessage("registration.success", null, Locale.getDefault()), HttpStatus.OK);
        }
        catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }

    @PostMapping(value = "/registerAdmin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerAdmin(@Valid @ModelAttribute UserInfoRegister userInfoRegister) {
        try{
            this.userService.register(userInfoRegister);
            return new ResponseEntity<>(messageSource.getMessage("registrationAdmin.success", null, Locale.getDefault()), HttpStatus.OK);
        }
        catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }

    @GetMapping(value = "/activate/{userId}")
    public ResponseEntity<?> activateAccount(@PathVariable Integer userId) {
        try{
            this.userService.activate(userId);
            return new ResponseEntity<>(messageSource.getMessage("activation.success", null, Locale.getDefault()), HttpStatus.USE_PROXY);
        }
        catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }

    @GetMapping(value = "/info")
    public ResponseEntity<?> getUserInfo() {
        try{
            User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserInfoDTO userInfo = this.userService.getUserInfo(user.getId());
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        }
        catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO authenticationRequest) {
        try {
            TokenDTO generatedToken = userService.login(authenticationRequest);
            return new ResponseEntity<>(generatedToken, HttpStatus.OK);
        }
        catch(UsernameNotFoundException ex) {
            return new ResponseEntity<>(messageSource.getMessage("login.invalid", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
        catch(ResponseStatusException ex) {
            return new ResponseEntity<>(ex.getReason(), ex.getStatus());
        }
    }
}
