package com.ftn.uns.ac.rs.smarthome.models;

import com.ftn.uns.ac.rs.smarthome.models.devices.Device;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name="PROPERTIES")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Property {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @OneToMany(mappedBy = "property", fetch = FetchType.EAGER)
    private List<Device> devices;
}
