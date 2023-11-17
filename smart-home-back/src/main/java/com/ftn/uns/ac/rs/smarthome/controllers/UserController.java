package com.ftn.uns.ac.rs.smarthome.controllers;

import com.ftn.uns.ac.rs.smarthome.models.dtos.UserInfoRegister;
import com.ftn.uns.ac.rs.smarthome.utils.ImageCompressor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin("https://localhost:4200")
@RestController
@RequestMapping(value = "/api/user")
@Validated
public class UserController {

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@Valid @ModelAttribute UserInfoRegister userInfoRegister) throws IOException {
        Path filepath = Paths.get("src/main/resources/temp", userInfoRegister.getProfilePicture().getOriginalFilename());
        userInfoRegister.getProfilePicture().transferTo(filepath);
        File file = new File(filepath.toString());
        ImageCompressor.compressImage(file,0.1f);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
