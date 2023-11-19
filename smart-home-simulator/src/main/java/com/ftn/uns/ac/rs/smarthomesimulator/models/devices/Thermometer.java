package com.ftn.uns.ac.rs.smarthomesimulator.models.devices;

import com.ftn.uns.ac.rs.smarthomesimulator.models.PowerSource;
import com.ftn.uns.ac.rs.smarthomesimulator.models.Property;
import com.ftn.uns.ac.rs.smarthomesimulator.models.TemperatureUnit;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="THERMOMETERS")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Thermometer extends Device {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TemperatureUnit temperatureUnit;

    public Thermometer(Property property, String name, String description,
                       PowerSource powerSource, Double energyConsumption, TemperatureUnit temperatureUnit) {
        super(-1, property, name, description, null, powerSource, energyConsumption);
        this.temperatureUnit = temperatureUnit;
    }
}
