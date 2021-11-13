package com.kalia.friday.user;

import com.kalia.friday.util.RepositoryResponse;
import com.kalia.friday.util.SHA512Hasher;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@code UserRepository} used by Micronaut for dependency injection.
 * (is actually used)
 */
@Singleton
public class UserRepositoryImpl implements UserRepository {

    @Inject
    private EntityManager manager;

    @Inject
    private SHA512Hasher hasher;

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
            var hashed = hasher.hash(newPassword);
            response.get().setPassword(hashed);
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
}
