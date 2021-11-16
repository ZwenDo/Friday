package com.kalia.friday.user;

import com.kalia.friday.DbProperties;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest(transactionMode = TransactionMode.SINGLE_TRANSACTION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DbProperties
public class UserControllerTest {

    @Inject
    @Client("/user")
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
    public void testSaveUser() {
        var username = UUID.randomUUID().toString();
        var responseSave = client
            .toBlocking()
            .exchange(HttpRequest.POST(
                "/",
                new UserCredsDTO(username, "1234")
            ), UserResponseDTO.class);
        assertEquals(HttpStatus.CREATED, responseSave.getStatus());
        var body = responseSave.body();
        assertNotNull(body);
        var user = manager.find(User.class, body.id());
        assertNotNull(user);
        assertEquals(username, user.username());
        assertEquals(hasher.hash("1234"), user.password());
    }

    @Test
    public void testUpdatePasswordWrongPassword() {
        var thrownUpdate = assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().exchange(HttpRequest.PUT(
                "/update/" + user.id(),
                new UserPasswordUpdateDTO("FAKE", "abcd")
            ))
        );
        assertNotNull(thrownUpdate.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, thrownUpdate.getStatus());
    }

    @Test
    public void testUpdatePassword() {
        var responseUpdate = client
            .toBlocking()
            .exchange(HttpRequest.PUT(
                "/update/" + user.id(),
                new UserPasswordUpdateDTO("1234", "abcd")
            ));
        assertEquals(HttpStatus.NO_CONTENT, responseUpdate.getStatus());
        manager.refresh(user);
        assertNotNull(user);
        assertEquals(hasher.hash("abcd"), user.password());
    }

    @Test
    public void testDeleteWrongPassword() {
        var thrownDelete = assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().exchange(HttpRequest.DELETE(
                "/delete/" + user.id(),
                new UserDeleteDTO("FAKE")
            ))
        );
        assertNotNull(thrownDelete.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, thrownDelete.getStatus());
    }

    @Test
    public void testDelete() {
        manager.detach(user);
        var responseDelete = client
            .toBlocking()
            .exchange(HttpRequest.DELETE(
                "/delete/" + user.id(),
                new UserDeleteDTO("1234")
            ));
        assertEquals(HttpStatus.NO_CONTENT, responseDelete.getStatus());
        var user = manager.find(User.class, this.user.id());
        assertNull(user);
    }

    @Test
    public void testUpdatePasswordWithUnknownId() {
        var thrownUpdateUnknown = assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().exchange(HttpRequest.PUT(
                "/update/" + UUID.randomUUID(),
                new UserPasswordUpdateDTO("1234", "foobar")
            ))
        );
        assertNotNull(thrownUpdateUnknown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrownUpdateUnknown.getStatus());
    }

    @Test
    public void testDeleteWithUnknownId() {
        var thrownDeleteUnknown = assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().exchange(HttpRequest.DELETE(
                "/delete/" + UUID.randomUUID(),
                new UserDeleteDTO("1234")
            ))
        );
        assertNotNull(thrownDeleteUnknown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrownDeleteUnknown.getStatus());
    }
}
