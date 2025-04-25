package io.github.arthoura.domain.repository;

import io.github.arthoura.domain.model.Vehicle;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VehicleRepository implements PanacheRepository<Vehicle> {

}
