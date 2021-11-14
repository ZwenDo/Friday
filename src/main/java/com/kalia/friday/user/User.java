package com.kalia.friday.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kalia.friday.event.Event;
import com.kalia.friday.login.Login;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.kalia.friday.util.StringUtils.requireNotEmpty;

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
        @NotEmpty String username,
        @NotEmpty @NotBlank String password
    ) {
        this.username = requireNotEmpty(username);
        this.password = requireNotEmpty(password);
    }

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @NotBlank
    @Column(name = "username", unique = true, nullable = false, length = 64)
    private String username;

    @NotNull
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
    public void setPassword(@NotEmpty String password) {
        this.password = requireNotEmpty(password);
    }
}
