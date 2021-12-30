package com.kalia.friday.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kalia.friday.event.Event;
import com.kalia.friday.login.Login;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.kalia.friday.util.StringUtils.requireNotNullOrBlank;

/**
 * Represents the {@code User} table in the database.
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 234567543562L;

    /**
     * Necessary empty constructor for Micronaut and JPA.
     */
    public User() {
    }

    /**
     * Creates an {@code user} row.
     *
     * @param username the name of the user
     * @param password the password of the user
     */
    public User(
        @NotBlank String username,
        @NotBlank String password
    ) {
        this.username = requireNotNullOrBlank(username);
        this.password = requireNotNullOrBlank(password);
    }

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(name = "username", unique = true, nullable = false, length = 64)
    private String username;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final Set<Event> events = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final Set<Login> logins = new HashSet<>();

    /**
     * Gets the id of the user row.
     *
     * @return the id of the user
     */
    public UUID id() {
        return id;
    }

    /**
     * Gets the name of the user row.
     *
     * @return the name of the user
     */
    public String username() {
        return username;
    }

    /**
     * Gets the password of the user row.
     *
     * @return the password of the user
     */
    public String password() {
        return password;
    }

    /**
     * Gets the events of the user row.
     *
     * @return the events of the user
     */
    public Set<Event> events() {
        return events;
    }

    /**
     * Gets the logins of the user row.
     *
     * @return the logins of the user
     */
    public Set<Login> logins() {
        return logins;
    }

    /**
     * Sets the password of the user row.
     *
     * @param password the new password to be set
     */
    public void setPassword(@NotBlank String password) {
        this.password = requireNotNullOrBlank(password);
    }
}
