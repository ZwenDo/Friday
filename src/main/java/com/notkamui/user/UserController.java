package com.notkamui.user;

import com.notkamui.dto.UserResponseDTO;
import com.notkamui.dto.UserSaveDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import javax.validation.Valid;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@ExecuteOn(value = TaskExecutors.IO)
@Controller("/user")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        Objects.requireNonNull(repository);
        this.repository = repository;
    }

    @Post
    public HttpResponse<UserResponseDTO> save(@Body @Valid UserSaveDTO userDTO) throws NoSuchAlgorithmException {
        Objects.requireNonNull(userDTO);
        var user = repository.save(userDTO.username(), userDTO.password());
        return HttpResponse.created(new UserResponseDTO(user.id(), user.username()))
                .headers(h -> h.location(URI.create("/user/" + user.id())));
    }
}
