package com.notkamui.utils;

public sealed interface RepositoryResponseStatus {
    record Ok<T>(T response) implements RepositoryResponseStatus {
    }

    record NotFound() implements RepositoryResponseStatus {
    }

    record Unauthorized() implements RepositoryResponseStatus {
    }
}
