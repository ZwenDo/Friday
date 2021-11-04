package com.kalia.friday;

import com.kalia.friday.dto.LoginSessionDTO;
import com.kalia.friday.dto.UserCredsDTO;
import com.kalia.friday.dto.UserDeleteDTO;
import com.kalia.friday.dto.UserResponseDTO;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginControllerTest {

    @Inject
    @Client("/auth")
    private HttpClient client;

    @Inject
    @Client("/user")
    private HttpClient userClient;

    private static UUID userId;
    private static UUID token;

    @Test
    @Order(1)
    public void setup() {
        var responseSave = userClient
            .toBlocking()
            .exchange(HttpRequest.POST(
                "/",
                new UserCredsDTO("notKamui", "1234")
            ), UserResponseDTO.class);
        assertEquals(HttpStatus.CREATED, responseSave.getStatus());
        var body = responseSave.body();
        assertNotNull(body);
        userId = body.id();
    }

    @Test
    @Order(2)
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
    @Order(3)
    public void testLoginInvalidPassword() {
        var response = assertThrows(
            HttpClientResponseException.class,
            () -> client
                .toBlocking()
                .exchange(HttpRequest.POST(
                    "/login",
                    new UserCredsDTO("notKamui", "FAKE")
                ))
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    @Order(4)
    public void testLogin() {
        var response = client
            .toBlocking()
            .exchange(HttpRequest.POST(
                "/login",
                new UserCredsDTO("notKamui", "1234")
            ), LoginSessionDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        var body = response.body();
        assertNotNull(body);
        token = body.token();
    }

    @Test
    @Order(5)
    public void testLogoutInvalidUser() {
        var response = assertThrows(
            HttpClientResponseException.class,
            () -> client
                .toBlocking()
                .exchange(HttpRequest.POST(
                    "/logout",
                    new LoginSessionDTO(UUID.nameUUIDFromBytes(new byte[16]), token)
                ))
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    @Order(6)
    public void testLogoutInvalidToken() {
        var response = assertThrows(
            HttpClientResponseException.class,
            () -> client
                .toBlocking()
                .exchange(HttpRequest.POST(
                    "/logout",
                    new LoginSessionDTO(userId, UUID.nameUUIDFromBytes(new byte[16]))
                ))
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    @Order(7)
    public void testLogout() {
        var response = client
            .toBlocking()
            .exchange(HttpRequest.POST(
                "/logout",
                new LoginSessionDTO(userId, token)
            ));
        assertEquals(HttpStatus.ACCEPTED, response.getStatus());
    }

    @Test
    @Order(8)
    public void testLogoutAllInvalidUser() {
        var response = assertThrows(
            HttpClientResponseException.class,
            () -> client
                .toBlocking()
                .exchange(HttpRequest.POST(
                    "/logout/all",
                    new LoginSessionDTO(UUID.nameUUIDFromBytes(new byte[16]), token)
                ))
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    @Order(9)
    public void testLogoutAllInvalidToken() {
        var response = assertThrows(
            HttpClientResponseException.class,
            () -> client
                .toBlocking()
                .exchange(HttpRequest.POST(
                    "/logout/all",
                    new LoginSessionDTO(userId, UUID.nameUUIDFromBytes(new byte[16]))
                ))
        );
        assertNotNull(response.getResponse());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    @Order(10)
    public void testLogoutAll() {
        for (var i = 0; i < 10; i++) {
            token = client.toBlocking().exchange(HttpRequest.POST(
                "/login",
                new UserCredsDTO("notKamui", "1234")
            ), LoginSessionDTO.class).body().token();
        }
        var response = client
            .toBlocking()
            .exchange(HttpRequest.POST(
                "/logout/all",
                new LoginSessionDTO(userId, token)
            ));
        assertEquals(HttpStatus.ACCEPTED, response.getStatus());
    }

    @Test
    @Order(11)
    public void end() {
        var responseDelete = userClient
            .toBlocking()
            .exchange(HttpRequest.DELETE(
                "/delete/" + userId,
                new UserDeleteDTO("1234")
            ));
        assertEquals(HttpStatus.NO_CONTENT, responseDelete.getStatus());
    }
}
