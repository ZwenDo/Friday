package com.notkamui.user;

import com.notkamui.utils.SHA512Hasher;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Singleton
public class UserRepositoryImpl implements UserRepository {

    private final EntityManager manager;
    private final SHA512Hasher hasher = SHA512Hasher.getHasher();

    public UserRepositoryImpl(@NotNull EntityManager manager) throws NoSuchAlgorithmException {
        requireNonNull(manager);
        this.manager = manager;
    }

    @Override
    @ReadOnly
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(manager.find(User.class, id));
    }

    @Override
    @Transactional
    public User save(String username, String password) {
        requireNonNull(username);
        requireNonNull(password);
        var hashedPwd = hasher.hash(password);
        var user = new User(username, hashedPwd);
        manager.persist(user);
        return user;
    }

    @Override
    @Transactional
    public boolean deleteById(UUID id, String password) {
        requireNonNull(id);
        requireNonNull(password);
        var hashedPwd = hasher.hash(password);
        var user = findById(id);
        if (user.isEmpty()) return false;
        if (!user.get().password().equals(hashedPwd)) return false;
        manager.remove(user);
        return true;
    }
}
