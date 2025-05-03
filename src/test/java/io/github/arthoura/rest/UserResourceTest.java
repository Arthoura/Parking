package io.github.arthoura.rest;

import io.github.arthoura.domain.model.Parking;
import io.github.arthoura.domain.model.User;
import io.github.arthoura.domain.model.Vehicle;
import io.github.arthoura.domain.repository.ParkingRepository;
import io.github.arthoura.domain.repository.UsersRepository;
import io.github.arthoura.domain.repository.VehicleRepository;
import io.github.arthoura.rest.Dto.CreateUserRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
class UserResourceTest {

    private String tokenTest;
    private Long userId;
    private Long userToDeleteId;


    @Inject
    UsersRepository repository;

    @Inject
    ParkingRepository parkingRepository;

    @Inject
    VehicleRepository vehicleRepository;

    @BeforeEach
    @Transactional
    public void setUp(){
        Parking parking = new Parking();
        Vehicle vehicle = new Vehicle();
        User user = new User();
        User userToDelete = new User();

        vehicle.setPlate("as");
        vehicle.setType_vehicle("car");
        vehicleRepository.persist(vehicle);


        user.setEmail("ama@ecomp.poli.br");
        user.setName("Arthur Moura");
        user.setCpf("12345678910");
        user.setBirthday("1999-03-25");
        repository.persist(user);

        userToDelete.setName("Fernando");
        userToDelete.setCpf("123123123123");
        userToDelete.setBirthday("1997-01-06");
        userToDelete.setEmail("fran@ecomp.poli");
        repository.persist(userToDelete);

        userToDeleteId = userToDelete.getId();

        parking.setUser(user);
        parking.setVehicle(vehicle);
        parking.setEntry_time("random");
        parkingRepository.persist(parking);

        userId = user.getId();

        Set<String> roles = new HashSet<>();
        roles.add("Admin");
        roles.add("User");

        tokenTest = Jwt.issuer("https://seu-dominio.com/issuer")
                .upn("testuser@email.com")
                .groups(roles)
                .sign();

    }

    //Tests for POST - CreateUser
    @Test
    @DisplayName("Should create an user succesfully")
    public void creatUserTest(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setBirthday("1999-03-25");
        createUserRequest.setCpf("70202505480");
        createUserRequest.setName("Arthur Moura");
        createUserRequest.setEmail("arthur@ecomp.poli.br");

        Response response =
                given()
                    .contentType(ContentType.JSON)
                    .body(createUserRequest)
                .when()
                    .post()
                .then()
                    .extract()
                    .response();

        assertEquals(201, response.getStatusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }
    @Test
    @DisplayName("Should return error when cpf is already used")
    public void createUserCPFAlreadyUsedTest(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setBirthday("1999-03-25");
        createUserRequest.setCpf("12345678910");
        createUserRequest.setName("Arthur Moura");
        createUserRequest.setEmail("arthur@ecomp.poli.br");

        Response response =
                given()
                    .contentType(ContentType.JSON)
                    .body(createUserRequest)
                .when()
                    .post()
                .then()
                    .extract()
                    .response();

        assertEquals(409, response.getStatusCode());
    }
    @Test
    @DisplayName("Should return error when Email is already used")
    public void createUserEMAILlreadyUsedTest(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setBirthday("1999-03-25");
        createUserRequest.setCpf("98765432100");
        createUserRequest.setName("Arthur Moura");
        createUserRequest.setEmail("ama@ecomp.poli.br");

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(createUserRequest)
                        .when()
                        .post()
                        .then()
                        .extract()
                        .response();

        assertEquals(409, response.getStatusCode());
    }

    //Tests for GET - listAllUsers
    @Test
    @DisplayName("Should return all users")
    public void listUsersTest(){
                given()
                        .header("Authorization", "Bearer " + tokenTest)
                    .contentType(ContentType.JSON)
                .when()
                    .get()
                .then()
                    .statusCode(200);

    }


    //Tests for GET - listUser
    @Test
    @DisplayName("Should return an User specified by UserID")
    public void listUserTest(){
        given()
                .header("Authorization", "Bearer " + tokenTest)
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get("{userId}")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Should return error because User was not found")
    public void listUserNotFoundTest(){
        given()
                .header("Authorization", "Bearer " + tokenTest)
                .contentType(ContentType.JSON)
                .pathParam("userId", 472819247)
                .when()
                .get("{userId}")
                .then()
                .statusCode(404);
    }


    //Tests for DELETE - deleteUser
    @Test
    @DisplayName("Should delete an user succesfully")
    public void deleteUserSuccesTest() {
        Response response = given()
                .pathParam("userId", userToDeleteId)
                .header("Authorization", "Bearer " + tokenTest)
                .when()
                .delete("{userId}")
                .then()
                .extract().response();

        assertEquals(204, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return error because user is inside parking")
    public void deleteUserInParkingTest() {
        parkingRepository.findAll().firstResult();
        Response response = given()
                .pathParam("userId", userId)
                .header("Authorization", "Bearer " + tokenTest)
                .when()
                .delete("{userId}")
                .then()
                .extract().response();

        assertEquals(409, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return error because user was not found")
    public void deleteInexistentUserTest() {
        Response response = given()
                .pathParam("userId", 999L)
                .header("Authorization", "Bearer " + tokenTest)
                .when()
                .delete("{userId}")
                .then()
                .extract().response();

        assertEquals(404, response.getStatusCode());
    }


    //Tests for PATCH - updateUser
    @Test
    @DisplayName("Should update an user succesfully")
    public void updateUserTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setBirthday("1999-03-25");
        createUserRequest.setCpf("70202505480");
        createUserRequest.setName("Arthur Moura");
        createUserRequest.setEmail("arthur@ecomp.poli.br");

        Response response = given()
                .pathParam("userId", userId)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + tokenTest)
                .body(createUserRequest)
                .when()
                .patch("{userId}")
                .then()
                .extract().response();

        assertEquals(204, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return error because user was not found")
    public void testUpdateUser_NotFound() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setBirthday("1999-03-25");
        createUserRequest.setCpf("70202505480");
        createUserRequest.setName("Arthur Moura");
        createUserRequest.setEmail("arthur@ecomp.poli.br");

        Response response = given()
                .pathParam("userId", 999L) // ID inexistente
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + tokenTest)
                .body(createUserRequest)
                .when()
                .patch("{userId}")
                .then()
                .extract().response();

        assertEquals(404, response.getStatusCode());
    }



}
