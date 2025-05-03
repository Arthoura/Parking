package io.github.arthoura.rest;

import io.github.arthoura.enums.VehicleType;
import io.github.arthoura.domain.model.History;
import io.github.arthoura.domain.model.Parking;
import io.github.arthoura.domain.model.User;
import io.github.arthoura.domain.model.Vehicle;
import io.github.arthoura.domain.repository.HistoryRepository;
import io.github.arthoura.domain.repository.ParkingRepository;
import io.github.arthoura.domain.repository.UsersRepository;
import io.github.arthoura.domain.repository.VehicleRepository;
import io.github.arthoura.rest.Dto.*;
import jakarta.annotation.security.RolesAllowed;
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
    private HistoryRepository historyRepository;

    @Inject
    public ParkingResource(ParkingRepository repository, UsersRepository usersRepository,
                           VehicleRepository vehicleRepository, HistoryRepository historyRepository) {
        this.repository = repository;
        this.usersRepository = usersRepository;
        this.vehicleRepository = vehicleRepository;
        this.historyRepository = historyRepository;
    }

    @POST
    @RolesAllowed({"Admin"})
    public Response initParking(InitParkingRequest initParkingRequest){
        repository.setParkingSize(initParkingRequest.getSize());
        repository.setInitialized(true);
        return Response.status(Response.Status.CREATED).entity(repository.getParkingSize()).build();
    }

    @PUT
    @Path("{userId}")
    @Transactional
    @RolesAllowed({"User"})
    public Response userEntry(@PathParam("userId") Long userId, EntryUserRequest entryUserRequest){
        if(repository.isInitialized()) {
            if (repository.count() > repository.getParkingSize()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"message\": \"Estacionamento cheio. Por favor, aguarde.\"} ")
                        .build();
            }

            if (!VehicleType.isValidType(entryUserRequest.getType_vehicle())) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Tipo de veículo precisa ser um dos seguintes: " + VehicleType.getVehiclesAsList()  + "\"}")
                        .build();
            }

            User user = usersRepository.findById(userId);

            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            if (repository.alreadyExistUser(user)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"message\": \"Usuário já está no estacionamento.\"}")
                        .build();
            }

            Parking parking = new Parking();
            Vehicle vehicleSearch = vehicleRepository.find("plate", entryUserRequest.getPlate()).firstResult();

            if (repository.alreadyExistVehicle(vehicleSearch)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"message\": \"Veículo já está no estacionamento\"}")
                        .build();
            }

            if (vehicleSearch == null) {
                Vehicle vehicle = new Vehicle();
                vehicle.setType_vehicle(entryUserRequest.getType_vehicle());
                vehicle.setPlate(entryUserRequest.getPlate());
                vehicleRepository.persist(vehicle);
                parking.setVehicle(vehicle);
            } else {
                parking.setVehicle(vehicleSearch);
            }

            parking.setUser(user);
            parking.setEntry_time(LocalDateTime.now().toString());
            repository.persist(parking);
            repository.setParkingSize(repository.getParkingSize() - 1);
            return Response.status(Response.Status.OK).entity(parking).build();
        }
        return Response.status(Response.Status.CONFLICT).entity("{\"Message\":\"O nosso estacionamento ainda não foi aberto ;)\"}").build();
    }

    @GET
    @RolesAllowed({"Admin"})
    public Response listAllVehicles(){
        return Response.status(Response.Status.OK).entity(ResponseListUsers.listVehicles(repository.findAll().list())).build();
    }

    @GET
    @Path("{userId}")
    @RolesAllowed({"User", "Admin"})
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

        responseCalculateValue.setValue(VehicleType.valueOf(vehicleType.toUpperCase()).calcularValor(minutes));

        return Response.status(Response.Status.OK).entity(responseCalculateValue).build();
    }

    @GET
    @Path("/history")
    public Response getHistory(){
        return Response.ok().entity(historyRepository.findAll().list()).build();
    }

    @DELETE
    @Path("{userId}")
    @Transactional
    @RolesAllowed({"User"})
    public Response exitUser(@PathParam("userId") Long userId){
        User user = usersRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Usuário não encontrado :/\"} ")
                    .build();
        }
        Parking parking = repository.find("user", user).firstResult();
        if(parking == null){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Desculpe, mas parece que você não estacionou seu veículo aqui :/\"} ")
                    .build();
        }
        repository.delete(parking);
        repository.setParkingSize(repository.getParkingSize()+1);


        //Adiciona os dados ao histórico
        History history = new History();
        history.setVehicle(parking.getVehicle());
        history.setEntry_time(parking.getEntry_time());
        history.setExit_time(LocalDateTime.now().toString());
        history.setUserName(parking.getUser().getName());
        history.setUserCpf(parking.getUser().getCpf());
        history.setUserEmail(parking.getUser().getEmail());
        historyRepository.persist(history);

        return Response.ok().build();

    }

    @PATCH
    @RolesAllowed({"Admin"})
    public Response alterSize(AlterSizeRequest alterSizeRequest){
        if(alterSizeRequest.getNewSize() < 0){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        repository.setParkingSize(alterSizeRequest.getNewSize());
        return Response.ok().build();
    }
}

