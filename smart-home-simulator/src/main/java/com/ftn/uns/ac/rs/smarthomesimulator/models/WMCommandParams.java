package com.ftn.uns.ac.rs.smarthomesimulator.models;

import com.ftn.uns.ac.rs.smarthomesimulator.models.enums.ACMode;
import com.ftn.uns.ac.rs.smarthomesimulator.models.enums.CommandType;
import com.ftn.uns.ac.rs.smarthomesimulator.models.enums.WMMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WMCommandParams {
    private Integer userId;
    private String unit;
    private Integer centrifugeSpeed;
    private Integer temp;
    private WMMode mode;
    private Long from;
    private Long taskId;
}
