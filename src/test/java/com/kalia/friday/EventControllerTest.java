package com.kalia.friday;

import com.kalia.friday.dto.EventDTO;
import com.kalia.friday.dto.EventResponseDTO;
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

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@MicronautTest
public class EventControllerTest {

    @Inject
    @Client("/event")
    private HttpClient client;
    private static EntityManager manager;
    private static SHA512Hasher hasher;
    private static User user;
    private static Login login;

    public EventControllerTest(EntityManager manager, SHA512Hasher hasher) {
        EventControllerTest.manager = manager;
        EventControllerTest.hasher = hasher;
    }

    @BeforeEach
    @Transactional
    public void setupUserAndLogin() {
        if (user != null) System.out.println(manager.find(User.class, user.id()));
        user = new User("foo", hasher.hash("bar"));
        manager.persist(user);
        login = new Login(user, LocalDateTime.now());
        manager.persist(login);
    }

    @AfterEach
    @Transactional
    public void removeUserAndLogin() {
        manager.remove(manager.find(User.class, EventControllerTest.user.id()));
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
    public void testSaveUnknownUserFails() {
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

    // FIXME still doesn't work
    public void testSaveUnknownTokenFails() {
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
}
