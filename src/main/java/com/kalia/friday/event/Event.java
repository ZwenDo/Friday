package com.kalia.friday.event;

import biweekly.component.VEvent;
import biweekly.property.Geo;
import biweekly.util.Duration;
import com.kalia.friday.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
     * @param startDate      the date on which begins the event
     */
    public Event(
        @NotNull User user,
        @NotEmpty String title,
        @NotBlank String description,
        @NotBlank String place,
        @NotEmpty String recurRuleParts,
        @NotNull LocalDateTime startDate,
        Double latitude,
        Double longitude,
        long duration
    ) {
        this.user = requireNonNull(user);
        this.title = requireNotEmpty(title);
        this.description = requireNotBlank(description);
        this.place = requireNotBlank(place);
        this.recurRuleParts = requireNotEmpty(recurRuleParts);
        this.startDate = startDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.duration = duration;
    }

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotEmpty
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "description")
    private String description;

    @NotBlank
    @Column(name = "place")
    private String place;

    @NotEmpty
    @Column(name = "recur_rule_parts", nullable = false)
    private String recurRuleParts;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "duration", nullable = false)
    private long duration;

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
     * Gets the duration (in seconds) of the event.
     *
     * @return the duration of the event
     */
    public long duration() {
        return duration;
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

    /**
     * Sets the start date of the event.
     *
     * @param startDate the start date to set
     */
    public void setStartDate(@NotNull LocalDateTime startDate) {
        this.startDate = requireNonNull(startDate);
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
     * Sets the duration (in seconds) of the event.
     *
     * @param duration the duration to set
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public VEvent asVEvent() {
        var vevent = new VEvent();

        vevent.setUid(id.toString());
        vevent.setSummary(title);
        vevent.setDescription(description);
        vevent.setLocation(place);
        vevent.setRecurrenceRule(EventRecurRuleParts.fromString(recurRuleParts));
        vevent.setDateStart(Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()));
        vevent.setDuration(Duration.fromMillis(duration));
        if (latitude != null && longitude != null) {
            vevent.setGeo(new Geo(latitude, longitude));
        }

        return vevent;
    }
}
