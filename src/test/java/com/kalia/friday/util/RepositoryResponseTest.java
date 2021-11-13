package com.kalia.friday.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

public class RepositoryResponseTest {

    @Test
    public void okCreationMethodWithNullArgumentThrowsNPE() {
        Assertions.assertThrows(NullPointerException.class, () -> RepositoryResponse.ok(null));
    }

    @Test
    public void noValueThrowsNoSuchElementException() {
        var response = RepositoryResponse.notFound();
        Assertions.assertThrows(NoSuchElementException.class, response::get);
    }
}
