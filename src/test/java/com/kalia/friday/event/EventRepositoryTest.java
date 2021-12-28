package com.kalia.friday.event;

import com.kalia.friday.TestDbProperties;
import com.kalia.friday.login.Login;
import com.kalia.friday.user.User;
import com.kalia.friday.util.RepositoryResponse;
import com.kalia.friday.util.SHA512Hasher;
import io.micronaut.test.annotation.TransactionMode;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(transactionMode = TransactionMode.SINGLE_TRANSACTION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestDbProperties
public class EventRepositoryTest {

    @Inject
    private EventRepository repository;

    @Inject
    @PersistenceContext
    private EntityManager manager;

    @Inject
    private SHA512Hasher hasher;

    private User user;
    private Login login;

    @BeforeEach
    public void setupUserAndLogin() {
        var username = UUID.randomUUID().toString(); // generate random username to avoid conflicts with DB contents
        user = new User(username, hasher.hash("password"));
        manager.persist(user);
        login = new Login(user, LocalDateTime.now());
        manager.persist(login);
        manager.getTransaction().commit();
        manager.getTransaction().begin();
    }

    @AfterEach
    public void clear() {
        manager.createQuery("DELETE FROM Event e").executeUpdate();
        manager.createQuery("DELETE FROM Login l").executeUpdate();
        manager.createQuery("DELETE FROM User u").executeUpdate();
    }

    private List<Event> insert10Events() {
        var events = new ArrayList<Event>();
        for (int i = 0; i < 10; i++) {
            var event = Event.createEvent(
                user,
                "title",
                null,
                null,
                null,
                LocalDateTime.now(),
                null,
                null,
                LocalDateTime.now()
            );
            events.add(event);
            manager.persist(event);
        }
        manager.flush();
        manager.getTransaction().commit();
        manager.getTransaction().begin();
        return events;
    }

    private Event insertEvent() {
        var event = Event.createEvent(
            user,
            "title",
            null,
            null,
            null,
            LocalDateTime.now(),
            null,
            null,
            LocalDateTime.now()
        );
        manager.persist(event);
        manager.flush();
        manager.getTransaction().commit();
        manager.getTransaction().begin();
        return event;
    }

    @Test
    public void testAuthenticatedFindById() {
        var event = insertEvent();
        var fetched = repository.authenticatedFindById(event.id(), user.id(), login.token());
        assertEquals(RepositoryResponse.Status.OK, fetched.status());
        assertEquals(event.id(), fetched.get().id());
        assertEquals(event.title(), fetched.get().title());
        assertEquals(event.startDate().truncatedTo(ChronoUnit.SECONDS), fetched.get().startDate().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    public void testAuthenticatedFindByIdIdNotFound() {
        var fetched = repository.authenticatedFindById(UUID.randomUUID(), user.id(), login.token());
        assertEquals(RepositoryResponse.Status.NOT_FOUND, fetched.status());
    }

    @Test
    public void testAuthenticatedFindByIdWrongUserId() {
        var event = insertEvent();
        var fetched = repository.authenticatedFindById(event.id(), UUID.randomUUID(), login.token());
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, fetched.status());
    }

    @Test
    public void testAuthenticatedFindByIdWrongToken() {
        var event = insertEvent();
        var fetched = repository.authenticatedFindById(event.id(), user.id(), UUID.randomUUID());
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, fetched.status());
    }

    @Test
    public void testAuthenticatedFindByUserId() {
        var events = insert10Events();
        var response = repository.authenticatedFindByUserId(user.id(), login.token());
        assertEquals(RepositoryResponse.Status.OK, response.status());
        var fetched = response.get();
        assertEquals(events.size(), fetched.size());
        assertTrue(() -> {
            var ids = fetched.stream().map(Event::id).toList();
            for (var event : events) {
                if (!ids.contains(event.id())) return false;
            }
            return true;
        });
    }

    @Test
    public void testAuthenticatedFindByUserIdWrongUserId() {
        var response = repository.authenticatedFindByUserId(UUID.randomUUID(), login.token());
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testAuthenticatedFindByUserIdWrongToken() {
        var response = repository.authenticatedFindByUserId(user.id(), UUID.randomUUID());
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testAuthenticatedSave() {
        var response = repository.authenticatedSave(
            user.id(),
            login.token(),
            "title",
            null,
            null,
            null,
            LocalDateTime.now(),
            null,
            null,
            LocalDateTime.now()
        );
        assertEquals(RepositoryResponse.Status.OK, response.status());
        var event = response.get();
        var fetched = manager.find(Event.class, event.id());
        assertNotNull(fetched);
        assertEquals(event.id(), fetched.id());
        assertEquals(event.title(), fetched.title());
        assertEquals(event.startDate().truncatedTo(ChronoUnit.SECONDS), fetched.startDate().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    public void testAuthenticatedSaveWrongUserId() {
        var response = repository.authenticatedSave(
            UUID.randomUUID(),
            login.token(),
            "title",
            null,
            null,
            null,
            LocalDateTime.now(),
            null,
            null,
            LocalDateTime.now()
        );
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testAuthenticatedSaveWrongToken() {
        var response = repository.authenticatedSave(
            user.id(),
            UUID.randomUUID(),
            "title",
            null,
            null,
            null,
            LocalDateTime.now(),
            null,
            null,
            LocalDateTime.now()
        );
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testAuthenticatedDeleteById() {
        var event = insertEvent();
        var response = repository.authenticatedDeleteById(event.id(), user.id(), login.token());
        assertEquals(RepositoryResponse.Status.OK, response.status());
        var fetched = manager.find(Event.class, event.id());
        assertNull(fetched);
    }

    @Test
    public void testAuthenticatedDeleteByIdNotFound() {
        var response = repository.authenticatedDeleteById(UUID.randomUUID(), user.id(), login.token());
        assertEquals(RepositoryResponse.Status.NOT_FOUND, response.status());
    }

    @Test
    public void testAuthenticatedDeleteByIdWrongUserId() {
        var event = insertEvent();
        var response = repository.authenticatedDeleteById(event.id(), UUID.randomUUID(), login.token());
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testAuthenticatedDeleteByIdWrongToken() {
        var event = insertEvent();
        var response = repository.authenticatedDeleteById(event.id(), user.id(), UUID.randomUUID());
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testAuthenticatedUpdate() {
        var event = insertEvent();
        var response = repository.authenticatedUpdate(
            event.id(),
            user.id(),
            login.token(),
            "title",
            "description",
            "bistrot",
            null,
            LocalDateTime.now(),
            null,
            null,
            LocalDateTime.now()
        );
        assertEquals(RepositoryResponse.Status.OK, response.status());
        manager.refresh(event);
        assertEquals("title", event.title());
        assertEquals("description", event.description());
        assertEquals("bistrot", event.place());
        assertNull(event.recurRuleParts());
    }

    @Test
    public void testAuthenticatedUpdateNotFound() {
        var response = repository.authenticatedUpdate(
            UUID.randomUUID(),
            user.id(),
            login.token(),
            "title",
            "description",
            "somewhere",
            null,
            LocalDateTime.now(),
            null,
            null,
            LocalDateTime.now()
        );
        assertEquals(RepositoryResponse.Status.NOT_FOUND, response.status());
    }

    @Test
    public void testAuthenticatedUpdateWrongUserId() {
        var event = insertEvent();
        var response = repository.authenticatedUpdate(
            event.id(),
            UUID.randomUUID(),
            login.token(),
            "title",
            "description",
            "somewhere",
            null,
            LocalDateTime.now(),
            null,
            null,
            LocalDateTime.now()
        );
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testAuthenticatedUpdateWrongToken() {
        var event = insertEvent();
        var response = repository.authenticatedUpdate(
            event.id(),
            user.id(),
            UUID.randomUUID(),
            "title",
            "description",
            "somewhere",
            null,
            LocalDateTime.now(),
            null,
            null,
            LocalDateTime.now()
        );
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }
}
