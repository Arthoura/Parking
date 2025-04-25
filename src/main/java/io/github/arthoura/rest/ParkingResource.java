package io.github.arthoura.rest;

import io.github.arthoura.domain.model.Parking;
import io.github.arthoura.domain.model.User;
import io.github.arthoura.domain.model.Vehicle;
import io.github.arthoura.domain.repository.ParkingRepository;
import io.github.arthoura.domain.repository.UsersRepository;
import io.github.arthoura.domain.repository.VehicleRepository;
import io.github.arthoura.rest.Dto.EntryUserRequest;
import io.github.arthoura.rest.Dto.InitParkingRequest;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.xml.stream.events.EntityReference;
import java.time.LocalDateTime;
import java.util.Optional;

@Path("/parking")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ParkingResource {
    private ParkingRepository repository;
    private UsersRepository usersRepository;
    private VehicleRepository vehicleRepository;
    private Integer parkingSize = 0;

    @Inject
    public ParkingResource(ParkingRepository repository, UsersRepository usersRepository, VehicleRepository vehicleRepository) {
        this.repository = repository;
        this.usersRepository = usersRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @POST
    public Response initParking(InitParkingRequest initParkingRequest){
        parkingSize = initParkingRequest.getSize();
        return Response.status(Response.Status.CREATED).entity(parkingSize).build();
    }

    @PUT
    @Path("{userId}")
    @Transactional
    public Response userEntry(@PathParam("userId") Long userId, EntryUserRequest entryUserRequest){
        User user = usersRepository.findById(userId);

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(repository.alreadyExistUser(user)){
            return Response.status(Response.Status.CONFLICT).build();
        }

        Parking parking = new Parking();
        Vehicle vehicleSearch = vehicleRepository.find("plate", entryUserRequest.getPlate()).firstResult();

        if(repository.alreadyExistVehicle(vehicleSearch)){
            return Response.status(Response.Status.CONFLICT).build();
        }

        if(vehicleSearch == null){
            Vehicle vehicle = new Vehicle();
            vehicle.setType_vehicle(entryUserRequest.getType_vehicle());
            vehicle.setPlate(entryUserRequest.getPlate());
            vehicleRepository.persist(vehicle);
            parking.setVehicle(vehicle);
        }

        else{
            parking.setVehicle(vehicleSearch);
        }

        parking.setUser(user);
        parking.setEntry_time(LocalDateTime.now().toString());
        repository.persist(parking);
        return Response.status(Response.Status.OK).entity(parking).build();

    }
}
