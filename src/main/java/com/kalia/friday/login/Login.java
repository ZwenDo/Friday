package com.kalia.friday.login;

import com.kalia.friday.user.User;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Represents the {@code Login} table in the database.
 */
@Entity
@Table(name = "login")
@IdClass(LoginId.class)
public class Login {

    /**
     * Necessary empty constructor for Micronaut and JPA.
     */
    public Login() {}

    /**
     * Creates a {@code login} row.
     *
     * @param user the user (id) to create a token for
     * @param lastRefresh the timestamp of the last token refresh
     */
    public Login(@NotNull User user, @NotNull Date lastRefresh) {
        this.user = requireNonNull(user);
        this.lastRefresh = requireNonNull(lastRefresh);
    }

    @Id
    @GeneratedValue
    private UUID token;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Basic(optional = false)
    @Column(name = "last_refresh", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRefresh;

    /**
     * Gets the token of the user session.
     *
     * @return the token of the user session
     */
    public UUID token() {
        return token;
    }

    /**
     * Gets the user (id) of the session.
     *
     * @return the user (id) of the session
     */
    public User user() {
        return user;
    }

    /**
     * Gets the timestamp of the last token refresh.
     *
     * @return the timestamp of the last token refresh
     */
    public Date lastRefresh() {
        return lastRefresh;
    }

    /**
     * Sets the timestamp of the last token refresh.
     *
     * @param lastRefresh the timestamp of the last token refresh
     */
    public void setLastRefresh(Date lastRefresh) {
        this.lastRefresh = requireNonNull(lastRefresh);
    }
}
