package com.kalia.friday.event;

import com.kalia.friday.user.UserRepository;
import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
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
     * @param userRepository the injected {@code UserRepository}
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
    public RepositoryResponse<List<Event>> findByUserId(UUID userId) {
        requireNonNull(userId);
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return RepositoryResponse.notFound();
        }
        var result = manager.createQuery("SELECT e FROM Event e WHERE e.id = :userId", Event.class)
                .setParameter("userId", userId)
                .getResultList();
        return RepositoryResponse.ok(result);
    }

    @Override
    @Transactional
    public RepositoryResponse<Event> save(UUID userId, String title, String description, String place, String recurRuleParts) {
        requireNonNull(userId);
        requireNonNull(title);
        requireNonNull(recurRuleParts);
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return RepositoryResponse.notFound();
        }
        var event = new Event(user.get(), title, description, place, recurRuleParts);
        manager.persist(event);
        return RepositoryResponse.ok(event);
    }

    @Override
    @Transactional
    public RepositoryResponse<Event> deleteById(UUID id) {
        requireNonNull(id);
        var event = findById(id);
        if (event.isEmpty()) {
            return RepositoryResponse.notFound();
        }
        manager.remove(event.get());
        return RepositoryResponse.ok(event.get());
    }

    @Override
    @Transactional
    public RepositoryResponse<Event> edit(UUID id, String title, String description, String place, String recurRuleParts) {
        requireNonNull(id);
        requireNonNull(title);
        var event = findById(id);
        if (event.isEmpty()) {
            return RepositoryResponse.notFound();
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
        return RepositoryResponse.ok(event.get());
    }
}
