package com.ftn.uns.ac.rs.smarthome.models.devices;

import com.ftn.uns.ac.rs.smarthome.models.PowerSource;
import com.ftn.uns.ac.rs.smarthome.models.Property;
import com.ftn.uns.ac.rs.smarthome.models.TemperatureUnit;
import com.ftn.uns.ac.rs.smarthome.models.dtos.devices.AirConditionerDTO;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="AIR_CONDITIONERS")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AirConditioner extends Device {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TemperatureUnit temperatureUnit;

    @Column(nullable = false)
    private Integer maxTemperature;

    @Column(nullable = false)
    private Integer minTemperature;

    @Column(nullable = false)
    private Integer fanSpeed;

    @Column(nullable = false)
    private Boolean cooling;

    @Column(nullable = false)
    private Boolean heating;

    @Column(nullable = false)
    private Boolean dry;

    @Column(nullable = false)
    private Boolean fan;

    @Column(nullable = false)
    private Boolean auto;

    @Column(nullable = false)
    private Boolean health;

    @Column(nullable = false)
    private Boolean fungusPrevention;

    public AirConditioner(AirConditionerDTO dto, Property property) {
        super(-1, property, dto.getName(), null, dto.getPowerSource(), dto.getEnergyConsumption(), false, false);
        this.temperatureUnit = dto.getTemperatureUnit();
        this.maxTemperature = dto.getMaxTemperature();
        this.minTemperature = dto.getMinTemperature();
        this.fanSpeed = dto.getFanSpeed();
        this.cooling = dto.getCooling();
        this.heating = dto.getHeating();
        this.dry = dto.getDry();
        this.fan = dto.getFan();
        this.auto = dto.getAuto();
        this.health = dto.getHealth();
        this.fungusPrevention = dto.getFungusPrevention();
    }
}
