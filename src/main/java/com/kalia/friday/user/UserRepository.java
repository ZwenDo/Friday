package com.kalia.friday.user;

import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.transaction.annotation.ReadOnly;

import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Interface used by Micronaut to inject a UserRepository when requested.
 *
 * Serves to manage the {@code user} table.
 *
 * @see User
 */
public interface UserRepository {
    /**
     * Finds a user by its id.
     *
     * SELECT * FROM User WHERE ID = {id} LIMIT 1;
     *
     * @param id the id of the user
     * @return OK if the user is found | NOT_FOUND if the id is unknown
     */
    @ReadOnly
    RepositoryResponse<User> findById(@NotNull UUID id);

    /**
     * Finds a user by its username.
     *
     * SELECT * FROM User WHERE USERNAME = {username} LIMIT 1;
     *
     * @param username the username of the user
     * @return OK if the user is found | NOT_FOUND if the id is unknown
     */
    @ReadOnly
    RepositoryResponse<User> findByUsername(@NotNull String username);

    /**
     * Saves a new user.
     *
     * @param username the unique username of the user
     * @param password the password of the user (will be hashed SHA-512)
     * @return OK if created
     */
    @Transactional
    RepositoryResponse<User> save(@NotEmpty String username, @NotEmpty String password);

    /**
     * Deletes a user by its id, provided the given password is the correct one.
     *
     * @param id       the id of the user to delete
     * @param password the password of the user to delete
     * @return OK if deleted | NOT_FOUND if the id is unknown | UNAUTHORIZED if the password is incorrect
     */
    @Transactional
    RepositoryResponse<User> deleteById(@NotNull UUID id, @NotEmpty String password);

    /**
     * Updates a user's password (by id), provided the old password is the correct one.
     *
     * @param id          the id of the user of whom to update the password
     * @param oldPassword the old password of the user
     * @param newPassword the new password to replace the old one with
     * @return OK if updated | NOT_FOUND if the id is unknown | UNAUTHORIZED if the old password is incorrect
     */
    @Transactional
    RepositoryResponse<User> updatePassword(@NotNull UUID id, @NotEmpty String oldPassword, @NotEmpty String newPassword);
}
