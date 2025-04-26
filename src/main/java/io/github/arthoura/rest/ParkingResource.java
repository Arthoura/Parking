package io.github.arthoura.rest;

import io.github.arthoura.VehicleType;
import io.github.arthoura.domain.model.Parking;
import io.github.arthoura.domain.model.User;
import io.github.arthoura.domain.model.Vehicle;
import io.github.arthoura.domain.repository.ParkingRepository;
import io.github.arthoura.domain.repository.UsersRepository;
import io.github.arthoura.domain.repository.VehicleRepository;
import io.github.arthoura.rest.Dto.*;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Duration;
import java.time.LocalDateTime;

@Path("/parking")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ParkingResource {
    private ParkingRepository repository;
    private UsersRepository usersRepository;
    private VehicleRepository vehicleRepository;

    @Inject
    public ParkingResource(ParkingRepository repository, UsersRepository usersRepository, VehicleRepository vehicleRepository) {
        this.repository = repository;
        this.usersRepository = usersRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @POST
    public Response initParking(InitParkingRequest initParkingRequest){
        repository.setParkingSize(initParkingRequest.getSize());
        return Response.status(Response.Status.CREATED).entity(repository.getParkingSize()).build();
    }

    @PUT
    @Path("{userId}")
    @Transactional
    public Response userEntry(@PathParam("userId") Long userId, EntryUserRequest entryUserRequest){

        if(repository.count() >= repository.getParkingSize()){
            return Response.status(Response.Status.CONFLICT).build();
        }

        if(!entryUserRequest.getType_vehicle().equals("car") && !entryUserRequest.getType_vehicle().equals("moto") ){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\": \"Tipo de veículo precisa ser 'car' ou 'moto'\"}").build();
        }

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

    @GET
    public Response listAllVehicles(){
        return Response.status(Response.Status.OK).entity(ResponseListUsers.listVehicles(repository.findAll().list())).build();
    }

    @GET
    @Path("{userId}")
    public Response getValue(@PathParam("userId") Long userId){
        User user = usersRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Parking parking = repository.find("user", user).firstResult();
        String vehicleType = vehicleRepository.findById(parking.getVehicle().getId()).getType_vehicle();
        LocalDateTime entryTime = LocalDateTime.parse(parking.getEntry_time());
        LocalDateTime exitTime = LocalDateTime.now();
        ResponseCalculateValue responseCalculateValue = new ResponseCalculateValue();
        long minutes = Duration.between(entryTime, exitTime).toMinutes();

        if( minutes <= 5){
            responseCalculateValue.setValue(Long.parseLong("0"));
            return Response.status(Response.Status.OK).entity(responseCalculateValue).build();
        }

        if(vehicleType.equals("car")){
             responseCalculateValue.setValue(VehicleType.CAR.calcularValor(minutes));
        }

        else if(vehicleType.equals("moto")){
            responseCalculateValue.setValue(VehicleType.MOTO.calcularValor(minutes));
        }

        return Response.status(Response.Status.OK).entity(responseCalculateValue).build();
    }

    @DELETE
    @Path("{userId}")
    @Transactional
    public Response exitUser(@PathParam("userId") Long userId){
        User user = usersRepository.findById(userId);
        Parking parking = repository.find("user", user).firstResult();
        repository.delete(parking);
        return Response.ok().build();
    }

    @PATCH
    public Response alterSize(AlterSizeRequest alterSizeRequest){
        if(alterSizeRequest.getNewSize() < 0){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        repository.setParkingSize(alterSizeRequest.getNewSize());
        return Response.ok().build();
    }
}

