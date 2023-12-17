package com.ftn.uns.ac.rs.smarthomesimulator.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scheduled {
    private Long id;
    private Long from;
    private Long to;
    private boolean everyDay;
}
