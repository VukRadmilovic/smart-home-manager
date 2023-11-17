package com.ftn.uns.ac.rs.smarthome.models.dtos;

import lombok.Data;

@Data
public class TokenDTO {
    String accessToken;
    public TokenDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}
