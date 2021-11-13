package com.kalia.friday.login;

import com.kalia.friday.user.UserRepository;
import com.kalia.friday.util.RepositoryResponse;
import com.kalia.friday.util.SHA512Hasher;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
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

    @Inject
    private EntityManager manager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private SHA512Hasher hasher;

    @Override
    @ReadOnly
    public RepositoryResponse<List<Login>> allLogins() {
        var result = manager
            .createQuery("SELECT l FROM Login l", Login.class)
            .getResultList();
        result.forEach(it -> manager.detach(it)); // detach before return
        return RepositoryResponse.ok(result);
    }

    @Override
    @Transactional
    public RepositoryResponse<Login> checkIdentity(@NotNull UUID userId, @NotNull UUID token) {
        requireNonNull(userId);
        requireNonNull(token);
        var loginResponse = findLoginByToken(token);
        if (loginResponse.status() != OK ||
            !loginResponse.get().user().id().equals(userId)
        ) {
            return RepositoryResponse.notFound();
        }
        var login = loginResponse.get();
        login.setLastRefresh(LocalDateTime.now());
        manager.flush(); // flush before detach
        manager.detach(login);
        return loginResponse;
    }

    @Override
    @Transactional
    public RepositoryResponse<Login> login(@NotNull String username, @NotNull String password) {
        requireNonNull(username);
        requireNonNull(password);
        var userResponse = userRepository.findByUsername(username);
        if (userResponse.status() != OK ||
            !hasher.hash(password).equals(userResponse.get().password())
        ) {
            return RepositoryResponse.unauthorized();
        }
        var user = userResponse.get();
        var login = new Login(user, LocalDateTime.now());
        user.logins().add(login);
        manager.flush(); // flush before detach
        manager.detach(login);// persists a login in the db
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

    @Override
    @Transactional
    public void purgeExpiredTokens(long lifetime) {
        var logins = allLogins().get();
        var limit = LocalDateTime.now().minusDays(lifetime);
        for (var login : logins) {
            if (login.lastRefresh().isBefore(limit)) {
                logout(login.token());
            }
        }
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
