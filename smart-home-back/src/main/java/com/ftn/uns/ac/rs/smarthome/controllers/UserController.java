package com.ftn.uns.ac.rs.smarthome.controllers;

import com.ftn.uns.ac.rs.smarthome.models.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin("https://localhost:4200")
@RestController
@RequestMapping(value = "/api/user")
@Validated
public class UserController {

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@Valid @ModelAttribute UserInfo userInfo) {
        System.out.println(userInfo);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
