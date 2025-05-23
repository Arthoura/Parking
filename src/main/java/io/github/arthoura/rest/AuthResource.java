package io.github.arthoura.rest;

import io.github.arthoura.domain.model.User;
import io.github.arthoura.domain.repository.UsersRepository;
import io.github.arthoura.rest.Dto.LoginRequest;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashSet;
import java.util.Set;

@Path("/auth")
@PermitAll
public class AuthResource {
    private UsersRepository usersRepository;

    @Inject
    public AuthResource(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(LoginRequest loginRequest) {

        if(loginRequest.getEmail().equals("fadparking@gmail.com") && loginRequest.getPassword().equals("admin123")){
            // Define os papéis do usuário
            Set<String> roles = new HashSet<>();
            roles.add("Admin");

            // Cria o token JWT
            String token = Jwt.issuer("https://seu-dominio.com/issuer")
                    .upn(loginRequest.getEmail())
                    .groups(roles)
                    .sign();
            return Response.ok(token).build();
        }

        User user = usersRepository.find("email", loginRequest.getEmail()).firstResult();
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"Messagem\":\"Usuário não encontrado\"}").build();
        }
        // Consulta o UsersRepository e checa o email e o cpf (6 primeiros dígitos) como password.
        if (loginRequest.getEmail().equals(user.getEmail()) && loginRequest.getPassword().equals(user.getCpf().substring(0,6))) {
            // Define os papéis do usuário
            Set<String> roles = new HashSet<>();
            roles.add("User");

            // Cria o token JWT
            String token = Jwt.issuer("https://seu-dominio.com/issuer")
                    .upn(loginRequest.getEmail())
                    .groups(roles)
                    .sign();
            return Response.ok(token).build();
        }

        // Caso as credenciais estejam incorretas, retorna 401 Unauthorized.
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
