package com.kalia.friday;

import com.kalia.friday.dto.EventDTO;
import com.kalia.friday.dto.EventResponseDTO;
import com.kalia.friday.dto.LoginSessionDTO;
import com.kalia.friday.event.Event;
import com.kalia.friday.login.Login;
import com.kalia.friday.user.User;
import com.kalia.friday.util.SHA512Hasher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventControllerTest {

    @Client("/event")
    @Inject
    private HttpClient client;
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
    }

    @AfterEach
    public void removeUserAndLogin() {
        manager.remove(manager.find(User.class, user.id()));
    }

    @Test
    public void testSave() {
        var saveBody = new EventDTO(
                user.id(),
                login.token(),
                "title",
                "description",
                "place",
                "rec"
        );
        var responseSave = client
                .toBlocking()
                .exchange(HttpRequest.POST("/", saveBody), EventResponseDTO.class);
        assertEquals(HttpStatus.CREATED, responseSave.getStatus());
        var body = responseSave.body();
        assertNotNull(body);
    }

    @Test
    public void testSaveWithWrongUserIdFails() {
        var saveBody = new EventDTO(
                UUID.randomUUID(),
                login.token(),
                "title",
                "description",
                "place",
                "rec"
        );
        var response = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.POST("/", saveBody), EventResponseDTO.class)
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void testSaveWithWrongLoginTokenFails() {
        var saveBody = new EventDTO(
                user.id(),
                UUID.randomUUID(),
                "title",
                "description",
                "place",
                "rec"
        );
        var response = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.POST("/", saveBody), EventResponseDTO.class)
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void testSaveInvalidEvent() {
        var saveBody = new EventDTO(
                user.id(),
                login.token(),
                "",
                null,
                "place",
                ""
        );
        assertThrows(HttpClientResponseException.class, () -> client
                .toBlocking()
                .exchange(HttpRequest.POST("/", saveBody), EventResponseDTO.class)
        );
    }

    @Test
    public void testDelete() {
        var event = new Event(user, "title", null, null, "rules");
        manager.persist(event);
        manager.getTransaction().commit();
        var loginDTO = new LoginSessionDTO(user.id(), login.token());
        var responseDelete = client
                .toBlocking()
                .exchange(HttpRequest.DELETE("/delete/" + event.id(), loginDTO));
        assertEquals(HttpStatus.NO_CONTENT, responseDelete.getStatus());
    }

    @Test
    public void testDeleteWithWrongEventIdFails() {
        var event = new Event(user, "title", null, null, "rules");
        manager.persist(event);
        manager.getTransaction().commit();
        var loginDTO = new LoginSessionDTO(user.id(), login.token());
        assertThrows(HttpClientResponseException.class, () -> client
                .toBlocking()
                .exchange(HttpRequest.DELETE("/delete/" + UUID.randomUUID(), loginDTO))
        );
        manager.remove(event);
    }

    @Test
    public void testDeleteWithWrongUserIdFails() {
        var event = new Event(user, "title", null, null, "rules");
        manager.persist(event);
        manager.getTransaction().commit();
        var loginDTO = new LoginSessionDTO(UUID.randomUUID(), login.token());
        assertThrows(HttpClientResponseException.class, () -> client
                .toBlocking()
                .exchange(HttpRequest.DELETE("/delete/" + event.id(), loginDTO))
        );
        manager.remove(event);
    }

    @Test
    public void testDeleteWithWrongLoginTokenFails() {
        var event = new Event(user, "title", null, null, "rules");
        manager.persist(event);
        manager.getTransaction().commit();
        var loginDTO = new LoginSessionDTO(user.id(), UUID.randomUUID());
        assertThrows(HttpClientResponseException.class, () -> client
                .toBlocking()
                .exchange(HttpRequest.DELETE("/delete/" + event.id(), loginDTO))
        );
    }

    @Test
    public void testDeleteOtherUserEventFails() {
        // creates event for user
        var event = new Event(user, "title", null, null, "rules");
        manager.persist(event);

        // creates new user and login
        var otherUser = new User(UUID.randomUUID().toString(), "password");
        manager.persist(otherUser);
        var otherLogin = new Login(otherUser, LocalDateTime.now());
        manager.persist(otherLogin);

        manager.getTransaction().commit(); // commit all changes

        var loginDTO = new LoginSessionDTO(otherUser.id(), otherLogin.token()); // other user login
        assertThrows(HttpClientResponseException.class, () -> client
                .toBlocking()
                .exchange(HttpRequest.DELETE("/delete/" + event.id(), loginDTO))
        );
        var tr = manager.getTransaction();
        tr.begin();
        manager.remove(otherLogin);
        manager.remove(otherUser);
        tr.commit();
    }

    @Test
    public void testUpdate() {
        var event = new Event(user, "title", null, null, "rules");
        manager.persist(event);
        manager.getTransaction().commit();
        var updateDTO = new EventDTO(
                user.id(),
                login.token(),
                "title",
                "description",
                "place",
                "rules"
        );
        var updateResponse = client
                .toBlocking()
                .exchange(HttpRequest.PUT("/update/" + event.id(), updateDTO), EventResponseDTO.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatus());
        manager.refresh(event);
        assertEquals("description", event.description());
    }

    @Test
    public void testUpdateWrongEventIdFails() {
        var event = new Event(user, "title", null, null, "rules");
        manager.persist(event);
        manager.getTransaction().commit();
        var updateDTO = new EventDTO(
                user.id(),
                login.token(),
                "title",
                "description",
                "place",
                "rules"
        );
        assertThrows(HttpClientResponseException.class, () -> client
                .toBlocking()
                .exchange(HttpRequest.PUT("/update/" + UUID.randomUUID(), updateDTO), EventResponseDTO.class)
        );
    }

    @Test
    public void testUpdateWrongUserIdFails() {
        var event = new Event(user, "title", null, null, "rules");
        manager.persist(event);
        manager.getTransaction().commit();
        var updateDTO = new EventDTO(
                UUID.randomUUID(),
                login.token(),
                "title",
                "description",
                "place",
                "rules"
        );
        assertThrows(HttpClientResponseException.class, () -> client
                .toBlocking()
                .exchange(HttpRequest.PUT("/update/" + event.id(), updateDTO), EventResponseDTO.class)
        );
    }

    @Test
    public void testUpdateWrongLoginTokenFails() {
        var event = new Event(user, "title", null, null, "rules");
        manager.persist(event);
        manager.getTransaction().commit();
        var updateDTO = new EventDTO(
                user.id(),
                UUID.randomUUID(),
                "title",
                "description",
                "place",
                "rules"
        );
        assertThrows(HttpClientResponseException.class, () -> client
                .toBlocking()
                .exchange(HttpRequest.PUT("/update/" + event.id(), updateDTO), EventResponseDTO.class)
        );
    }

    @Test
    public void testUpdateWithInvalidValuesFails() {
        var event = new Event(user, "title", null, null, "rules");
        manager.persist(event);
        manager.getTransaction().commit();
        var updateDTO = new EventDTO(
                user.id(),
                login.token(),
                "title",
                "", // invalid blank desc
                "place",
                "rules"
        );
        assertThrows(HttpClientResponseException.class, () -> client
                .toBlocking()
                .exchange(HttpRequest.PUT("/update/" + event.id(), updateDTO), EventResponseDTO.class)
        );
    }
}
