package com.kalia.friday.event;

import com.kalia.friday.login.LoginRepository;
import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.kalia.friday.event.EventRecurRuleParts.requireValidRecurRule;
import static com.kalia.friday.util.StringUtils.requireNotBlank;
import static com.kalia.friday.util.StringUtils.requireNotEmpty;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@code EventRepository} used by Micronaut for dependency injection.
 * (is actually used)
 */
@Singleton
public class EventRepositoryImpl implements EventRepository {

    @Inject
    private EntityManager manager;

    @Inject
    private LoginRepository loginRepository;

    @Override
    @ReadOnly
    public RepositoryResponse<Event> authenticatedFindById(UUID id, UUID userId, UUID userToken) {
        requireNonNull(id);
        requireNonNull(userId);
        requireNonNull(userToken);
        var event = getIfAuthenticated(id, userId, userToken);
        if (event.status() == RepositoryResponse.Status.OK) {
            manager.detach(event.get()); // if present detach before return
        }
        return event;
    }

    @Override
    @ReadOnly
    public RepositoryResponse<List<Event>> authenticatedFindByUserId(UUID userId, UUID userToken) {
        requireNonNull(userId);
        requireNonNull(userToken);
        var userAuthenticate = loginRepository.checkIdentity(userId, userToken);
        if (userAuthenticate.status() != RepositoryResponse.Status.OK) { // invalid user.
            return RepositoryResponse.unauthorized();
        }
        var result = manager.createQuery("SELECT e FROM Event e WHERE e.user.id = :userId", Event.class)
            .setParameter("userId", userId)
            .getResultList();
        result.forEach(it -> manager.detach(it)); // detach before return
        return RepositoryResponse.ok(result);
    }

    @Override
    @Transactional
    public RepositoryResponse<Event> authenticatedSave(
        UUID userId,
        UUID userToken,
        String title,
        String description,
        String place,
        String recurRuleParts,
        LocalDateTime startDate,
        Double latitude,
        Double longitude,
        long duration
    ) {
        requireNonNull(userId);
        requireNotEmpty(title);
        requireNotBlank(description);
        requireNotBlank(place);
        requireNotBlank(recurRuleParts);
        requireValidRecurRule(recurRuleParts);
        requireNonNull(startDate);
        var login = loginRepository.checkIdentity(userId, userToken);
        if (login.status() != RepositoryResponse.Status.OK) {
            return RepositoryResponse.unauthorized();
        }
        var user = login.get().user();
        var event = new Event(user, title, description, place, recurRuleParts, startDate, latitude, longitude, duration);
        manager.merge(user).events().add(event);
        manager.flush();
        manager.detach(event);
        return RepositoryResponse.ok(event);
    }

    @Override
    @Transactional
    public RepositoryResponse<Event> authenticatedDeleteById(UUID id, UUID userId, UUID userToken) {
        requireNonNull(id);
        requireNonNull(userId);
        requireNonNull(userToken);
        var eventGetResponse = getIfAuthenticated(id, userId, userToken);
        if (eventGetResponse.status() != RepositoryResponse.Status.OK) {
            return eventGetResponse;
        }
        manager.remove(eventGetResponse.get());
        return eventGetResponse;
    }

    @Override
    @Transactional
    public RepositoryResponse<Event> authenticatedUpdate(
        UUID id,
        UUID userId,
        UUID userToken,
        String title,
        String description,
        String place,
        String recurRuleParts,
        LocalDateTime localDateTime,
        Double latitude,
        Double longitude,
        long duration
    ) {
        requireNonNull(id);
        requireNonNull(userId);
        requireNotEmpty(title);
        requireNotBlank(description);
        requireNotBlank(place);
        requireNotBlank(recurRuleParts);
        requireValidRecurRule(recurRuleParts);
        requireNonNull(localDateTime);
        var eventGetResponse = getIfAuthenticated(id, userId, userToken);
        if (eventGetResponse.status() != RepositoryResponse.Status.OK) {
            return eventGetResponse;
        }
        var event = eventGetResponse.get();
        event.setTitle(title);
        event.setDescription(description);
        event.setPlace(place);
        event.setRecurRuleParts(recurRuleParts);
        event.setStartDate(localDateTime);
        event.setLatitude(latitude);
        event.setLongitude(longitude);
        event.setDuration(duration);
        manager.flush(); // flush changes before detach
        manager.detach(event);
        return eventGetResponse;
    }

    /**
     * @return unauthorized if wrong credentials or if user is not the owner of the event | not found if event is not found
     */
    private RepositoryResponse<Event> getIfAuthenticated(UUID eventId, UUID userId, UUID userToken) {
        var userAuthenticate = loginRepository.checkIdentity(userId, userToken);
        if (userAuthenticate.status() != RepositoryResponse.Status.OK) { // invalid user.
            return RepositoryResponse.unauthorized();
        }

        var eventRepository = findById(eventId);
        if (eventRepository.status() == RepositoryResponse.Status.NOT_FOUND) { // not found.
            return eventRepository;
        }

        if (!eventRepository.get().user().equals(userAuthenticate.get().user())) { // unauthorized access.
            return RepositoryResponse.unauthorized();
        }

        return eventRepository; // ok.
    }

    private RepositoryResponse<Event> findById(UUID id) {
        var event = manager.find(Event.class, id);
        return event == null ? RepositoryResponse.notFound() : RepositoryResponse.ok(event);
    }
}
