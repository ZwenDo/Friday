package com.kalia.friday.event;

import com.kalia.friday.util.RepositoryResponse;
import io.micronaut.transaction.annotation.ReadOnly;

import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Interface used by Micronaut to inject an EventRepository when requested.
 *
 * Serves to manage the {@code Event} table.
 *
 * @see Event
 */
public interface EventRepository {

    /**
     * Finds an event by its id.
     *
     * SELECT * FROM Event WHERE id = {id} LIMIT 1;
     *
     * @param id the id of the event
     * @return an optional of {@code Event} (empty if not found)
     */
    @ReadOnly
    RepositoryResponse<Event> findById(@NotNull UUID id);

    /**
     * Finds all events of a user by its user id.
     *
     * @param userId the id of the user
     * @return Ok if found | NotFound if the id is unknown
     */
    @ReadOnly
    RepositoryResponse<List<Event>> findByUserId(@NotNull UUID userId);

    /**
     * Saves a new event.
     *
     * @param userId the id of the user related to the event
     * @param title the title of the event
     * @param description the description of the event
     * @param place the place of the event
     * @param recurRuleParts the recurrence rule parts of the event
     * @return Ok if saved | NotFound if the id is unknown
     */
    @Transactional
    RepositoryResponse<Event> save(
            @NotNull UUID userId,
            @NotNull @NotBlank String title,
            @NotBlank String description,
            @NotBlank String place,
            @NotNull @NotBlank String recurRuleParts
    );

    /**
     * Deletes an event by its id.
     *
     * @param id the id of the event to delete
     * @return Ok if deleted | NotFound if the id is unknown
     */
    @Transactional
    RepositoryResponse<Event> deleteById(@NotNull UUID id);

    /**
     * Edits an event.
     *
     * @param id the id of the event to edit
     * @param title the new title of the event
     * @param description the new description of the event
     * @param place the new place of the event
     * @param recurRuleParts the new {@code recurRuleParts} of the event
     * @return the edited event
     */
    @Transactional
    RepositoryResponse<Event> update(
            @NotNull UUID id,
            @NotNull @NotBlank String title,
            @NotBlank String description,
            @NotBlank String place,
            @NotNull @NotBlank String recurRuleParts
    );
}
