package com.notkamui.user;

import com.notkamui.utils.SHA512Hasher;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
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
    public User save(String username, String password) throws NoSuchAlgorithmException {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        var hashedPwd = new SHA512Hasher().hash(password);
        var user = new User(username, hashedPwd);
        manager.persist(user);
        return user;
    }
}
