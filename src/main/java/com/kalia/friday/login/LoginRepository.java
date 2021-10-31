package com.kalia.friday.login;

import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.transaction.annotation.ReadOnly;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Interface used by Micronaut to inject a LoginRepository when requested.
 *
 * Serves to manage the {@code login} table.
 *
 * @see Login
 */
public interface LoginRepository {

    /**
     * Gets all the logins.
     *
     * @return OK of all the logins
     */
    @ReadOnly
    RepositoryResponse<List<Login>> allLogins();

    /**
     * Checks the identity of a user by its id and token, and refreshes the token.
     *
     * @param userId the id of the user
     * @param token the token to check
     * @return OK if the identity is valid | NOT_FOUND if the userId is unknown | UNAUTHORIZED if the token is invalid
     */
    @Transactional
    RepositoryResponse<Login> checkIdentity(@NotNull UUID userId, @NotNull UUID token);

    /**
     * Logs a user in and creates a token.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return OK if the credentials are correct | NOT_FOUND if the username is unknown | UNAUTHORIZED if the password is incorrect
     */
    @Transactional
    RepositoryResponse<Login> login(@NotNull String username, @NotNull String password);

    /**
     * Logs a user out by the given token.
     *
     * @param token the token to delete
     * @return OK of the deleted token | NOT_FOUND if the token doesn't exist
     */
    @Transactional
    RepositoryResponse<Login> logout(@NotNull UUID token);

    /**
     * Logs a user out by its id.
     *
     * @param userId the id of the user to logout
     * @return OK of the list of deleted tokens | NOT_FOUND if the user id is unknown
     */
    @Transactional
    RepositoryResponse<List<Login>> logoutAll(@NotNull UUID userId);
}
