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
        if (user == null) return RepositoryResponse.notFound();
        manager.detach(user); // detach before return
        return RepositoryResponse.ok(user);
    }

    @Override
    @ReadOnly
    public RepositoryResponse<User> findByUsername(String username) {
        requireNonNull(username);
        var result = manager
                .createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
        if (result.isEmpty()) return RepositoryResponse.notFound();
        manager.detach(result.get()); // detach before return
        return RepositoryResponse.ok(result.get());
    }

    @Override
    @Transactional
    public RepositoryResponse<User> save(String username, String password) {
        requireNonNull(username);
        requireNonNull(password);
        var hashedPwd = hasher.hash(password);
        var user = new User(username, hashedPwd);
        manager.persist(user);
        manager.flush();
        manager.detach(user);
        return RepositoryResponse.ok(user);
    }

    @Override
    @Transactional
    public RepositoryResponse<User> deleteById(UUID id, String password) {
        requireNonNull(id);
        requireNonNull(password);
        var getResponse = checkIdentity(id, password);
        if (getResponse.status() == RepositoryResponse.Status.OK) {
            manager.remove(getResponse.get());
        }
        return getResponse;
    }

    @Override
    @Transactional
    public RepositoryResponse<User> updatePassword(UUID id, String oldPassword, String newPassword) {
        requireNonNull(id);
        requireNonNull(oldPassword);
        requireNonNull(newPassword);
        var response = checkIdentity(id, oldPassword);
        if (response.status() != RepositoryResponse.Status.OK) return response;
        var hashed = hasher.hash(newPassword);
        var user = response.get();
        user.setPassword(hashed);
        manager.flush();
        manager.detach(user);
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
            ? RepositoryResponse.ok(manager.merge(user.get()))
            : RepositoryResponse.unauthorized();
    }
}
