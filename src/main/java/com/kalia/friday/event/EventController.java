package com.kalia.friday.event;

import com.kalia.friday.dto.EventDTO;
import com.kalia.friday.dto.EventResponseDTO;
import com.kalia.friday.dto.LoginSessionDTO;
import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.http.HttpResponse;
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

@ExecuteOn(value = TaskExecutors.IO)
@Controller("/event")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = requireNonNull(eventRepository);
    }

    @Post
    public HttpResponse<EventResponseDTO> save(@Body @Valid EventDTO eventDTO) {
        requireNonNull(eventDTO);
        var saveResponse = eventRepository.authenticatedSave(
                eventDTO.userId(),
                eventDTO.userToken(),
                eventDTO.title(),
                eventDTO.description(),
                eventDTO.place(),
                eventDTO.recurRuleParts()
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
                createdEvent.recurRuleParts())
        );
        return httpResponse.headers(h -> h.location(URI.create("/event/" + createdEvent.id())));
    }

    @Delete("/delete/{id}")
    public HttpResponse<?> delete(UUID id, @Body @Valid LoginSessionDTO loginSessionDTO) {
        requireNonNull(id);
        requireNonNull(loginSessionDTO);
        var deleteResponse = eventRepository.authenticatedDeleteById(
                id,
                loginSessionDTO.userId(),
                loginSessionDTO.token()
        );
        return RepositoryResponse
                .toEmptyHttpResponse(deleteResponse.status())
                .headers(h -> h.location(URI.create("/event/delete/" + id)));
    }

    @Put("/update/{id}")
    public HttpResponse<EventResponseDTO> update(UUID id, @Body @Valid EventDTO eventDTO) {
        requireNonNull(id);
        requireNonNull(eventDTO);
        var updateResponse = eventRepository.authenticatedUpdate(
                id,
                eventDTO.userId(),
                eventDTO.userToken(),
                eventDTO.title(),
                eventDTO.description(),
                eventDTO.place(),
                eventDTO.recurRuleParts()
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
                updatedEvent.recurRuleParts())
        );
        return httpResponse.headers(h -> h.location(URI.create("/event/" + updatedEvent.id())));
    }
}
