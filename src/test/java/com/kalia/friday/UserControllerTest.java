package com.kalia.friday;

import com.kalia.friday.dto.UserDeleteDTO;
import com.kalia.friday.dto.UserPasswordUpdateDTO;
import com.kalia.friday.dto.UserResponseDTO;
import com.kalia.friday.dto.UserSaveDTO;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
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
public class UserControllerTest {

    @Inject
    @Client("/user")
    private HttpClient client;

    private static UUID retrievedId;

    @Test
    @Order(1)
    public void testSaveUser() {
        HttpResponse<UserResponseDTO> responseSave = client
            .toBlocking()
            .exchange(HttpRequest.POST(
                "/",
                new UserSaveDTO("notKamui", "1234")
            ));
        assertEquals(HttpStatus.CREATED, responseSave.getStatus());
        var header = responseSave.header(HttpHeaders.LOCATION);
        assertNotNull(responseSave);
        assert header != null;
        retrievedId = UUID.fromString(header.substring("/user/".length()));
    }

    @Test
    @Order(2)
    public void testUpdatePasswordWrongPassword() {
        var thrownUpdate = assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().exchange(HttpRequest.PUT(
                "/update/" + retrievedId,
                new UserPasswordUpdateDTO("FAKE", "abcd")
            ))
        );
        assertNotNull(thrownUpdate.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, thrownUpdate.getStatus());
    }

    @Test
    @Order(3)
    public void testUpdatePassword() {
        var responseUpdate = client
            .toBlocking()
            .exchange(HttpRequest.PUT(
                "/update/" + retrievedId,
                new UserPasswordUpdateDTO("1234", "abcd")
            ));
        assertEquals(HttpStatus.NO_CONTENT, responseUpdate.getStatus());
    }

    @Test
    @Order(4)
    public void testDeleteWrongPassword() {
        var thrownDelete = assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().exchange(HttpRequest.DELETE(
                "/delete/" + retrievedId,
                new UserDeleteDTO("FAKE")
            ))
        );
        assertNotNull(thrownDelete.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, thrownDelete.getStatus());
    }

    @Test
    @Order(5)
    public void testDelete() {
        var responseDelete = client
            .toBlocking()
            .exchange(HttpRequest.DELETE(
                "/delete/" + retrievedId,
                new UserDeleteDTO("abcd")
            ));
        assertEquals(HttpStatus.NO_CONTENT, responseDelete.getStatus());
    }

    @Test
    @Order(6)
    public void testUpdatePasswordWithUnknownId() {
        var thrownUpdateUnknown = assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().exchange(HttpRequest.PUT(
                "/update/" + UUID.nameUUIDFromBytes(new byte[16]),
                new UserPasswordUpdateDTO("abcd", "foobar")
            ))
        );
        assertNotNull(thrownUpdateUnknown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrownUpdateUnknown.getStatus());
    }

    @Test
    @Order(7)
    public void testDeleteWithUnknownId() {
        var thrownDeleteUnknown = assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().exchange(HttpRequest.DELETE(
                "/delete/" + UUID.nameUUIDFromBytes(new byte[16]),
                new UserDeleteDTO("abcd")
            ))
        );
        assertNotNull(thrownDeleteUnknown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrownDeleteUnknown.getStatus());
    }
}
