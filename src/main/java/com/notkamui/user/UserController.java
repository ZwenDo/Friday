package com.notkamui.user;

import com.notkamui.dto.UserDeleteDTO;
import com.notkamui.dto.UserResponseDTO;
import com.notkamui.dto.UserSaveDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import javax.validation.Valid;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ExecuteOn(value = TaskExecutors.IO)
@Controller("/user")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        requireNonNull(repository);
        this.repository = repository;
    }

    @Post
    public HttpResponse<UserResponseDTO> save(@Body @Valid UserSaveDTO userSaveDTO) throws NoSuchAlgorithmException {
        requireNonNull(userSaveDTO);
        var user = repository.save(userSaveDTO.username(), userSaveDTO.password());
        return HttpResponse
            .created(new UserResponseDTO(user.id(), user.username()))
            .headers(h -> h.location(URI.create("/user/" + user.id())));
    }

    @Delete("/{id}")
    public HttpResponse<Boolean> delete(UUID id, @Body @Valid UserDeleteDTO userDeleteDTO) {
        requireNonNull(id);
        requireNonNull(userDeleteDTO);
        return repository.deleteById(id, userDeleteDTO.password())
            ? HttpResponse.ok(true)
            : HttpResponse.badRequest(false);
    }
}
