package com.kalia.friday.login;

import com.kalia.friday.user.User;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class LoginId implements Serializable {
    private UUID token;
    private User user;

    public LoginId() {}

    public LoginId(UUID token, User user) {
        this.token = requireNonNull(token);
        this.user = requireNonNull(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginId loginId = (LoginId) o;
        return token.equals(loginId.token) && user.equals(loginId.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, user);
    }
}
