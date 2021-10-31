package com.kalia.friday.login;

import com.kalia.friday.dto.LoginResponseDTO;
import com.kalia.friday.dto.UserCredsDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import javax.validation.Valid;
import java.net.URI;

import static com.kalia.friday.util.RepositoryResponse.Status.OK;
import static java.util.Objects.requireNonNull;

/**
 * API endpoint for communicating with the login side of the database.
 */
@ExecuteOn(value = TaskExecutors.IO)
@Controller("/auth")
public class LoginController {

    private final LoginRepository repository;

    /**
     * Creates a controller by injection with Micronaut.
     *
     * @param repository the login repository which serves to manipulate the database
     */
    public LoginController(LoginRepository repository) {
        this.repository = requireNonNull(repository);
    }

    /**
     * Logs a user in and returns a token for the session.
     *
     * @param userCredsDTO {
     *                     "username": "",
     *                     "password": ""
     *                     }
     * @return {
     *     "token": "",
     *     "userId": ""
     * }
     */
    @Post("/login")
    public HttpResponse<LoginResponseDTO> login(@Body @Valid UserCredsDTO userCredsDTO) {
        requireNonNull(userCredsDTO);
        var loginResponse = repository.login(userCredsDTO.username(), userCredsDTO.password());
        MutableHttpResponse<LoginResponseDTO> httpResponse = loginResponse.status() == OK
            ? HttpResponse.created(new LoginResponseDTO(
                loginResponse.get().token(),
                loginResponse.get().user().id()
            ))
            : HttpResponse.unauthorized();
        return httpResponse.headers(h -> h.location(URI.create("/auth/login/" + userCredsDTO.username())));
    }
}
