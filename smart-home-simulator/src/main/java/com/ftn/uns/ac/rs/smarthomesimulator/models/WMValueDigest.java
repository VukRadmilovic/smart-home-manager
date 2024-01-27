package com.ftn.uns.ac.rs.smarthomesimulator.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WMValueDigest {
    private Integer deviceId;
    private Integer centrifugeSpeed;
    private Integer temperature;
    private String unit;
    private String mode;
}
