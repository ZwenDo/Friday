package com.kalia.friday.user;

import com.kalia.friday.util.RepositoryResponseStatus;
import io.micronaut.transaction.annotation.ReadOnly;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface used by Micronaut to inject a UserRepository when requested.
 *
 * Serves to manage the {@code user} table.
 */
public interface UserRepository {
    /**
     * Finds a user by its id.
     *
     * SELECT * USER WHERE ID = {id} LIMIT 1;
     *
     * @param id the id of the user
     * @return an optional of {@code User} (empty if not found)
     */
    @ReadOnly
    Optional<User> findById(@NotNull UUID id);

    /**
     * Saves a new user.
     *
     * @param username the unique username of the user
     * @param password the password of the user (will be hashed SHA-512)
     * @return the created user
     */
    @Transactional
    User save(@NotNull String username, @NotNull String password);

    /**
     * Deletes a user by its id, provided the given password is the correct one.
     *
     * @param id       the id of the user to delete
     * @param password the password of the user to delete
     * @return Ok if deleted | NotFound if the id is unknown | Unauthorized if the password is incorrect
     */
    @Transactional
    RepositoryResponseStatus deleteById(@NotNull UUID id, @NotNull String password);

    /**
     * Updates a user's password (by id), provided the old password is the correct one.
     *
     * @param id          the id of the user of whom to update the password
     * @param oldPassword the old password of the user
     * @param newPassword the new password to replace the old one with
     * @return Ok if updated | NotFound if the id is unknown | Unauthorized if the old password is incorrect
     */
    @Transactional
    RepositoryResponseStatus updatePassword(@NotNull UUID id, @NotNull String oldPassword, @NotNull String newPassword);
}
