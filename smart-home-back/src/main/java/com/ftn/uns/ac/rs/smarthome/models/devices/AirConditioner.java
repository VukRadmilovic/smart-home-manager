package com.ftn.uns.ac.rs.smarthome.models.devices;

import com.ftn.uns.ac.rs.smarthome.models.PowerSource;
import com.ftn.uns.ac.rs.smarthome.models.Property;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="AIR_CONDITIONERS")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AirConditioner extends Device {
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

    public AirConditioner(Property property, String name,
                          PowerSource powerSource, Double energyConsumption, Integer maxTemperature,
                          Integer minTemperature, Integer fanSpeed, Boolean cooling, Boolean heating,
                          Boolean dry, Boolean fan, Boolean auto, Boolean health, Boolean fungusPrevention) {
        super(-1, property, name, null, powerSource, energyConsumption, false, false);
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.fanSpeed = fanSpeed;
        this.cooling = cooling;
        this.heating = heating;
        this.dry = dry;
        this.fan = fan;
        this.auto = auto;
        this.health = health;
        this.fungusPrevention = fungusPrevention;
    }
}
