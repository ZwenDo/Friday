package com.kalia.friday.event;

import biweekly.component.VEvent;
import biweekly.property.Geo;
import com.kalia.friday.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
        @NotNull LocalDateTime endDate
    ) {
        requireNonNull(user);
        requireNonNull(title);
        requireNotBlank(description);
        requireNotBlank(place);
        requireNotBlank(recurRuleParts);
        requireNonNull(startDate);
        requireNonNull(endDate);
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

    @Column(name = "end_date", nullable = false)
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
     * Sets the {@code recurRuleParts} of the event, a string representing the event recurrence data in {@code iCalendar}
     * format.
     *
     * @param recurRuleParts the recurRuleParts to set
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
        requireNonNull(endDate);
        requireEndAfterStart(startDate, endDate);
        this.endDate = requireNonNull(endDate);
    }

    /**
     * Gets the event as a {@link VEvent} instance.
     *
     * @return the event as vEvent
     */
    public VEvent asVEvent() {
        var vevent = new VEvent();

        vevent.setUid(id.toString());
        vevent.setSummary(title);
        vevent.setDescription(description);
        vevent.setLocation(place);
        if (recurRuleParts != null) {
            vevent.setRecurrenceRule(EventRecurRuleParts.fromString(recurRuleParts));
        }
        vevent.setDateStart(localToDate(startDate));
        vevent.setDateEnd(localToDate(endDate));
        if (latitude != null && longitude != null) {
            vevent.setGeo(new Geo(latitude, longitude));
        }
        return vevent;
    }

    private static long durationFromDates(LocalDateTime start, LocalDateTime end) {
        return end.atZone(ZoneId.systemDefault()).toEpochSecond() - start.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    private static Date localToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static void requireEndAfterStart(LocalDateTime startDate, LocalDateTime endDate) {
        if (requireNonNull(endDate).isBefore(requireNonNull(startDate))) {
            throw new IllegalArgumentException("endDate is before startDate.");
        }
    }
}
