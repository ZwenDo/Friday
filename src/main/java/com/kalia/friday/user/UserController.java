package com.kalia.friday.user;

import com.kalia.friday.dto.UserDeleteDTO;
import com.kalia.friday.dto.UserPasswordUpdateDTO;
import com.kalia.friday.dto.UserResponseDTO;
import com.kalia.friday.dto.UserSaveDTO;
import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * API endpoint for communicating with the user side of the database.
 */
@ExecuteOn(value = TaskExecutors.IO)
@Controller("/user")
public class UserController {

    private final UserRepository repository;

    /**
     * Creates a controller by injection with Micronaut.
     *
     * @param repository the user repository which serves to manipulate the database
     */
    public UserController(UserRepository repository) {
        this.repository = requireNonNull(repository);
    }

    /**
     * Creates and saves a user, provided a correct body.
     *
     * @param userSaveDTO {
     *                    "username": "",
     *                    "password": ""
     *                    }
     * @return {
     * "id": "",
     * "username": ""
     * }
     */
    @Post
    public HttpResponse<UserResponseDTO> save(@Body @Valid UserSaveDTO userSaveDTO) {
        requireNonNull(userSaveDTO);
        var saveResponse = repository.save(userSaveDTO.username(), userSaveDTO.password());
        var user = saveResponse.get();
        return HttpResponse
            .created(new UserResponseDTO(user.id(), user.username()))
            .headers(h -> h.location(URI.create("/user/" + user.id())));
    }

    /**
     * Deletes a user by its id, provided a correct body
     * which holds the valid password of the user.
     *
     * @param id            the id of the user to delete
     * @param userDeleteDTO {
     *                      "password": ""
     *                      }
     * @return OK if deleted | NOT_FOUND if the id is unknown | BAD_REQUEST if the password is incorrect
     */
    @Delete("/delete/{id}")
    public HttpResponse<Object> delete(UUID id, @Body @Valid UserDeleteDTO userDeleteDTO) {
        requireNonNull(id);
        requireNonNull(userDeleteDTO);
        var deleteUserResponse = repository.deleteById(id, userDeleteDTO.password());
        var httpResponse = toEmptyHttpResponse(deleteUserResponse.status());
        return httpResponse.headers(h -> h.location(URI.create("/user/delete/" + id)));
    }

    /**
     * Updates a user's password by its id, provided a correct body
     * which holds the valid old password of the user.
     *
     * @param id     the id of the user to update
     * @param upuDTO {
     *               "oldPassword": "",
     *               "newPassword": ""
     *               }
     * @return OK if updated | NOT_FOUND if the id is unknown | BAD_REQUEST if the old password is incorrect
     */
    @Put("/update/{id}")
    public HttpResponse<Object> updatePassword(UUID id, @Body @Valid UserPasswordUpdateDTO upuDTO) {
        requireNonNull(id);
        requireNonNull(upuDTO);
        var updatePwdResponse = repository.updatePassword(id, upuDTO.oldPassword(), upuDTO.newPassword());
        var httpResponse = toEmptyHttpResponse(updatePwdResponse.status());
        return httpResponse.headers(h -> h.location(URI.create("/user/update/" + id)));
    }

    private static MutableHttpResponse<Object> toEmptyHttpResponse(RepositoryResponse.Status repositoryResponseStatus) {
        return switch (repositoryResponseStatus) {
            case OK -> HttpResponse.noContent();
            case NOT_FOUND -> HttpResponse.notFound();
            case UNAUTHORIZED -> HttpResponse.badRequest();
        };
    }
}
