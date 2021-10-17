package com.notkamui.user;

import com.notkamui.dto.UserDTO;
import com.notkamui.dto.UserSaveDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import javax.validation.Valid;
import java.net.URI;
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
    public HttpResponse<UserDTO> save(@Body @Valid UserSaveDTO userDTO) {
        Objects.requireNonNull(userDTO);
        var user = repository.save(userDTO.username(), userDTO.password());
        return HttpResponse.created(new UserDTO(user.id(), user.username(), user.password()))
                .headers(h -> h.location(URI.create("/user/" + user.id())));
    }
}
