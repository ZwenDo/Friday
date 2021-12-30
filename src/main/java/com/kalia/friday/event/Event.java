package com.kalia.friday.event;

import com.kalia.friday.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.kalia.friday.util.StringUtils.requireNotBlank;
import static com.kalia.friday.util.StringUtils.requireNotNullOrBlank;
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

    private Event(
        User user,
        String title,
        String description,
        String place,
        String recurRuleParts,
        LocalDateTime startDate,
        Double latitude,
        Double longitude,
        LocalDateTime endDate
    ) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.place = place;
        this.recurRuleParts = recurRuleParts;
        this.startDate = startDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.endDate = endDate;
    }

    /**
     * Creates an {@code event} row.
     *
     * @param user           the user which this event is attached to
     * @param title          the title of the event
     * @param description    the optional description of the event
     * @param place          the optional location of the event
     * @param recurRuleParts the ICal recursion rule parts
     * @param startDate      the date on which begins the event
     * @param latitude       the latitude of the event
     * @param longitude      the longitude of the event
     * @param endDate        the date on which ends the event
     */
    public static Event createEvent(
        @NotNull User user,
        @NotBlank String title,
        @Size(min = 1) String description,
        @Size(min = 1) String place,
        @Size(min = 1) String recurRuleParts,
        @NotNull LocalDateTime startDate,
        Double latitude,
        Double longitude,
        LocalDateTime endDate
    ) {
        requireNonNull(user);
        requireNonNull(title);
        requireNotBlank(description);
        requireNotBlank(place);
        requireNotBlank(recurRuleParts);
        requireNonNull(startDate);
        requireEndAfterStart(startDate, endDate);
        return new Event(user, title, description, place, recurRuleParts, startDate, latitude, longitude, endDate);
    }

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @Size(min = 1)
    @Column(name = "description")
    private String description;

    @Size(min = 1)
    @Column(name = "place")
    private String place;

    @Size(min = 1)
    @Column(name = "recur_rule_parts")
    private String recurRuleParts;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "end_date")
    private LocalDateTime endDate;

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
     * Gets the rrule of the event.
     *
     * @return the rrule of the event
     */
    public String recurRuleParts() {
        return recurRuleParts;
    }

    /**
     * Gets the start date of the event.
     *
     * @return the startDate of the event
     */
    public LocalDateTime startDate() {
        return startDate;
    }

    /**
     * Gets the latitude of the event.
     *
     * @return the latitude of the event
     */
    public Double latitude() {
        return latitude;
    }

    /**
     * Gets the longitude of the event.
     *
     * @return the longitude of the event
     */
    public Double longitude() {
        return longitude;
    }

    /**
     * Gets the end date of the event.
     *
     * @return the endDate of the event
     */
    public LocalDateTime endDate() {
        return endDate;
    }

    /**
     * Sets the title of the event.
     *
     * @param title the title to set
     */
    public void setTitle(@NotBlank String title) {
        this.title = requireNotNullOrBlank(title);
    }

    /**
     * Sets the description of the event.
     *
     * @param description the description to set
     */
    public void setDescription(@Size(min = 1) String description) {
        this.description = requireNotBlank(description);
    }

    /**
     * Sets the place of the event.
     *
     * @param place the place to set
     */
    public void setPlace(@Size(min = 1) String place) {
        this.place = requireNotBlank(place);
    }

    /**
     * Sets the {@code rrule} of the event, a string representing the event recurrence data in {@code iCalendar}
     * format.
     *
     * @param recurRuleParts the rrule to set
     */
    public void setRecurRuleParts(@Size(min = 1) String recurRuleParts) {
        this.recurRuleParts = requireNotBlank(recurRuleParts);
    }

    /**
     * Sets the start date of the event.
     *
     * @param startDate the start date to set
     */
    public void setStartDate(@NotNull LocalDateTime startDate) {
        requireNonNull(startDate);
        requireEndAfterStart(startDate, endDate);
        this.startDate = startDate;
    }

    /**
     * Sets the latitude of the event.
     *
     * @param latitude the latitude to set
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Sets the longitude of the event.
     *
     * @param longitude the longitude to set
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Sets the end date of the event.
     *
     * @param endDate the end date to set
     */
    public void setEndDate(LocalDateTime endDate) {
        requireEndAfterStart(startDate, endDate);
        this.endDate = endDate;
    }

    /**
     * Converts an event to an eventResponseDTO.
     *
     * @return the created eventResponseDTO
     */
    public EventResponseDTO toEventResponseDTO() {
        return new EventResponseDTO(id, title, description, place, recurRuleParts, startDate, latitude, longitude, endDate);
    }

    /**
     * Asserts that a dae starts after another.
     *
     * @param startDate the date that must start before
     * @param endDate the date that must start afer
     */
    public static void requireEndAfterStart(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate == null) return;
        if (endDate.isBefore(requireNonNull(startDate))) {
            throw new IllegalArgumentException("endDate is before startDate.");
        }
    }
}
