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
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Represents the {@code User} table in the database.
 */
@Entity
@Table(name = "user")
public class User {

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
        @NotNull @NotBlank String username,
        @NotNull @NotBlank String password
    ) {
        this.username = requireNonNull(username);
        this.password = requireNonNull(password);
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
}
