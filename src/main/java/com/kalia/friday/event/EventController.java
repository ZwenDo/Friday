package com.kalia.friday.event;

import com.kalia.friday.login.LoginSessionDTO;
import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

/**
 * API endpoint for communicating with the event side of the database.
 */
@ExecuteOn(value = TaskExecutors.IO)
@Controller("/api/event")
public class EventController {

    @Inject
    private EventRepository eventRepository;

    /**
     * Creates and saves an event, provided a correct body.
     *
     * @param eventDTO {
     *                 "userId": "",
     *                 "userToken": "",
     *                 "title": "",
     *                 "description": "",
     *                 "place": "",
     *                 "recurRule": ""
     *                 }
     * @return {
     * "id": "",
     * "title": "",
     * "description": "",
     * "place": "",
     * "recurRule": ""
     * }
     */
    @Post
    public HttpResponse<@Valid EventResponseDTO> save(@Body @Valid EventDTO eventDTO) {
        var saveResponse = eventRepository.authenticatedSave(
            eventDTO.userId(),
            eventDTO.userToken(),
            eventDTO.title(),
            eventDTO.description(),
            eventDTO.place(),
            eventDTO.recurRuleParts(),
            eventDTO.startDate()
        );
        if (saveResponse.status() != RepositoryResponse.Status.OK) {
            return HttpResponse.unauthorized();
        }
        var createdEvent = saveResponse.get();
        var httpResponse = HttpResponse.created(new EventResponseDTO(
            createdEvent.id(),
            createdEvent.title(),
            createdEvent.description(),
            createdEvent.place(),
            createdEvent.recurRuleParts(),
            createdEvent.startDate())
        );
        return httpResponse.headers(h -> h.location(URI.create("/event/" + createdEvent.id())));
    }

    /**
     * Deletes an event by its id, provided a correct body
     * which holds the valid password of the user.
     *
     * @param id              the id of the event to delete
     * @param loginSessionDTO {
     *                        "userId": "",
     *                        "token": ""
     *                        }
     * @return OK if deleted | NOT_FOUND if the id is unknown | BAD_REQUEST if the credentials are invalid or the user doesn't
     * own the event
     */
    @Delete("/delete/{id}")
    public HttpResponse<?> delete(UUID id, @Body @Valid LoginSessionDTO loginSessionDTO) {
        var deleteResponse = eventRepository.authenticatedDeleteById(
            id,
            loginSessionDTO.userId(),
            loginSessionDTO.token()
        );

        return RepositoryResponse
            .toEmptyHttpResponse(deleteResponse.status())
            .headers(h -> h.location(URI.create("/event/delete/" + id)));
    }

    /**
     * Updates an event by its id, provided a correct body
     * which holds the valid used credentials and the updated data.
     *
     * @param id       the id of the event to update
     * @param eventDTO {
     *                 "userId": "",
     *                 "userToken": "",
     *                 "title": "",
     *                 "description": "",
     *                 "place": "",
     *                 "recurRule"
     *                 }
     * @return {
     * "id": "",
     * "title": "",
     * "description": "",
     * "place": "",
     * "recurRule": ""
     * }
     */
    @Put("/update/{id}")
    public HttpResponse<@Valid EventResponseDTO> update(UUID id, @Body @Valid EventDTO eventDTO) {
        var updateResponse = eventRepository.authenticatedUpdate(
            id,
            eventDTO.userId(),
            eventDTO.userToken(),
            eventDTO.title(),
            eventDTO.description(),
            eventDTO.place(),
            eventDTO.recurRuleParts(),
            eventDTO.startDate()
        );
        if (updateResponse.status() != RepositoryResponse.Status.OK) {
            return HttpResponse.unauthorized();
        }
        var updatedEvent = updateResponse.get();
        var httpResponse = HttpResponse.ok(new EventResponseDTO(
            updatedEvent.id(),
            updatedEvent.title(),
            updatedEvent.description(),
            updatedEvent.place(),
            updatedEvent.recurRuleParts(),
            updatedEvent.startDate())
        );
        return httpResponse.headers(h -> h.location(URI.create("/event/" + updatedEvent.id())));
    }
}
