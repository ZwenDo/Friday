package com.notkamui.user;

import com.notkamui.utils.RepositoryResponseStatus;
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

    /**
     * Used by Micronaut to create a singleton repository by injection
     *
     * @param manager the injected {@code EntityManager}
     * @throws NoSuchAlgorithmException (should never happen unless SHA-512 doesn't exist anymore)
     */
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
    public RepositoryResponseStatus deleteById(UUID id, String password) {
        requireNonNull(id);
        requireNonNull(password);
        var status = checkIdentity(id, password);
        return switch (status) {
            case RepositoryResponseStatus.Ok user -> {
                manager.remove(user.response());
                yield user;
            }
            case RepositoryResponseStatus.NotFound notFound -> notFound;
            case RepositoryResponseStatus.Unauthorized unAuth -> unAuth;
        };
    }

    @Override
    @Transactional
    public RepositoryResponseStatus updatePassword(UUID id, String oldPassword, String newPassword) {
        requireNonNull(id);
        requireNonNull(oldPassword);
        requireNonNull(newPassword);
        var status = checkIdentity(id, oldPassword);
        return switch (status) {
            case RepositoryResponseStatus.Ok user -> {
                updatePasswordQuery(id, newPassword);
                yield user;
            }
            case RepositoryResponseStatus.NotFound notFound -> notFound;
            case RepositoryResponseStatus.Unauthorized unAuth -> unAuth;
        };
    }

    private RepositoryResponseStatus checkIdentity(UUID id, String password) {
        requireNonNull(id);
        requireNonNull(password);
        var user = findById(id);
        if (user.isEmpty()) return new RepositoryResponseStatus.NotFound();
        var hashedPwd = hasher.hash(password);
        return user.get().password().equals(hashedPwd)
            ? new RepositoryResponseStatus.Ok<>(user.get())
            : new RepositoryResponseStatus.Unauthorized();
    }

    private void updatePasswordQuery(UUID id, String newPassword) {
        var hashedNewPwd = hasher.hash(newPassword);
        manager.createQuery("UPDATE User SET password = :password WHERE id = :id")
            .setParameter("id", id)
            .setParameter("password", hashedNewPwd)
            .executeUpdate();
    }
}
