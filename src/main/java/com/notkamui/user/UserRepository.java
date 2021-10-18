package com.notkamui.user;

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
    Optional<User> findById(@NotNull UUID id);

    /**
     * Saves a new user.
     *
     * @param username the unique username of the user
     * @param password the password of the user (will be hashed SHA-512)
     * @return the created user
     */
    User save(@NotNull String username, @NotNull String password);

    /**
     * Deletes a user by its id, provided the given password is the correct one.
     *
     * @param id the id of the user to delete
     * @param password the password of the user to delete
     * @return true if the user has been deleted successfully (known id + valid password), false otherwise.
     */
    boolean deleteById(@NotNull UUID id, @NotNull String password);
}
