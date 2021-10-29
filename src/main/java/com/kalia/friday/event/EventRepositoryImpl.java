package com.kalia.friday.event;

import com.kalia.friday.user.UserRepository;
import com.kalia.friday.util.RepositoryResponseStatus;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@code EventRepository} used by Micronaut for dependency injection.
 * (is actually used)
 */
@Singleton
public class EventRepositoryImpl implements EventRepository {

    private final EntityManager manager;
    private final UserRepository userRepository;

    /**
     * Used by Micronaut to create a singleton repository by injection.
     *
     * @param manager the injected {@code EntityManager}
     * @param userRepository
     */
    public EventRepositoryImpl(EntityManager manager, UserRepository userRepository) {
        this.manager = requireNonNull(manager);
        this.userRepository = requireNonNull(userRepository);
    }

    @Override
    @ReadOnly
    public Optional<Event> findById(UUID id) {
        requireNonNull(id);
        return Optional.ofNullable(manager.find(Event.class, id));
    }

    @Override
    @ReadOnly
    public RepositoryResponseStatus findByUserId(UUID userId) {
        requireNonNull(userId);
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return new RepositoryResponseStatus.NotFound();
        }
        var result = manager.createQuery("SELECT e FROM Event e WHERE e.id = :userId", Event.class)
                .setParameter("userId", userId)
                .getResultList();
        return new RepositoryResponseStatus.Ok<>(result);
    }

    @Override
    @Transactional
    public RepositoryResponseStatus save(UUID userId, String title, String description, String place, String recurRuleParts) {
        requireNonNull(userId);
        requireNonNull(title);
        requireNonNull(recurRuleParts);
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return new RepositoryResponseStatus.NotFound();
        }
        var event = new Event(user.get(), title, description, place, recurRuleParts);
        manager.persist(event);
        return new RepositoryResponseStatus.Ok<>(event);
    }

    @Override
    @Transactional
    public RepositoryResponseStatus deleteById(UUID id) {
        requireNonNull(id);
        var event = findById(id);
        if (event.isEmpty()) {
            return new RepositoryResponseStatus.NotFound();
        }
        manager.remove(event.get());
        return new RepositoryResponseStatus.Ok<>(event.get());
    }

    @Override
    @Transactional
    public RepositoryResponseStatus edit(UUID id, String title, String description, String place, String recurRuleParts) {
        requireNonNull(id);
        requireNonNull(title);
        if (findById(id).isEmpty()) {
            return new RepositoryResponseStatus.NotFound();
        }
        manager.createQuery("UPDATE Event SET title = :title," +
                        "description = :description," +
                        "place = :place," +
                        "recurRuleParts = :recurRuleParts" +
                        " WHERE id = :id")
                .setParameter("title", title)
                .setParameter("description", description)
                .setParameter("place", place)
                .setParameter("recurRuleParts", recurRuleParts)
                .setParameter("id", id)
                .executeUpdate();
        return new RepositoryResponseStatus.Ok<>(new Object());
    }
}
