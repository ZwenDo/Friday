package com.kalia.friday.login;

import com.kalia.friday.user.User;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity
@Table(name = "login")
@IdClass(LoginId.class)
public class Login {
    public Login() {}

    public Login(@NotNull User user, @NotNull Date lastRefresh) {
        this.user = requireNonNull(user);
        this.lastRefresh = requireNonNull(lastRefresh);
    }

    @Id
    @GeneratedValue
    private UUID token;

    @Id
    @ManyToOne(cascade = {CascadeType.ALL})
    private User user;

    @Basic(optional = false)
    @Column(name = "last_refresh", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRefresh;

    public UUID token() {
        return token;
    }

    public User user() {
        return user;
    }

    public Date lastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(Date lastRefresh) {
        this.lastRefresh = requireNonNull(lastRefresh);
    }
}
