package com.kalia.friday.event;

import com.kalia.friday.login.LoginSessionDTO;
import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
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

    @Get
    public HttpResponse<String> foo() {
        return HttpResponse.ok("""
            BEGIN:VCALENDAR
            VERSION:2.0
            PRODID:-//Michael Angstadt//biweekly 0.6.6//EN
            NAME:Kamelia calendar
            DESCRIPTION:The new jetbrains
            BEGIN:VEVENT
            UID:72e75074-f455-485f-870c-d511e3d9d4cb
            DTSTAMP:20211221T220818Z
            SUMMARY:Réunion corporate
            DESCRIPTION:On va regarder des vidéos et rien faire
            LOCATION:BU de l'UGE
            GEO:48.839915;2.590522
            DTSTART:20201231T230000Z
            DURATION:PT2H
            RRULE:FREQ=MONTHLY;BYMINUTE=30;BYHOUR=11;BYDAY=-1WE
            END:VEVENT
            END:VCALENDAR""");
    }

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
