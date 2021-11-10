package com.kalia.friday.event;

import com.kalia.friday.login.LoginRepository;
import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@code EventRepository} used by Micronaut for dependency injection.
 * (is actually used)
 */
@Singleton
public class EventRepositoryImpl implements EventRepository {
    private final EntityManager manager;
    private final LoginRepository loginRepository;

    /**
     * Used by Micronaut to create a singleton repository by injection.
     *
     * @param manager the injected {@code EntityManager}
     * @param loginRepository the injected {@code LoginRepository}
     */
    public EventRepositoryImpl(EntityManager manager, LoginRepository loginRepository) {
        this.manager = requireNonNull(manager);
        this.loginRepository = requireNonNull(loginRepository);
    }

    @Override
    @ReadOnly
    public RepositoryResponse<Event> authenticatedFindById(UUID id, UUID userId, UUID userToken) {
        requireNonNull(id);
        requireNonNull(userId);
        requireNonNull(userToken);
        return getIfAuthenticated(id, userId, userToken);
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
        var result = manager.createQuery("SELECT e FROM Event e WHERE e.id = :userId", Event.class)
                .setParameter("userId", userId)
                .getResultList();
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
            String recurRuleParts
    ) {
        requireNonNull(userId);
        requireNonNull(title);
        requireNonNull(recurRuleParts);
        var login = loginRepository.checkIdentity(userId, userToken);
        if (login.status() != RepositoryResponse.Status.OK) {
            return RepositoryResponse.unauthorized();
        }
        var event = new Event(login.get().user(), title, description, place, recurRuleParts);
        manager.persist(event);
        return RepositoryResponse.ok(event);
    }

    @Override
    @Transactional
    public RepositoryResponse<Event> authenticatedDeleteById(UUID id, UUID userId, UUID userToken) {
        requireNonNull(id);
        requireNonNull(userId);
        requireNonNull(userToken);
        var event = getIfAuthenticated(id, userId, userToken);
        if (event.status() != RepositoryResponse.Status.OK) {
            return event;
        }
        manager.remove(event.get());
        return RepositoryResponse.ok(event.get());
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
            String recurRuleParts
    ) {
        requireNonNull(id);
        requireNonNull(title);
        var eventGetResponse = getIfAuthenticated(id, userId, userToken);
        if (eventGetResponse.status() != RepositoryResponse.Status.OK) {
            return eventGetResponse;
        }
        var event = eventGetResponse.get();
        event.setTitle(title);
        event.setDescription(description);
        event.setPlace(place);
        event.setRecurRuleParts(recurRuleParts);
        return RepositoryResponse.ok(eventGetResponse.get());
    }

    /**
     * @return unauthorized if wrong creds or if user is not the owner of the event | not found if event is not found
     */
    @ReadOnly
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

    @ReadOnly
    private RepositoryResponse<Event> findById(UUID id) {
        var event = manager.find(Event.class, id);
        return event == null ? RepositoryResponse.notFound() : RepositoryResponse.ok(event);
    }
}
