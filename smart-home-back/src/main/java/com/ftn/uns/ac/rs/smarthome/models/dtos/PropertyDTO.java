package com.ftn.uns.ac.rs.smarthome.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class PropertyDTO {
    private String address;
    private String city;
    private String size;
    private String floors;
    private String status;
    private String propertyType;
    private MultipartFile picture;
    private String Username;
}