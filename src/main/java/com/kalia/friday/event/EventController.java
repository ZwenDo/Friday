package com.kalia.friday.event;

import com.kalia.friday.login.LoginSessionDTO;
import com.kalia.friday.util.BiweeklyUtils;
import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
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
        try {
            var saveResponse = eventRepository.authenticatedSave(eventDTO);
            if (saveResponse.status() != RepositoryResponse.Status.OK) {
                return HttpResponse.unauthorized();
            }
            var createdEvent = saveResponse.get();
            var httpResponse = HttpResponse.created(createdEvent.toEventResponseDTO());
            return httpResponse.headers(h -> h.location(URI.create(DEFAULT_ROUTE + createdEvent.id())));
        } catch (IllegalArgumentException e) { // if save parameters (recur rules) are invalid.
            return HttpResponse.<EventResponseDTO>badRequest().headers(h -> h.location(URI.create(DEFAULT_ROUTE)));
        }
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
        try {
            var updateResponse = eventRepository.authenticatedUpdate(id, eventDTO);
            if (updateResponse.status() != RepositoryResponse.Status.OK) {
                return HttpResponse.unauthorized();
            }
            var updatedEvent = updateResponse.get();
            var httpResponse = HttpResponse.ok(updatedEvent.toEventResponseDTO());
            return httpResponse.headers(h -> h.location(URI.create(DEFAULT_ROUTE + updatedEvent.id())));
        } catch (IllegalArgumentException e) { // if save parameters (recur rules) are invalid.
            return HttpResponse.<EventResponseDTO>notFound().headers(h -> h.location(URI.create(DEFAULT_ROUTE)));
        }
    }

    /**
     * Retrieves all events of a given user.
     *
     * @param loginSessionDTO {
     *                        "userId": "",
     *                        "token": "",
     *                        }
     * @return OK with body containing events if success | UNAUTHORIZED if invalid credentials
     */
    @Post(value = "/allbyuser", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<List<EventResponseDTO>> allByUser(@Body @Valid LoginSessionDTO loginSessionDTO) {
        var findResponse = eventRepository.authenticatedFindByUserId(loginSessionDTO.userId(), loginSessionDTO.token());
        if (findResponse.status() != RepositoryResponse.Status.OK) {
            return HttpResponse.unauthorized();
        }
        var events = findResponse.get()
            .stream()
            .map(Event::toEventResponseDTO)
            .toList();
        return HttpResponse.ok(events)
            .headers(h -> h.location(URI.create(DEFAULT_ROUTE + "allbyuser")));
    }

    /**
     * Imports an ics calendar from a URL (request will use GET method).
     *
     * @param url             url to get the calendar
     * @param loginSessionDTO {
     *                        "userId": "",
     *                        "token": "",
     *                        }
     * @return OK if imported | BAD REQUEST if invalid file content | UNAUTHORIZED if invalid credentials
     */
    @Post("/import/url")
    public HttpResponse<Void> importFromURL(String url, @Body @Valid LoginSessionDTO loginSessionDTO) {
        var request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        var client = HttpClient.newHttpClient();
        try {
            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            var events = BiweeklyUtils.eventDTOListFromString(response.body(), loginSessionDTO.userId(), loginSessionDTO.token());
            if (events.isEmpty()) {
                return HttpResponse.badRequest();
            }

            var saveResponse = eventRepository.authenticatedEventListSave(events);
            if (saveResponse.status() != RepositoryResponse.Status.OK) {
                return HttpResponse.unauthorized();
            }

            return HttpResponse.ok(null);
        } catch (IOException | InterruptedException e) {
            return HttpResponse.serverError();
        } catch (NullPointerException | IllegalArgumentException e) { // if invalid format
            return HttpResponse.badRequest();
        }
    }

    /**
     * Imports an ics calendar from a file.
     *
     * @param fileContent     file content
     * @param loginSessionDTO {
     *                        "userId": "",
     *                        "token": "",
     *                        }
     * @return OK if imported | BAD REQUEST if invalid file content | UNAUTHORIZED if invalid credentials
     */
    @Post("/import/file")
    public HttpResponse<Void> importFromFile(String fileContent, @Body @Valid LoginSessionDTO loginSessionDTO) {
        var events = BiweeklyUtils.eventDTOListFromString(fileContent, loginSessionDTO.userId(), loginSessionDTO.token());
        if (events.isEmpty()) {
            return HttpResponse.badRequest();
        }

        try {
            var saveResponse = eventRepository.authenticatedEventListSave(events);
            if (saveResponse.status() != RepositoryResponse.Status.OK) {
                return HttpResponse.unauthorized();
            }
            return HttpResponse.ok(null);
        } catch (NullPointerException | IllegalArgumentException e) { // if invalid format
            return HttpResponse.badRequest();
        }
    }
}
