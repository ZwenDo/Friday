package com.kalia.friday.util;

import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepositoryResponseTest {


    @Test
    public void testNoValueThrowsNoSuchElementException() {
        var notFound = RepositoryResponse.notFound();
        var unauthorized = RepositoryResponse.unauthorized();
        var conflict = RepositoryResponse.conflict();
        assertThrows(NoSuchElementException.class, notFound::get);
        assertThrows(NoSuchElementException.class, unauthorized::get);
        assertThrows(NoSuchElementException.class, conflict::get);

    }

    @Test
    public void testToEmptyHttpResponse() {
        var httpOk = RepositoryResponse.toEmptyHttpResponse(RepositoryResponse.Status.OK);
        var httpNotFound = RepositoryResponse.toEmptyHttpResponse(RepositoryResponse.Status.NOT_FOUND);
        var httpUnauthorized = RepositoryResponse.toEmptyHttpResponse(RepositoryResponse.Status.UNAUTHORIZED);
        var httpConflict = RepositoryResponse.toEmptyHttpResponse(RepositoryResponse.Status.CONFLICT);
        assertEquals(HttpStatus.NO_CONTENT, httpOk.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, httpNotFound.getStatus());
        assertEquals(HttpStatus.UNAUTHORIZED, httpUnauthorized.getStatus());
        assertEquals(HttpStatus.CONFLICT, httpConflict.getStatus());
    }
}
