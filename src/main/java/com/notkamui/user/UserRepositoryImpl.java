package com.notkamui.user;

import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Singleton
public class UserRepositoryImpl implements UserRepository {

    private final EntityManager manager;

    public UserRepositoryImpl(@NotNull EntityManager manager) {
        Objects.requireNonNull(manager);
        this.manager = manager;
    }

    @Override
    @Transactional
    public User save(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        var user = new User(username, password);
        manager.persist(user);
        return user;
    }
}
