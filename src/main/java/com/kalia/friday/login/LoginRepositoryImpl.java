package com.kalia.friday.login;

import com.kalia.friday.user.UserRepository;
import com.kalia.friday.util.RepositoryResponse;
import com.kalia.friday.util.SHA512Hasher;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.kalia.friday.util.RepositoryResponse.Status.OK;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@code LoginRepository} used by Micronaut for dependency injection.
 * (is actually used)
 */
@Singleton
public class LoginRepositoryImpl implements LoginRepository {

    private final EntityManager manager;
    private final UserRepository userRepository;
    private final SHA512Hasher hasher = SHA512Hasher.getHasher();

    /**
     * Used by Micronaut to create a singleton repository by injection.
     *
     * @param manager the injected {@code EntityManager}
     * @param userRepository the injected {@code UserRepository}
     */
    public LoginRepositoryImpl(@NotNull EntityManager manager, @NotNull UserRepository userRepository) throws NoSuchAlgorithmException {
        this.manager = requireNonNull(manager);
        this.userRepository = requireNonNull(userRepository);
    }

    @Override
    @ReadOnly
    public RepositoryResponse<List<Login>> allLogins() {
        var result = manager
            .createQuery("SELECT l FROM Login l", Login.class)
            .getResultList();
        return RepositoryResponse.ok(result);
    }

    @Override
    @Transactional
    public RepositoryResponse<Login> checkIdentity(@NotNull UUID userId, @NotNull UUID token) {
        requireNonNull(userId);
        requireNonNull(token);
        var login = findLoginByToken(token);
        if (login.status() != OK ||
            !login.get().user().id().equals(userId)
        ) {
            return RepositoryResponse.notFound();
        }
        login.get().setLastRefresh(LocalDateTime.now());
        return login;
    }

    @Override
    @Transactional
    public RepositoryResponse<Login> login(@NotNull String username, @NotNull String password) {
        requireNonNull(username);
        requireNonNull(password);
        var user = userRepository.findByUsername(username);
        if (user.status() != OK ||
            !hasher.hash(password).equals(user.get().password())
        ) {
            return RepositoryResponse.unauthorized();
        }
        var login = new Login(user.get(), LocalDateTime.now());
        user.get().logins().add(login); // persists a login in the db
        return RepositoryResponse.ok(login);
    }

    @Override
    @Transactional
    public RepositoryResponse<Login> logout(@NotNull UUID token) {
        requireNonNull(token);
        var login = findLoginByToken(token);
        if (login.status() == OK) manager.remove(login.get());
        return login;
    }

    @Override
    @Transactional
    public RepositoryResponse<Integer> logoutAll(@NotNull UUID userId) {
        requireNonNull(userId);
        var user = userRepository.findById(userId);
        if (user.status() != OK) return RepositoryResponse.unauthorized();
        var logins = user.get().logins();
        var size = logins.size();
        var iterator = logins.iterator();
        iterator.forEachRemaining(l -> {
            iterator.remove();
            manager.remove(l);
        }); // clears all the logins by two-ways ManyToOne binding
        return RepositoryResponse.ok(size);
    }

    private RepositoryResponse<Login> findLoginByToken(UUID token) {
        requireNonNull(token);
        var result = manager
            .createQuery("SELECT l FROM Login l WHERE l.token = :token", Login.class)
            .setParameter("token", token)
            .getResultList();
        if (result.isEmpty()) return RepositoryResponse.unauthorized();
        return RepositoryResponse.ok(result.get(0));
    }
}
