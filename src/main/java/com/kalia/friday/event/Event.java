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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Represents the {@code Event} table in the database.
 */
@Entity
@Table(name = "event")
public class Event implements Serializable {

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
        @NotNull @NotBlank String title,
        @NotBlank String description,
        @NotBlank String place,
        @NotNull @NotBlank String recurRuleParts
    ) {
        this.user = requireNonNull(user);
        this.title = requireNonNull(title);
        this.description = requireNonNull(description);
        this.place = requireNonNull(place);
        this.recurRuleParts = requireNonNull(recurRuleParts);
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

    public UUID id() {
        return id;
    }

    public User user() {
        return user;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String place() {
        return place;
    }

    public String recurRuleParts() {
        return recurRuleParts;
    }

    public void setTitle(@NotNull @NotBlank String title) {
        this.title = requireNonNull(title);
    }

    public void setDescription(@NotBlank String description) {
        this.description = description;
    }

    public void setPlace(@NotBlank String place) {
        this.place = place;
    }

    public void setRecurRuleParts(@NotBlank String recurRuleParts) {
        this.recurRuleParts = recurRuleParts;
    }
}
