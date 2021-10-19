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

/**
 * Implementation of {@code UserRepository} used by Micronaut for dependency injection.
 * (is actually used)
 */
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
        requireNonNull(id);
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
        var user = checkIdentity(id, password);
        if (user.isEmpty()) return false;
        manager.remove(user.get());
        return true;
    }

    @Override
    @Transactional
    public boolean updatePassword(UUID id, String oldPassword, String newPassword) {
        requireNonNull(id);
        requireNonNull(oldPassword);
        requireNonNull(newPassword);
        var user = checkIdentity(id, oldPassword);
        if (user.isEmpty()) return false;
        var hashedNewPwd = hasher.hash(newPassword);
        manager.createQuery("UPDATE User u SET password = :password WHERE id = :id")
            .setParameter("id", id)
            .setParameter("password", hashedNewPwd)
            .executeUpdate();
        return true;
    }

    private Optional<User> checkIdentity(UUID id, String password) {
        requireNonNull(id);
        requireNonNull(password);
        var user = findById(id);
        if (user.isEmpty()) return Optional.empty();
        var hashedPwd = hasher.hash(password);
        return user.get().password().equals(hashedPwd) ? user : Optional.empty();
    }
}
