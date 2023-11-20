package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.models.Property;
import com.ftn.uns.ac.rs.smarthome.models.Town;
import com.ftn.uns.ac.rs.smarthome.models.dtos.PropertyDTO;
import com.ftn.uns.ac.rs.smarthome.repositories.PropertyRepository;
import com.ftn.uns.ac.rs.smarthome.repositories.TownRepository;
import com.ftn.uns.ac.rs.smarthome.repositories.UserRepository;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IPropertyService;
import com.ftn.uns.ac.rs.smarthome.services.interfaces.IUserService;
import com.ftn.uns.ac.rs.smarthome.utils.ImageCompressor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import com.ftn.uns.ac.rs.smarthome.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;

@Service
public class PropertyService implements IPropertyService {
    private final MessageSource messageSource;
    private IUserService userService;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final TownRepository townRepository;
    public  PropertyService(MessageSource messageSource,
                            PropertyRepository propertyRepository,
                            IUserService userService,
                            UserRepository userRepository,
                            TownRepository townRepository){
        this.messageSource = messageSource;
        this.propertyRepository = propertyRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.townRepository = townRepository;
    }
    public void registerProperty(PropertyDTO propertyDTO){
        try {
            if (this.propertyRepository.findByAddress(propertyDTO.getAddress()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("address.alreadyUsed", null, Locale.getDefault()));
            }
            Optional<User> owner = this.userRepository.findByUsername(propertyDTO.getUsername());
            if(owner.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("user.notExisting", null, Locale.getDefault()));
            }
            Optional<Town> town = townRepository.findByName(propertyDTO.getCity());
            if(town.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("town.notExisting", null, Locale.getDefault()));
            }
            Path filepath = Paths.get("../temp/", propertyDTO.getPicture().getOriginalFilename());
            propertyDTO.getPicture().transferTo(filepath);
            File file = new File(filepath.toString());
            File compressed = ImageCompressor.compressImage(file, 0.1f, propertyDTO.getUsername());
            String[] tokens = compressed.getName().split("/");
            String key = tokens[tokens.length - 1];
            String type = propertyDTO.getPicture().getContentType();
            String bucket = "images";
            String pathToImage = "http://127.0.0.1:9000/" + bucket + '/' + "profilePictures/" + key;

            Property propertyToSave = new Property(propertyDTO.getAddress(),
                    propertyDTO.getSize(),
                    pathToImage,
                    owner.get(),
                    propertyDTO.getFloors(),
                    propertyDTO.getPropertyType());
            this.propertyRepository.save(propertyToSave);
        }catch(ResponseStatusException ex) {
            throw ex;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}