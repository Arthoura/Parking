package io.github.arthoura.domain.model;

import jakarta.persistence.*;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;

@Entity
@Table(name = "VEHICLE")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_vehicle")
    private String type_vehicle;

    @Column(name = "plate")
    private String plate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType_vehicle() {
        return type_vehicle;
    }

    public void setType_vehicle(String type_vehicle) {
        this.type_vehicle = type_vehicle;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }
}
