package com.kalia.friday.event;

import com.kalia.friday.dto.EventResponseDTO;
import com.kalia.friday.dto.EventSaveDTO;
import com.kalia.friday.dto.LoginSessionDTO;
import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import javax.persistence.JoinColumn;
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
    @JoinColumn
    public HttpResponse<EventResponseDTO> save(@Body @Valid EventSaveDTO eventSaveDTO) {
        requireNonNull(eventSaveDTO);
        var saveResponse = eventRepository.authenticatedSave(
                eventSaveDTO.userId(),
                eventSaveDTO.userToken(),
                eventSaveDTO.title(),
                eventSaveDTO.description(),
                eventSaveDTO.place(),
                eventSaveDTO.recurRuleParts()
        );
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

    @Put("/delete/{id}")
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
}
