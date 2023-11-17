package com.ftn.uns.ac.rs.smarthome.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

    private String username;
    private String email;
    private String profilePicture;
    private String role;
}
