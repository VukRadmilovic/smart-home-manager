package com.ftn.uns.ac.rs.smarthomesockets.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Measurement {
    private Long date;
    private double value;
}
