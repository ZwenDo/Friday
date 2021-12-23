package com.kalia.friday.event;

import biweekly.Biweekly;
import biweekly.ICalendar;
import com.kalia.friday.login.LoginSessionDTO;
import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
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
    private static final String DEFAULT_ROUTE = "/api/event/";

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
        var saveResponse = eventRepository.authenticatedSave(eventDTO);
        if (saveResponse.status() != RepositoryResponse.Status.OK) {
            return HttpResponse.unauthorized();
        }
        var createdEvent = saveResponse.get();
        var httpResponse = HttpResponse.created(EventResponseDTO.fromEvent(createdEvent));
        return httpResponse.headers(h -> h.location(URI.create(DEFAULT_ROUTE + createdEvent.id())));
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
    public HttpResponse<Void> delete(UUID id, @Body @Valid LoginSessionDTO loginSessionDTO) {
        var deleteResponse = eventRepository.authenticatedDeleteById(
            id,
            loginSessionDTO.userId(),
            loginSessionDTO.token()
        );

        return RepositoryResponse
            .toEmptyHttpResponse(deleteResponse.status())
            .headers(h -> h.location(URI.create(DEFAULT_ROUTE + "delete/" + id)));
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
        var updateResponse = eventRepository.authenticatedUpdate(id, eventDTO);
        if (updateResponse.status() != RepositoryResponse.Status.OK) {
            return HttpResponse.unauthorized();
        }
        var updatedEvent = updateResponse.get();
        var httpResponse = HttpResponse.ok(EventResponseDTO.fromEvent(updatedEvent));
        return httpResponse.headers(h -> h.location(URI.create(DEFAULT_ROUTE + updatedEvent.id())));
    }

    /**
     * Gets the ICalendar of a user.
     *
     * @param userId the user id
     * @param token the login session token
     * @return a string representing the icalendar of the user
     */
    @Get("/allbyuser/{userId}/{token}")
    public HttpResponse<String> allByUser(UUID userId, UUID token) {
        var findResponse = eventRepository.authenticatedFindByUserId(userId, token);
        if (findResponse.status() != RepositoryResponse.Status.OK) {
            return HttpResponse.unauthorized();
        }
        var iCalendar = new ICalendar();
        findResponse.get()
            .stream()
            .map(Event::asVEvent)
            .toList()
            .forEach(iCalendar::addEvent);
        return HttpResponse.ok(Biweekly.write(iCalendar).go())
            .headers(h -> h.location(URI.create(DEFAULT_ROUTE + "allbyuser")));
    }
}
