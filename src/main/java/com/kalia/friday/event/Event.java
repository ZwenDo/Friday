package com.kalia.friday.event;

import com.kalia.friday.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import static com.kalia.friday.util.StringUtils.requireNotBlank;
import static com.kalia.friday.util.StringUtils.requireNotEmpty;
import static java.util.Objects.requireNonNull;

/**
 * Represents the {@code Event} table in the database.
 */
@Entity
@Table(name = "event")
public class Event implements Serializable {

    @Serial
    private static final long serialVersionUID = 23456766L;

    /**
     * Necessary empty constructor for Micronaut and JPA.
     */
    public Event() {
    }

    /**
     * Creates an {@code event} row.
     *
     * @param user           the user which this event is attached to
     * @param title          the title of the event
     * @param description    the optional description of the event
     * @param place          the optional location of the event
     * @param recurRuleParts the ICal recursion rule parts
     */
    public Event(
        @NotNull User user,
        @NotEmpty String title,
        @NotBlank String description,
        @NotBlank String place,
        @NotEmpty String recurRuleParts
    ) {
        this.user = requireNonNull(user);
        this.title = requireNotEmpty(title);
        this.description = requireNotBlank(description);
        this.place = requireNotBlank(place);
        this.recurRuleParts = requireNotEmpty(recurRuleParts);
    }

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "description")
    private String description;

    @NotBlank
    @Column(name = "place")
    private String place;

    @NotNull
    @NotBlank
    @Column(name = "recur_rule_parts", nullable = false)
    private String recurRuleParts;

    /**
     * Gets the id of the event.
     *
     * @return the id of the event
     */
    public UUID id() {
        return id;
    }

    /**
     * Gets the user owning the event.
     *
     * @return the user owning the event
     */
    public User user() {
        return user;
    }

    /**
     * Gets the title of the event.
     *
     * @return the title of the event
     */
    public String title() {
        return title;
    }

    /**
     * Gets the description of the event.
     *
     * @return the description of the event
     */
    public String description() {
        return description;
    }

    /**
     * Gets the place of the event.
     *
     * @return the place of the event
     */
    public String place() {
        return place;
    }

    /**
     * Gets the recurRuleParts of the event.
     *
     * @return the recurRuleParts of the event
     */
    public String recurRuleParts() {
        return recurRuleParts;
    }

    /**
     * Sets the title of the event.
     *
     * @param title the title to set
     */
    public void setTitle(@NotEmpty String title) {
        this.title = requireNotEmpty(title);
    }

    /**
     * Sets the description of the event.
     *
     * @param description the description to set
     */
    public void setDescription(@NotBlank String description) {
        this.description = requireNotBlank(description);
    }

    /**
     * Sets the place of the event.
     *
     * @param place the place to set
     */
    public void setPlace(@NotBlank String place) {
        this.place = requireNotBlank(place);
    }

    /**
     * Sets the {@code recurRuleParts} of the event, a string representing the event recurrence data in {@code iCalendar}
     * format.
     *
     * @param recurRuleParts the recurRuleParts to set
     */
    public void setRecurRuleParts(@NotEmpty String recurRuleParts) {
        this.recurRuleParts = requireNotEmpty(recurRuleParts);
    }
}
