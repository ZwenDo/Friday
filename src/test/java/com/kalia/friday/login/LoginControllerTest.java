package com.kalia.friday.login;

import com.kalia.friday.DbProperties;
import com.kalia.friday.user.User;
import com.kalia.friday.user.UserCredsDTO;
import com.kalia.friday.util.SHA512Hasher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.TransactionMode;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(transactionMode = TransactionMode.SINGLE_TRANSACTION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DbProperties
public class LoginControllerTest {

    @Inject
    @Client("/auth")
    private HttpClient client;

    @Inject
    @PersistenceContext
    private EntityManager manager;

    @Inject
    private SHA512Hasher hasher;

    private User user;

    @BeforeEach
    public void setupUser() {
        user = new User(UUID.randomUUID().toString(), hasher.hash("1234"));
        manager.persist(user);
        manager.flush();
        manager.getTransaction().commit();
        manager.getTransaction().begin();
    }

    @Test
    public void testLoginInvalidUser() {
        var response = assertThrows(
            HttpClientResponseException.class,
            () -> client
                .toBlocking()
                .exchange(HttpRequest.POST(
                    "/login",
                    new UserCredsDTO("FAKE", "1234")
                ))
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void testLoginInvalidPassword() {
        var response = assertThrows(
            HttpClientResponseException.class,
            () -> client
                .toBlocking()
                .exchange(HttpRequest.POST(
                    "/login",
                    new UserCredsDTO(user.username(), "FAKE")
                ))
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void testLogin() {
        var response = client
            .toBlocking()
            .exchange(HttpRequest.POST(
                "/login",
                new UserCredsDTO(user.username(), "1234")
            ), LoginSessionDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        var body = response.body();
        assertNotNull(body);
        var login = manager.find(Login.class, new LoginId(body.token(), user));
        assertNotNull(login);
    }

    private Login insertLogin() {
        var login = new Login(user, LocalDateTime.now());
        manager.persist(login);
        manager.flush();
        manager.getTransaction().commit();
        return login;
    }

    @Test
    public void testLogoutInvalidUser() {
        var login = insertLogin();
        var response = assertThrows(
            HttpClientResponseException.class,
            () -> client
                .toBlocking()
                .exchange(HttpRequest.POST(
                    "/logout",
                    new LoginSessionDTO(UUID.randomUUID(), login.token())
                ))
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void testLogoutInvalidToken() {
        var login = insertLogin();
        var response = assertThrows(
            HttpClientResponseException.class,
            () -> client
                .toBlocking()
                .exchange(HttpRequest.POST(
                    "/logout",
                    new LoginSessionDTO(login.user().id(), UUID.randomUUID())
                ))
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void testLogout() {
        var login = insertLogin();
        manager.detach(login);
        var response = client
            .toBlocking()
            .exchange(HttpRequest.POST(
                "/logout",
                new LoginSessionDTO(login.user().id(), login.token())
            ));
        assertEquals(HttpStatus.ACCEPTED, response.getStatus());
        login = manager.find(Login.class, new LoginId(login.token(), login.user()));
        assertNull(login);
    }

    @Test
    public void testLogoutAllInvalidUser() {
        var login = insertLogin();
        var response = assertThrows(
            HttpClientResponseException.class,
            () -> client
                .toBlocking()
                .exchange(HttpRequest.POST(
                    "/logout/all",
                    new LoginSessionDTO(UUID.randomUUID(), login.token())
                ))
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void testLogoutAllInvalidToken() {
        var login = insertLogin();
        var response = assertThrows(
            HttpClientResponseException.class,
            () -> client
                .toBlocking()
                .exchange(HttpRequest.POST(
                    "/logout/all",
                    new LoginSessionDTO(login.user().id(), UUID.randomUUID())
                ))
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void testLogoutAll() {
        var login = insertLogin();
        for (var i = 0; i < 10; i++) {
            client.toBlocking().exchange(HttpRequest.POST(
                "/login",
                new UserCredsDTO(user.username(), "1234")
            ));
        }
        var response = client
            .toBlocking()
            .exchange(HttpRequest.POST(
                "/logout/all",
                new LoginSessionDTO(login.user().id(), login.token())
            ));
        assertEquals(HttpStatus.ACCEPTED, response.getStatus());
        var logins = manager
            .createQuery("SELECT l FROM Login l WHERE l.user = :user")
            .setParameter("user", user)
            .getResultList();
        assertTrue(logins.isEmpty());
    }
}
