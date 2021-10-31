package com.kalia.friday.user;

import com.kalia.friday.util.RepositoryResponse;
import com.kalia.friday.util.SHA512Hasher;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
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
     * Used by Micronaut to create a singleton repository by injection.
     *
     * @param manager the injected {@code EntityManager}
     * @throws NoSuchAlgorithmException (should never happen unless SHA-512 doesn't exist anymore)
     */
    public UserRepositoryImpl(@NotNull EntityManager manager) throws NoSuchAlgorithmException {
        this.manager = requireNonNull(manager);
    }

    @Override
    @ReadOnly
    public RepositoryResponse<User> findById(UUID id) {
        requireNonNull(id);
        var user = manager.find(User.class, id);
        return user == null ? RepositoryResponse.notFound() : RepositoryResponse.ok(user);
    }

    @Override
    @ReadOnly
    public RepositoryResponse<User> findByUsername(String username) {
        requireNonNull(username);
        var result = manager
            .createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
            .setParameter("username", username)
            .getResultList();
        if (result.isEmpty()) return RepositoryResponse.notFound();
        return RepositoryResponse.ok(result.get(0));
    }

    @Override
    @Transactional
    public RepositoryResponse<User> save(String username, String password) {
        requireNonNull(username);
        requireNonNull(password);
        var hashedPwd = hasher.hash(password);
        var user = new User(username, hashedPwd);
        manager.persist(user);
        return RepositoryResponse.ok(user);
    }

    @Override
    @Transactional
    public RepositoryResponse<User> deleteById(UUID id, String password) {
        requireNonNull(id);
        requireNonNull(password);
        var response = checkIdentity(id, password);
        if (response.status() == RepositoryResponse.Status.OK) {
            manager.remove(response.get());
        }
        return response;
    }

    @Override
    @Transactional
    public RepositoryResponse<User> updatePassword(UUID id, String oldPassword, String newPassword) {
        requireNonNull(id);
        requireNonNull(oldPassword);
        requireNonNull(newPassword);
        var response = checkIdentity(id, oldPassword);
        if (response.status() == RepositoryResponse.Status.OK) {
            updatePasswordQuery(id, newPassword);
        }
        return response;
    }

    private RepositoryResponse<User> checkIdentity(UUID id, String password) {
        requireNonNull(id);
        requireNonNull(password);
        var user = findById(id);
        if (user.status() == RepositoryResponse.Status.NOT_FOUND) {
            return user;
        }
        var hashedPwd = hasher.hash(password);
        return user.get().password().equals(hashedPwd)
                ? RepositoryResponse.ok(user.get())
                : RepositoryResponse.unauthorized();
    }

    private void updatePasswordQuery(UUID id, String newPassword) {
        var hashedNewPwd = hasher.hash(newPassword);
        manager.createQuery("UPDATE User SET password = :password WHERE id = :id")
                .setParameter("id", id)
                .setParameter("password", hashedNewPwd)
                .executeUpdate();
    }
}
