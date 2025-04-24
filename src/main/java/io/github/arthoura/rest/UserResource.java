package io.github.arthoura.rest;

import io.github.arthoura.domain.model.User;
import io.github.arthoura.domain.repository.UsersRepository;
import io.github.arthoura.rest.Dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private UsersRepository repository;

    @Inject
    public UserResource(UsersRepository repository) {
        this.repository = repository;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest createUserRequest){
        User user = new User();
        user.setName(createUserRequest.getName());
        user.setBirthday(createUserRequest.getBirthday());
        user.setCpf(createUserRequest.getCpf());
        repository.persist(user);
    return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    public Response listAllUsers(){
        PanacheQuery<User> allUsers = repository.findAll();
        return Response.status(Response.Status.OK).entity(allUsers.list()).build();
    }

    @GET
    @Path("{userId}")
    public Response listUser(@PathParam("userId") Long id){
        User userById = repository.findById(id);
        if(userById != null) {
            return Response.status(Response.Status.OK).entity(userById).build();
        }
            return Response.status(Response.Status.NOT_FOUND).build();
    }


    @DELETE
    @Transactional
    @Path("{userId}")
    public Response deleteUser(@PathParam("userId") Long id){
        User userById = repository.findById(id);
        if(userById != null){
            repository.delete(userById);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PATCH
    @Path("{userId}")
    @Transactional
    public Response updateUser(@PathParam("userId") Long id, CreateUserRequest createUserRequest){
        User userById = repository.findById(id);
        if(userById != null){
            if(createUserRequest.getBirthday() != null){
                userById.setBirthday(createUserRequest.getBirthday());
            }
            if(createUserRequest.getCpf() != null){
                userById.setCpf(createUserRequest.getCpf());
            }
            if(createUserRequest.getName() != null){
                userById.setName(createUserRequest.getName());
            }
            repository.persist(userById);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
