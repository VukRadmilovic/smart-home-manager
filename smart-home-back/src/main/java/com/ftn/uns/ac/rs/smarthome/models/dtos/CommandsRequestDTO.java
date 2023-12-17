package com.ftn.uns.ac.rs.smarthome.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandsRequestDTO {
    private Long from;
    private Long to;
    private Integer deviceId;
    private Integer page;
    private Integer size;
    private Integer userId;
}
