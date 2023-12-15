package com.ftn.uns.ac.rs.smarthomesimulator.models.devices;

import com.ftn.uns.ac.rs.smarthomesimulator.models.PowerSource;
import com.ftn.uns.ac.rs.smarthomesimulator.models.Property;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="BATTERIES")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Battery extends Device {
    @Column(nullable = false)
    private Double capacity;

    public Battery(Property property, String name,
                   PowerSource powerSource, Double energyConsumption, Double capacity) {
        super(-1, property, name, null, powerSource, energyConsumption, false, false);
        this.capacity = capacity;
    }
}
