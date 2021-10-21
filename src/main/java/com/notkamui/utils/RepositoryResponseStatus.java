package com.notkamui.utils;

import static java.util.Objects.requireNonNull;

/**
 * Sealed interface to represent the different types of interaction response possible
 * with a repository.
 */
public sealed interface RepositoryResponseStatus {
    /**
     * The transaction went successfully with the given response.
     *
     * @param <T> the type of the response
     */
    record Ok<T>(T response) implements RepositoryResponseStatus {
        public Ok {
            requireNonNull(response);
        }
    }

    /**
     * The transaction failed because something was missing.
     */
    record NotFound() implements RepositoryResponseStatus {
    }

    /**
     * The transaction failed because the credentials were incorrect.
     */
    record Unauthorized() implements RepositoryResponseStatus {
    }
}
