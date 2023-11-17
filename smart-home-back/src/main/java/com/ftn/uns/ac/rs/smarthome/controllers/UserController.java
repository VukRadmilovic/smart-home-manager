package com.ftn.uns.ac.rs.smarthome.controllers;

import com.ftn.uns.ac.rs.smarthome.models.User;
import com.ftn.uns.ac.rs.smarthome.models.dtos.LoginDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.TokenDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.UserInfoRegister;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IUserService;
import com.ftn.uns.ac.rs.smarthome.utils.ImageCompressor;
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

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

@CrossOrigin("https://localhost:4200")
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
    public ResponseEntity<?> register(@Valid @ModelAttribute UserInfoRegister userInfoRegister) throws IOException {
        Path filepath = Paths.get("src/main/resources/temp", userInfoRegister.getProfilePicture().getOriginalFilename());
        userInfoRegister.getProfilePicture().transferTo(filepath);
        File file = new File(filepath.toString());
        ImageCompressor.compressImage(file,0.1f);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO authenticationRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            TokenDTO generatedToken = userService.login(user);
            return new ResponseEntity<>(generatedToken, HttpStatus.OK);
        }
        catch(UsernameNotFoundException ex) {
            return new ResponseEntity<>(messageSource.getMessage("login.invalid", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }
}
