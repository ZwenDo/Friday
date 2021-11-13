package com.kalia.friday.util;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;

import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

/**
 * Represents the different types of interaction response possible
 * with a repository.
 *
 * @param <T> Type content of the response, if it contains one
 */
public final class RepositoryResponse<T> {

    /**
     * Enum representing the status of the response
     */
    public enum Status {
        OK,
        NOT_FOUND,
        UNAUTHORIZED
    }

    private final Status status;
    private final T value;

    private RepositoryResponse(Status status, T value) {
        this.status = status;
        this.value = value;
    }

    /**
     * Gets the status of the response.
     *
     * @return the status of the response
     */
    public Status status() {
        return status;
    }

    /**
     * If the responses has a content, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the content of the response
     * @throws NoSuchElementException if response has no content
     */
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * Returns a {@code RepositoryResponse} with an {@code OK} status and a content.
     *
     * @param value the content of the response
     * @param <T> the type of the content
     * @return a {@code RepositoryResponse} with an {@code OK} status and containing value
     */
    public static <T> RepositoryResponse<T> ok(T value) {
        requireNonNull(value);
        return new RepositoryResponse<>(Status.OK, value);
    }

    /**
     * Returns {@code RepositoryResponse} with a {@code NOT_FOUND} status and no content.
     *
     * @param <T> the theoretical type of the content
     * @return a {@code RepositoryResponse} with a {@code NOT_FOUND} status and no content
     */
    public static <T> RepositoryResponse<T> notFound() {
        return new RepositoryResponse<>(Status.NOT_FOUND, null);
    }

    /**
     * Returns {@code RepositoryResponse} with an {@code UNAUTHORIZED} status and no content.
     *
     * @param <T> the theoretical type of the content
     * @return a {@code RepositoryResponse} with an {@code UNAUTHORIZED} status and no content
     */
    public static <T> RepositoryResponse<T> unauthorized() {
        return new RepositoryResponse<>(Status.UNAUTHORIZED, null);
    }

    /**
     * Creates an empty {@code MutableHttpResponse} corresponding to the given {@code RepositoryResponse.Status}.
     *
     * @param repositoryResponseStatus the repository response status to create the http response
     * @return the created mutable http response
     */
    public static MutableHttpResponse<?> toEmptyHttpResponse(RepositoryResponse.Status repositoryResponseStatus) {
        return switch (repositoryResponseStatus) {
            case OK -> HttpResponse.noContent();
            case NOT_FOUND -> HttpResponse.notFound();
            case UNAUTHORIZED -> HttpResponse.badRequest();
        };
    }
}
