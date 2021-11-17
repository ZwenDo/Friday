package com.kalia.friday.login;

import com.kalia.friday.user.UserCredsDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

import javax.validation.Valid;
import java.net.URI;

import static com.kalia.friday.util.RepositoryResponse.Status.OK;

/**
 * API endpoint for communicating with the login side of the database.
 */
@ExecuteOn(value = TaskExecutors.IO)
@Controller("/auth")
public class LoginController {

    @Inject
    private LoginRepository repository;

    /**
     * Creates a controller by injection with Micronaut.
     *
     * @param sessionManager the session manager to launch
     */
    public LoginController(SessionManagerService sessionManager) {
        sessionManager.start();
    }

    /**
     * Logs a user in and returns a token for the session.
     *
     * @param userCredsDTO {
     *                     "username": "",
     *                     "password": ""
     *                     }
     * @return {
     * "userId": "",
     * "token": ""
     * }
     */
    @Post("/login")
    public HttpResponse<LoginSessionDTO> login(@Body @Valid UserCredsDTO userCredsDTO) {
        var loginResponse = repository.login(userCredsDTO.username(), userCredsDTO.password());
        MutableHttpResponse<LoginSessionDTO> httpResponse = loginResponse.status() == OK
            ? HttpResponse.created(new LoginSessionDTO(
            loginResponse.get().user().id(),
            loginResponse.get().token()
        ))
            : HttpResponse.unauthorized();
        return httpResponse.headers(h -> h.location(URI.create("/auth/login/" + userCredsDTO.username())));
    }

    /**
     * Logs a user out and deletes their token.
     *
     * @param loginSessionDTO {
     *                        "userId": "",
     *                        "token": ""
     *                        }
     * @return OK if successfully logged out | UNAUTHORIZED if the sessions credentials are invalid
     */
    @Post("/logout")
    public HttpResponse<Object> logout(@Body @Valid LoginSessionDTO loginSessionDTO) {
        var httpResponse = checkAndRun(
            loginSessionDTO,
            () -> repository.logout(loginSessionDTO.token())
        );
        return httpResponse.headers(h -> h.location(URI.create("/auth/logout/" + loginSessionDTO.userId())));
    }

    /**
     * Logs a user out and deletes all their tokens.
     *
     * @param loginSessionDTO {
     *                        "userId": "",
     *                        "token": ""
     *                        }
     * @return OK if successfully logged out | UNAUTHORIZED if the sessions credentials are invalid
     */
    @Post("/logout/all")
    public HttpResponse<Object> logoutAll(@Body @Valid LoginSessionDTO loginSessionDTO) {
        var httpResponse = checkAndRun(
            loginSessionDTO,
            () -> repository.logoutAll(loginSessionDTO.userId())
        );
        return httpResponse.headers(h -> h.location(URI.create("/auth/logout/all/" + loginSessionDTO.userId())));
    }

    private MutableHttpResponse<Object> checkAndRun(LoginSessionDTO loginSessionDTO, Runnable request) {
        var checkResponse = repository.checkIdentity(loginSessionDTO.userId(), loginSessionDTO.token());
        if (checkResponse.status() == OK) {
            request.run();
            return HttpResponse.accepted();
        } else {
            return HttpResponse.unauthorized();
        }
    }
}
