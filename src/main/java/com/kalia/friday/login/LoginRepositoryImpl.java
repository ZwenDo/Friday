package com.kalia.friday.login;

import com.kalia.friday.user.UserRepository;
import com.kalia.friday.util.RepositoryResponse;
import com.kalia.friday.util.SHA512Hasher;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.kalia.friday.util.RepositoryResponse.Status.NOT_FOUND;
import static java.util.Objects.requireNonNull;

@Singleton
public class LoginRepositoryImpl implements LoginRepository {

    private final EntityManager manager;
    private final UserRepository userRepository;
    private final SHA512Hasher hasher = SHA512Hasher.getHasher();

    public LoginRepositoryImpl(EntityManager manager, UserRepository userRepository) throws NoSuchAlgorithmException {
        this.manager = requireNonNull(manager);
        this.userRepository = requireNonNull(userRepository);
    }

    @Override
    @Transactional
    public RepositoryResponse<Login> checkIdentity(UUID userId, UUID token) {
        var user = userRepository.findById(userId);
        if (user.status() == NOT_FOUND) return RepositoryResponse.notFound();
        var login = manager.find(Login.class, new LoginId(token, user.get()));
        if (login == null) return RepositoryResponse.unauthorized();
        login.setLastRefresh(new Date());
        return RepositoryResponse.ok(login);
    }

    @Override
    @Transactional
    public RepositoryResponse<Login> login(String username, String password) {
        var user = userRepository.findByUsername(username);
        if (user.status() == NOT_FOUND) return RepositoryResponse.notFound();
        if (!hasher.hash(password).equals(user.get().password())) return RepositoryResponse.unauthorized();
        var login = new Login(user.get(), new Date());
        manager.persist(login);
        return RepositoryResponse.ok(login);
    }

    @Override
    @Transactional
    public RepositoryResponse<Login> logout(UUID token) {
        var login = findLoginByToken(token);
        if (login.status() == NOT_FOUND) return login;
        manager.remove(login.get());
        return RepositoryResponse.ok(login.get());
    }

    @Override
    @Transactional
    public RepositoryResponse<List<Login>> logoutAll(UUID userId) {
        var logins = findLoginsByUserId(userId);
        if (logins.status() == NOT_FOUND) return logins;
        logins.get().forEach(manager::remove);
        return RepositoryResponse.ok(logins.get());
    }

    @Transactional
    private RepositoryResponse<Login> findLoginByToken(UUID token) {
        var result = manager
            .createQuery("SELECT l FROM Login l WHERE l.token = :token", Login.class)
            .setParameter("token", token)
            .getResultList();
        if (result.isEmpty()) return RepositoryResponse.notFound();
        return RepositoryResponse.ok(result.get(0));
    }

    @Transactional
    private RepositoryResponse<List<Login>> findLoginsByUserId(UUID userId) {
        var user = userRepository.findById(userId);
        if (user.status() == NOT_FOUND) return RepositoryResponse.notFound();
        var result = manager
            .createQuery("SELECT l FROM Login l WHERE l.user = :user", Login.class)
            .setParameter("user", userId)
            .getResultList();
        return RepositoryResponse.ok(result);
    }
}
