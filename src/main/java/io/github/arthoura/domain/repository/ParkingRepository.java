package io.github.arthoura.domain.repository;

import io.github.arthoura.domain.model.Parking;
import io.github.arthoura.domain.model.User;
import io.github.arthoura.domain.model.Vehicle;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ParkingRepository implements PanacheRepository<Parking> {
    private Integer parkingSize = 0;

    public Boolean alreadyExistUser(User user){
        if(find("user", user).firstResult() == null){
            return false;
        }
        return true;
    }

    public Boolean alreadyExistVehicle(Vehicle vehicle){
        if(find("vehicle", vehicle).firstResult() == null){
            return false;
        }
        return true;
    }

    public Integer getParkingSize() {
        return parkingSize;
    }

    public void setParkingSize(Integer parkingSize) {
        this.parkingSize = parkingSize;
    }
}
